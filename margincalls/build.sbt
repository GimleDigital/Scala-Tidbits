name := "margincalls"
organization := "com.gimledigital"
version := "0.1-SNAPSHOT"
scalaVersion := "2.11.12"

val sparkVersion = "2.4.4"

resolvers += "bintray-spark-packages" at "https://dl.bintray.com/spark-packages/maven/"
resolvers += "Typesafe Simple Repository" at "https://repo.typesafe.com/typesafe/simple/maven-releases/"
resolvers += "MavenRepository" at "https://mvnrepository.com/"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-sql" % sparkVersion
)
