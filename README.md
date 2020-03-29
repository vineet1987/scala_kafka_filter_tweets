# scala_kafka_filter_tweets
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

This project makes use of Kafka Streams where we read twitter tweets from one topic ("twitter_tweets"), do filtering on the basis of "followers_count" (number of followers should be > 10000) of user who has tweeted and then publish the same into another topic("important_tweets"). 

In order to get tweets into the "twitter_tweets" topic we make use of Twitter Streaming Client [https://github.com/twitter/hbc]

Using our own Kafka producer we will send tweets received from the Twitter Streaming client into the "twitter_tweets" topic.
Kafka Streams will then read tweets from the topic "twitter_tweets", do some filtering and then send them into "important_tweets"
Using our own Kafka consumer we will see the important tweets displayed on the console.

How this works:

Step 1: Start a ZooKeeper Instance
From the kafka folder you have setup we will start a zookeper instance. There is a bin folder which has various shell scripts.
In order to start a zookeper instance we will fire : "./bin/zookeeper-server-start.sh config/zookeeper.properties" .
Note: If you have a windows OS you will find a .bat file inside bin/windows folder.

Step 2: Start a Kafka Broker Instance
"./bin/kafka-server-start.sh config/server.properties"

Step 3: Create two topics with replication-factor of 1 and number of partitions per topic = 3
a) twitter_tweets
/bin/kafka-topics.sh --bootstrap-server localhost:9092 --topic twitter_tweets --replication-factor 1 --partitions 3 --create
b) important_tweets
/bin/kafka-topics.sh --bootstrap-server localhost:9092 --topic important_tweets --replication-factor 1 --partitions 3 --create

Step 4:
Run our custom producer "TwitterProducer" which is a scala class that will process tweets being streamed from the Twitter Streaming Client and send them into the "twitter_tweets" topic

Step 5:
This step is not necessary but in order to validate the output we do the same.
Run our custom consumer "AllTweetsConsumer" which is a scala class that subscribes to the "twitter_tweets" topic and displays the same on the console.

Step 6:
Run our custom consumer "ImportantTweetsConsumer" which is a scala class that subscribes to the "important_tweets" topic and displays tweets filtered by our own kafka streams class "StreamsFilterTweets" and displays the same on the console. At this point no tweets will be published as we are yet to activate our own KafkaStreams class which is the next step

Step 5:
Run the custom streams class "StreamsFilterTweets" which will filter all tweets received from the "twitter_tweets" topic, filter them on the basis of number of followers of user tweeting and then publish them into the "important_tweets" topic

After running the above steps if you check the console of the custom Consumer class "ImportantTweetsConsumer" you will see only important tweets as compared to the console of the "AllTweetsConsumer" which displays all tweets.
