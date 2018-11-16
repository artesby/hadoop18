package kafka

import java.util.Properties

import org.apache.kafka.clients.consumer.{ConsumerRecord, ConsumerRecords, KafkaConsumer}
import org.apache.kafka.common.TopicPartition

import scala.collection.JavaConversions._

class SimpleConsumer(val props: Properties, val topic: String) {
  def start(): Unit = {

    val thread = new Thread(new Runnable {
      override def run(): Unit = {
        val consumer = new KafkaConsumer[String, String](props)
        consumer.subscribe(java.util.Collections.singletonList(topic))

        while (true) {
          val records: ConsumerRecords[String, String] = consumer.poll(1000)

          for (record <- records) {
            System.out.println("Message: (" + record.key() + ", " + record.value() + ") at offset " + record.offset())
          }
        }
      }
    })

    thread.start()
  }
}

object SimpleConsumer {
  def main(args: Array[String]): Unit = {
    val props = new KafkaLocalConfig().getConsumerProps
    new SimpleConsumer(props, "testTopic").start()
  }
}