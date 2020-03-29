name := "ScalaWithKafka"
version := "0.1"
scalaVersion := "2.13.0"

//Kafka dependency for kafka consumer and kafka producer
libraryDependencies += "org.apache.kafka" % "kafka-clients" % "2.4.1"

//Kafka dependency for kafka streams
libraryDependencies += "org.apache.kafka" % "kafka-streams" % "2.4.1"

//Logging dependency for information
libraryDependencies += "org.slf4j" % "slf4j-simple" % "1.7.25"

//Json parser for parsing tweets 
libraryDependencies += "com.google.code.gson" % "gson" % "2.8.5"

//Twitter Streaming client
libraryDependencies += "com.twitter" % "hbc-core" % "2.2.0"
