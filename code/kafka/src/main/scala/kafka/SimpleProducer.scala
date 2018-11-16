package kafka

import java.util.{Date, Properties}

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

class SimpleProducer(val props: Properties, val topic: String) {
  def start(): Unit = {
    new Thread(new Runnable {
      override def run(): Unit = {
        val producer = new KafkaProducer[String, String](props)
        for (idx <- Range(0, 10)) {
          val date = new Date()
          val msg = s"$idx, $date"
          val data = new ProducerRecord[String, String](topic, "text", msg)
          producer.send(data)
        }

        producer.close()
      }
    }).start()
  }
}


object SimpleProducer {
  def main(args: Array[String]): Unit = {
    val props = new KafkaLocalConfig().getConsumerProps
    new SimpleProducer(props, "testTopic").start()
  }
}
