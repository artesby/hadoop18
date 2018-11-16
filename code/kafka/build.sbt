name := "kafka-vk"

version := "0.1"

scalaVersion := "2.11.11"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.11",
  "org.apache.spark" %% "spark-core" % "2.3.0",
  "org.apache.spark" %% "spark-sql" % "2.3.0",
  "org.apache.spark" %% "spark-streaming" % "2.3.0",
  "org.apache.spark" %% "spark-streaming-kafka-0-10" % "2.3.0",
  "org.apache.kafka"  %% "kafka" % "0.10.1.1",
  "org.apache.logging.log4j" % "log4j-core" % "2.11.0",
  "com.vk.api" % "sdk" % "0.5.12"
)