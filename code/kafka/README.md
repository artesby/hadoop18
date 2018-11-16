## Открытие проекта

Проект проще всего открыть с помощью Intellij IDEA. Предварительно нужно установить Scala-плагин, затем `File -> New -> Project from Existing Sources`,  выбрать директорию с проектом, выбрать `sbt` и импортировать. 

Небольшой проект, пример использования [VK Streaming API](https://vk.com/dev/streaming_api),
`Apache Kafka` и `Spark Streaming`.

С помощью `VK Streaming` можно в реальном времени получать сообщения в `VK` по каким-то ключевым словам. Всё это попадает в `Каfka`, оттуда в `Spark Streaming`, где каждые 40 секунд с перекрытием в 10 секунд считается статистика самых популярных слов в сообщениях. 

`KafkaLocal.scala` - тут содержится код для запуска `Kafka` на локальном машине и всего сопутствующего зоопарка в одной `JVM`.

`SimpleConsumer.scala` - пример реализации простого потребителя

`SimpleProducer.scala` - пример реализации простого производителя

`VkProducer.scala` - работа с `VK Streaming`, всё что получается - отправляется в `Kafka`.

`SparkConsumer.scala` - работа с `Spark Streaming`


Для запуска нужно [создать](https://vk.com/editapp?act=create) своё приложение в `VK`. Нужно выбрать standalone-приложение и получить `appId` и `secretKey`. Эти данные нужно записать в файл 

> src/main/resources/vk_credentials.conf

Затем запускаем `KafkaLocal.scala`, смотрим что всё работает. Потом `SimpleConsumer.scala`, `SimpleProducer.scala` - смотрим, что consumer напечатал полученные сообщения. Теперь уже можно запустить `VkProducer.scala` и `SparkConsumer.scala`.

