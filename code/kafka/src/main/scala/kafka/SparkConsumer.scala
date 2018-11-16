package kafka

import org.apache.log4j.{BasicConfigurator, Level, Logger}

import scala.collection.JavaConversions.mapAsScalaMap
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe

object SparkConsumer extends App {
  BasicConfigurator.configure()
  Logger.getRootLogger.setLevel(Level.ERROR)

  val conf = new SparkConf().setAppName("Spark streaming example").setMaster("local[*]")
  val ssc = new StreamingContext(conf, Seconds(10))

  val kafkaProps = new KafkaLocalConfig().getConsumerProps
  val kafkaPropsMap = mapAsScalaMap(kafkaProps.asInstanceOf[java.util.Map[String, Object]])

  val topics = Array("testTopic")
  val stream = KafkaUtils.createDirectStream[String, String](
    ssc,
    PreferConsistent,
    Subscribe[String, String](topics, kafkaPropsMap)
  )

  stream.map(t => t.value())
    .window(Seconds(40), Seconds(10))
    .flatMap(_.split("\\s+"))
    .countByValue()
    .foreachRDD(rdd => {
      println(rdd.sortBy(kv => -kv._2).take(5).toList)
    })

  ssc.start()
  ssc.awaitTermination()
}
