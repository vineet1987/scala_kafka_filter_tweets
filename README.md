# scala_kafka_filter_tweets

Thanks to Stephane Maarek and his course on Apache Kafka Series - Learn Apache Kafka for Beginners v2, taking inspiration from the tutorial which was in Java, I have tried my best to write the Kafka Streams part of it in Scala. His courses are really good and give you a good headstart. Stephane's github profile: https://github.com/simplesteph

Language used: Scala version: 2.13.0
Build Tool: sbt
IDE used: IntelliJ
kafka (You can download from https://kafka.apache.org/downloads) and unzip the same on your local machine.
OS used: MAC OS (Preferably avoid Windows. If you have windows OS try setting up a Linux VM on Windows OS)

Pre-requisites:
You need to have a twitter developer account. Once you create a developer account and it is approved you can create an app and copy/generate the below:
1) Consumer API Key
2) Consumer API secret Key
3) Access Token
4) Access Token Secret

Replace all the above secrets in the class com.github.vku.kafka.producer.TwitterProducer inside the run() method

This project makes use of Kafka Streams where we read twitter tweets from one topic ("twitter_tweets"), do filtering on the basis of "followers_count" (number of followers should be > 10000) of user who has tweeted and then publish the same into another topic("important_tweets"). 

In order to get tweets into the "twitter_tweets" topic we make use of Twitter Streaming Client [https://github.com/twitter/hbc]

Using our own Kafka producer we will send tweets received from the Twitter Streaming client into the "twitter_tweets" topic.
Kafka Streams will then read tweets from the topic "twitter_tweets", do some filtering and then send them into "important_tweets"
Using our own Kafka consumer we will see the important tweets displayed on the console.

<b>How this works:</b>

<b>Step 1:Start a ZooKeeper Instance</b><p>
From the kafka folder you have setup we will start a zookeper instance. There is a bin folder which has various shell scripts.
In order to start a zookeper instance we will fire : "./bin/zookeeper-server-start.sh config/zookeeper.properties" .
Note: If you have a windows OS you will find a .bat file inside bin/windows folder.</p>

<b>Step 2:</b><p>Start a Kafka Broker Instance<br>
"./bin/kafka-server-start.sh config/server.properties"
</p>

<b>Step 3:</b>Create two topics with replication-factor of 1 and number of partitions per topic = 3
<br><p>
a) twitter_tweets => <br>
/bin/kafka-topics.sh --bootstrap-server localhost:9092 --topic twitter_tweets --replication-factor 1 --partitions 3 --create
<br>
b) important_tweets => <br>
/bin/kafka-topics.sh --bootstrap-server localhost:9092 --topic important_tweets --replication-factor 1 --partitions 3 --create
</p>

<b>Step 4:</b><p>
Run our custom producer "TwitterProducer" which is a scala class that will process tweets being streamed from the Twitter Streaming Client and send them into the "twitter_tweets" topic
</p>
<b>
Step 5:</b><p>
This step is not necessary but in order to validate the output we do the same.
Run our custom consumer "AllTweetsConsumer" which is a scala class that subscribes to the "twitter_tweets" topic and displays the same on the console.</p><br>
<b>
Step 6:</b><p><br>
Run our custom consumer "ImportantTweetsConsumer" which is a scala class that subscribes to the "important_tweets" topic and displays tweets filtered by our own kafka streams class "StreamsFilterTweets" and displays the same on the console. At this point no tweets will be published as we are yet to activate our own KafkaStreams class which is the next step
</p><br>
<b>
Step 7:</b><p><br>
Run the custom streams class "StreamsFilterTweets" which will filter all tweets received from the "twitter_tweets" topic, filter them on the basis of number of followers of user tweeting and then publish them into the "important_tweets" topic
</p><br>
After running the above steps if you check the console of the custom Consumer class "ImportantTweetsConsumer" you will see only important tweets as compared to the console of the "AllTweetsConsumer" which displays all tweets.
