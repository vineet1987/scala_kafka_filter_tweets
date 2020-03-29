package com.github.vku.kafka.consumer

import java.time.Duration
import java.util.{Arrays, Properties}

import org.apache.kafka.clients.consumer.{ConsumerConfig, KafkaConsumer}
import org.apache.kafka.common.serialization.StringDeserializer

object ImportantTweetsConsumer {

  def createConsumer(): KafkaConsumer[String,String] = {
    val props = new Properties()
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092")
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"latest")
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,classOf[StringDeserializer].getName)
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,classOf[StringDeserializer].getName)
    props.put(ConsumerConfig.GROUP_ID_CONFIG,"my-twitter-application")
    val consumer = new KafkaConsumer[String,String](props)
    consumer.subscribe(Arrays.asList("important_tweets"))
    return consumer
  }

  def main(args:Array[String]): Unit ={
    //
    val consumer = createConsumer()

    //start polling
    while(true) {
      val consumerRecords = consumer.poll(Duration.ofSeconds(1))
      consumerRecords.forEach(cr => {
        println("\nNew Message==="+cr.value()+"========\n")
      })
    }
  }


}
