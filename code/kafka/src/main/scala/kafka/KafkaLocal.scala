package kafka


import java.io.{File, IOException}
import java.nio.file.{Files, Path, Paths}
import java.util.Properties

import kafka.KafkaLocal._
import kafka.server.KafkaServerStartable
import org.apache.commons.io.FileUtils
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.log4j.{BasicConfigurator, Level, Logger}
import org.apache.zookeeper.server.quorum.QuorumPeerConfig
import org.apache.zookeeper.server.{ServerConfig, ZooKeeperServerMain}

class ZooKeeperLocal(val zkProperties: Properties)  {
  def start(): Unit = {
    val quorumConfiguration = new QuorumPeerConfig

    quorumConfiguration.parseProperties(zkProperties)

    val zooKeeperServer = new ZooKeeperServerMain
    val configuration = new ServerConfig
    configuration.readFrom(quorumConfiguration)

    new Thread() {
      override def run(): Unit = {
        try
          zooKeeperServer.runFromConfig(configuration)
        catch {
          case e: IOException =>
            e.printStackTrace(System.err)
        }
      }
    }.start()
  }
}


class KafkaLocal(val cfg: KafkaLocalConfig) {
  def start(): Unit = {
    val zoo = new ZooKeeperLocal(cfg.getZookeeperProperties)
    zoo.start()

    val kafka = KafkaServerStartable.fromProps(cfg.getKafkaProperties)
    kafka.startup()
  }
}


class KafkaLocalConfig(val port: Int = BROKER_PORT,
                       val zkPort: Int = ZOOKEEPER_PORT,
                       val logDir: Path = Paths.get("/tmp")) {

  private val kLogDir = logDir + "/kafka"
  private val zkLogDir = logDir + "/zookeeper"

  def getConsumerProps: Properties = {
    val props = new Properties

    {
      import ConsumerConfig._
      props.put(ENABLE_AUTO_COMMIT_CONFIG, "true")
      props.put(AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000")
      props.put(SESSION_TIMEOUT_MS_CONFIG, "30000")
      props.put(GROUP_ID_CONFIG, "1")
      props.put(KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
      props.put(VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
      props.put(BOOTSTRAP_SERVERS_CONFIG, s"localhost:$port")
    }

    {
      import ProducerConfig._
      props.put(KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
      props.put(VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
    }
    props
  }

  def getKafkaProperties = {
    val props = new Properties
    props.put("port", port + "")
    props.put("broker.id", "0")
    props.put("log.dir", kLogDir)
    props.put("zookeeper.connect", s"localhost:$zkPort")
    props.put("default.replication.factor", "1")
    props.put("offsets.topic.replication.factor", "1")
    props.put("delete.topic.enable", "true")
    props
  }

  def getZookeeperProperties = {
    val props = new Properties
    props.put("clientPort", zkPort + "")
    props.put("dataDir", zkLogDir)
    props
  }
}

object KafkaLocal {
  val BROKER_PORT = 5000
  val ZOOKEEPER_PORT = 2000


  def runKafka(logDir: Path): KafkaLocal = {
    val cfg = new KafkaLocalConfig(logDir=logDir)
    val kafka = new KafkaLocal(cfg)
    kafka.start()

    kafka
  }

  def deleteRecursively(file: File): Unit = {
    if (file.isDirectory)
      file.listFiles.foreach(deleteRecursively)
  }

  def main(args: Array[String]): Unit = {
    BasicConfigurator.configure()
    Logger.getRootLogger.setLevel(Level.INFO)

    val tmpFolder = Files.createTempDirectory("kafka")
    FileUtils.forceDeleteOnExit(tmpFolder.toFile)

    runKafka(tmpFolder)
  }
}
