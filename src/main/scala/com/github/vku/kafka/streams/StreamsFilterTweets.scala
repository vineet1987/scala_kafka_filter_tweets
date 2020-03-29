package com.github.vku.kafka.streams

import java.util.Properties

import com.google.gson.JsonParser
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.{KafkaStreams, StreamsBuilder, StreamsConfig}

object StreamsFilterTweets {

  val parser = new JsonParser()

  def extractUserFollowerInTweet(jsonTweet:String): Int ={
    try {
      return parser.parse(jsonTweet).getAsJsonObject.get("user")
        .getAsJsonObject.get("followers_count").getAsInt
    }
    catch{
      case e:NullPointerException => {
        return 0
      }
    }
  }

  def filterTweets(inputTopic: KStream[String, String]): Unit = {
    val filteredStream = inputTopic.filter( (k,jsonTweet) => {
      extractUserFollowerInTweet(jsonTweet) > 10000
    })
    filteredStream.to("important_tweets")
  }

  def main(args:Array[String]): Unit ={
    var props = new Properties()
    props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092")
    props.put(StreamsConfig.APPLICATION_ID_CONFIG,"demo-kafka-streams")
    props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG,classOf[Serdes.StringSerde].getName)
    props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG,classOf[Serdes.StringSerde].getName  )
    val builder = new StreamsBuilder
    filterTweets(builder.stream("twitter_tweets"))
    val kafkaStreams = new KafkaStreams(builder.build(),props)
    kafkaStreams.start()
  }
}
