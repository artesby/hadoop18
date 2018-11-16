package kafka

import java.util
import java.util.{Date, List, Properties}

import com.typesafe.config.ConfigFactory
import com.vk.api.sdk.client.VkApiClient
import com.vk.api.sdk.client.actors.ServiceActor
import com.vk.api.sdk.httpclient.HttpTransportClient
import com.vk.api.sdk.streaming.clients.actors.StreamingActor
import com.vk.api.sdk.streaming.clients.{StreamingEventHandler, VkStreamingApiClient}
import com.vk.api.sdk.streaming.objects.{StreamingCallbackMessage, StreamingRule}
import com.vk.api.sdk.streaming.objects.responses.{StreamingGetRulesResponse, StreamingResponse}
import kafka.VkProducer.{actor, streamingClient}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.log4j.{BasicConfigurator, Level, Logger}

class VkProducer(val props: Properties, val topic: String) {
  def start(): Unit = {
    new Thread(new Runnable {
      override def run(): Unit = {
        val producer = new KafkaProducer[String, String](props)

        streamingClient.stream.get(actor, new StreamingEventHandler() {
          override def handle(message: StreamingCallbackMessage): Unit = {
            System.out.println(message)
          }
        }).execute


        producer.close()
      }
    }).start()
  }
}

object VkProducer {

  val transportClient = new HttpTransportClient
  val vkClient = new VkApiClient(transportClient)
  val streamingClient = new VkStreamingApiClient(transportClient)

  val vkCredentials = ConfigFactory.load("vk_credentials.conf")

  val servActor = new ServiceActor(vkCredentials.getInt("app_id"),
                                   vkCredentials.getString("access_token"))

  val serverUrlResponse = vkClient.streaming.getServerUrl(servActor).execute
  val actor = new StreamingActor(serverUrlResponse.getEndpoint, serverUrlResponse.getKey)


  def listen(): Unit = {
    streamingClient.stream.get(actor, new StreamingEventHandler() {
      override def handle(message: StreamingCallbackMessage): Unit = {
        System.out.println(message)
      }
    }).execute
  }

  def listenKafka(props: Properties, topic: String): Unit = {
    val producer = new KafkaProducer[String, String](props)

    streamingClient.stream.get(actor, new StreamingEventHandler() {
      override def handle(msg: StreamingCallbackMessage): Unit = {
        System.out.println(msg)

        val data = new ProducerRecord[String, String](topic, "VK", msg.getEvent.getText)
        producer.send(data)
      }
    }).execute
  }

  def addRule(tag: String, value: String): Unit = {
     streamingClient.rules.add(actor, tag, value).execute[StreamingResponse]()
  }

  def getRules: util.List[StreamingRule] = {
    val resp = streamingClient.rules.get(actor).execute[StreamingGetRulesResponse]
    resp.getRules
  }

  def main(args: Array[String]): Unit = {
    BasicConfigurator.configure()
    Logger.getRootLogger.setLevel(Level.ERROR)

    if(getRules.isEmpty) {
        addRule("0", "привет питер кот мэр собака")
    }
    listenKafka(new KafkaLocalConfig().getConsumerProps, "testTopic")
  }
}
