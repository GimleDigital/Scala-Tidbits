package com.gimledigital.margincalls

import scala.collection.mutable.{ListBuffer}

import org.apache.commons.math3.random.MersenneTwister
import org.apache.log4j.{Level, Logger}

import org.apache.spark.sql.expressions.UserDefinedFunction
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._

/** A simple example app that generates a random sample of equity option deals.
 *
 *  The input files should have been generated previously by the application
 *  DataCleaner, also provided in this package.
 */

object DealSampler extends App {
  // Initializing the program and the log
  val simpleLog = new SimpleLog("DealSampler")
  simpleLog.beginProgram

  // Dates and horizons, should be equal for all applications in this package
  val today = "2019-09-27"
  val yesterday =  "2019-09-26"
  val historical = "2019-03-26"
  val volObs = 100 // Number of observations used for estimating volatility

  // Sample size
  val numberOfTrades = 1000000
  val numberOfCptys = 1000

  // Apache Spark configuration
  val sparkEnv = "local"
  val sparkName = "Create Sample Data"

  // Filters
  val equities = Array("ERIBR", "NOKIA", "SAMPO", "STE-R", "TELIA1", "TIETO")

  // Input file names (e.g. directories where Spark will read partial files)
  val inputPath = "data/"
  val instrumentsDir = s"${inputPath}instruments.csv"
  val pricesDirs = equities.map(inputPath + _ + "-prices.csv")

  // Output file names (e.g. directories where Spark will write partial files)
  val outputPath = "data/"
  val dealsDir = s"${outputPath}deals"

  // Setting log levels to reduce verbose outputs in console
  val logLevel = Level.ERROR

  val rootLogger = Logger.getRootLogger()
  rootLogger.setLevel(logLevel)

  Logger.getLogger("org.apache.spark").setLevel(logLevel)
  Logger.getLogger("org.spark-project").setLevel(logLevel)
  Logger.getLogger("org.apache.hadoop").setLevel(logLevel)
  Logger.getLogger("io.netty.util").setLevel(logLevel)

  // Starting Apache Spark
  simpleLog.writeMsg("Starting the Apache Spark session")

  val spark = SparkSession.builder().master(sparkEnv).appName(sparkName)
    .getOrCreate()

  // Loading input files
  simpleLog.writeMsg("Loading input files")

  val instrumentsDF = spark.read.format("csv")
    .option("header", "true")
    .option("delimiter", ";")
    .option("mode", "DROPMALFORMED")
    .option("dateformat", "yyyy-MM-dd")
    .load(instrumentsDir)

  instrumentsDF.cache()

  /** In this section we build the random sample of deals in the given set
   *  of instruments and between the given number of counterparts.
   */
  val numberOfInstruments = instrumentsDF.count()

  val mt = new MersenneTwister

  /** Returns a random id that is not equal to some other id.
   *
   *  @param otherId the other id
   */
  def getId(otherId: Int = 0): Int = {
    val id = mt.nextInt(numberOfCptys)

    if (id != otherId) id
    else getId(otherId)
  }

  // Returns a sample row
  def buildSample = {
    val instrumentId = mt.nextLong(numberOfInstruments)
    val buyerId = mt.nextInt(numberOfCptys)
    val sellerId = getId(buyerId)
    val dealVolume = mt.nextInt(99) + 1 // Number of contracts, in thousands

    (instrumentId, buyerId, sellerId, dealVolume)
  }

  // Creating a random sample of deals
  simpleLog.writeMsg("Creating a random sample of deal Id's and volumes")

  val sample = new ListBuffer[(Long, Int, Int, Int)]

  for (i <- 1 to numberOfTrades) sample.append(buildSample)

  val sampleDF = spark.createDataFrame(spark.sparkContext.parallelize(sample))
    .toDF("INSTRUMENT_ID", "BUYER_ID", "SELLER_ID", "VOLUME")

  val instrumentsJoin =
    sampleDF.col("INSTRUMENT_ID") === instrumentsDF.col("INSTRUMENT_ID")

  simpleLog.writeMsg("Adding deal details to sample")

  val dealsDF = sampleDF.join(instrumentsDF, instrumentsJoin)
    .drop(sampleDF.col("INSTRUMENT_ID"))

  dealsDF.cache()

  // Writing deals sample to CSV file
  simpleLog.writeMsg("Writing deals sample to CSV file")

  dealsDF.coalesce(1).write.format("csv").mode("overwrite")
    .option("delimiter", ";").option("header", "true")
    .save(s"${dealsDir}.csv")

  // Closing Apache Spark and finishing the program
  simpleLog.writeMsg("Closing the Apache Spark session")

  spark.close()

  simpleLog.endProgram
}
