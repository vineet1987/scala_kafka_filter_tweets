  package com.github.vku.kafka.producer

import java.util
import java.util.Properties
import java.util.concurrent.{BlockingQueue, LinkedBlockingQueue, TimeUnit}

import com.twitter.hbc.ClientBuilder
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint
import com.twitter.hbc.core.processor.StringDelimitedProcessor
import com.twitter.hbc.core.{Client, Constants, HttpHosts}
import com.twitter.hbc.httpclient.auth.{Authentication, OAuth1}
import org.apache.kafka.clients.producer._
import org.apache.kafka.common.serialization.StringSerializer

  object TwitterProducer {

    def main(args:Array[String]): Unit ={
        run();
    }

    def run(): Unit = {

      val twitterTweetsTopic = "twitter_tweets"
      val importantTweetsTopic = "important_tweets"
      val consumerApiKey = ""
      val consumerApiSecret = ""
      val accessToken = ""
      val accessTokenSecret = ""
      val hashTagsToStream = util.Arrays.asList("#bitcoin")

      val msgQueue: BlockingQueue[String] = new LinkedBlockingQueue[String](1000)
      //create twitter client
      val hoseBirdClient: Client = createTwitterClient(msgQueue,consumerApiKey,consumerApiSecret,accessToken,
                                    accessTokenSecret,hashTagsToStream)
      hoseBirdClient.connect()

      //create a kafka producer
      val producer:KafkaProducer[String,String] = createKafkaProducer()
      addShutDownHook(hoseBirdClient,producer)

      //loop to send data to kafka
      while (!hoseBirdClient.isDone()) {
        var msg:String = null
        try {
          msg = msgQueue.poll(5, TimeUnit.SECONDS)
        }
        catch {
          case ex: InterruptedException => {
            ex.printStackTrace()
            hoseBirdClient.stop()
          }
        }
        if (msg != null) {
          val producerRecord:ProducerRecord[String,String] = new ProducerRecord[String,String](twitterTweetsTopic,null,msg)
          producer.send(producerRecord, new Callback {
            override def onCompletion(metadata: RecordMetadata, exception: Exception): Unit = {
              if(exception!=null){
                println("Something bad happened")
              }
            }
          })
        }
      }
    }

    private def addShutDownHook(hoseBirdClient:Client,producer:KafkaProducer[String,String]) = {
      Runtime.getRuntime.addShutdownHook(new Thread(() => {
        println("Shutting down app")
        hoseBirdClient.stop()
        producer.close()
      }))
    }

    def createTwitterClient(msgQueue:BlockingQueue[String], consumerKey:String, consumerSecret:String,
                            accessToken:String,accessTokenSecret:String,
                            hashTagsToStream:util.Collection[String]): Client ={


      /** Declare the host you want to connect to, the endpoint, and authentication (basic auth or oauth) */
      val hosebirdHosts:HttpHosts = new HttpHosts(Constants.STREAM_HOST)
      val hosebirdEndpoint:StatusesFilterEndpoint = new StatusesFilterEndpoint()
      val terms: util.ArrayList[String] = new util.ArrayList[String]()
      terms.addAll(hashTagsToStream)
      hosebirdEndpoint.trackTerms(terms)

      val hoseBirdAuth:Authentication = new OAuth1(consumerKey,consumerSecret,
        accessToken,accessTokenSecret);

      val builder:ClientBuilder = new ClientBuilder().name("HBC-1")
              .hosts(hosebirdHosts)
              .endpoint(hosebirdEndpoint)
              .authentication(hoseBirdAuth)
              .processor(new StringDelimitedProcessor(msgQueue))

      val hoseBirdClient:Client = builder.build()
      return hoseBirdClient
    }

    def createKafkaProducer(): KafkaProducer[String, String] = {
        val properties:Properties = new Properties()
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092")
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,classOf[StringSerializer].getName)
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,classOf[StringSerializer].getName)

        //safe producer
        properties.put(ProducerConfig.ACKS_CONFIG,"all")
        properties.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG,"true")
        properties.put(ProducerConfig.RETRIES_CONFIG,Integer.toString(Integer.MAX_VALUE))
        properties.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION,"5")
        properties.put(ProducerConfig.LINGER_MS_CONFIG,"20")
        properties.put(ProducerConfig.COMPRESSION_TYPE_CONFIG,"snappy")
        properties.put(ProducerConfig.BATCH_SIZE_CONFIG,Integer.toString(32*1024)) //32KB
        val producer: KafkaProducer[String,String] = new KafkaProducer[String, String](properties)
        return producer
    }

  }
