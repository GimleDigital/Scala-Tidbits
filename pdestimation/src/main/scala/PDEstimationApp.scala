package com.gimledigital.pdestimation

import utils._

import scala.collection.mutable.ArrayBuffer
import scala.math.{BigInt, exp, pow, sqrt}

import org.apache.commons.math3.random.MersenneTwister

import breeze.stats.distributions.{Beta, Gaussian}

/** A simple sample app that estimates probabilities of default for low default
 *  portfolios.
 *
 *  This program follows the ideas in articles by Katja Pluto & Dirk Tasche,
 *  among others, c.f enclosed licence file for details.
 *
 * You might experiment with different cases by changing the fixed parameters
 * and the arrays of observations.
 */
object PDEstimationApp extends App {
  println("-"*80)
  println("COMMENCING THE ESTIMATION OF PROBABILITIES OF DEFAULT")
  println("-"*80 + "\n")

  // Fixed parameters
  val interval = 0.75 // Confidence interval
  val rho = 0.12 // Asset correlation
  val mc = 10000 // Monte Carlo sample size

  // Observations for each rating grade (the arrays must be of equal length)
  val obligors = Array(100, 400, 300) // Number of obligors
  val defaults = Array(0, 2, 1) // Number of defaults

  // Factorials are computed in advance for better performance
  val binoms = new DoubleKeyMap[Int, Int, Int]()

  for (i <- 0 to obligors.length - 1) {
    val currentN = obligors.slice(i, obligors.length).sum
    val currentR = defaults.slice(i, defaults.length).sum

    for (j <- 0 to currentR)  binoms.put(currentN)(j)(binom(currentN, j))
  }

  println(s"Obligors per rating grade: ${obligors.deep.mkString(", ")}")
  println(s"Defaults per rating grade: ${defaults.deep.mkString(", ")}\n")
  println(s"Confidence interval: $interval")
  println(s"Asset correlation: $rho")
  println(s"Monte Carlo sample size: $mc\n")
  println("-"*80 + "\n")

  println("Case 1: Independent Default Events\n")

  // Buffer to store PDs for the case of independent default events
  val upperBoundsIndependentEvents = new ArrayBuffer[Double]

  // PDs are obtained analytically for independent default events
  for (i <- 0 to obligors.length - 1) {
    val currentN = obligors.slice(i, obligors.length).sum
    val currentR = defaults.slice(i, defaults.length).sum

    val beta = new Beta(currentR + 1, currentN - currentR)

    upperBoundsIndependentEvents.append(beta.inverseCdf(interval))
  }

  // Printing the results for case 1: independent default events
  println("Rating grade  PD")
  println("------------  ----")
  for (i <- 0 to (upperBoundsIndependentEvents.length - 1)) {
    val value = doubleToString(upperBoundsIndependentEvents(i) * 100, 2)

    println(s"${i + 1}             ${value}%")
  }
  println("\n" + "-"*80 + "\n")

  println("Case 2: Dependent Default Events\n")

  // Pseudo-random sample to be used in the Monte Carlo simulation
  val mt = new MersenneTwister
  val sample = new ArrayBuffer[Double]

  for (i <- 1 to mc) sample.append(mt.nextGaussian)

  // Initializing a standard normal distribution
  val dist = new Gaussian(0.0, 1.0)

  println(s"A pseudorandom sample of $mc observations has been obtained\n")

  // Buffer to store PDs for the case of independent default events
  val upperBoundsDependentEvents = new ArrayBuffer[Double]

  /** Returns a theoretical confidence level for an individual Monte Carlo
   *  sample value and a trial probability of default value.
   *
   *  @param y the Monte Carlo sample value
   *  @param p the trial probability of default value
   *  @param n the total number of observations
   *  @param r the total number of defaults
   */
  def f(y: Double, p: Double, n: Int, r: Int) = {
    // The Vasicek function
    val v = dist.cdf((dist.inverseCdf(p) - sqrt(rho) * y) / sqrt(1.0 - rho))

    val buffer = new ArrayBuffer[Double]

    for (k <- 0 to r)
      buffer.append(binoms(n)(k) * pow(v, k) * pow(1.0 - v, n - k))

    buffer.sum
  }

  // PDs are obtained numerically for dependent default events
  for (i <- 0 to obligors.length - 1) {
    val currentN = obligors.slice(i, obligors.length).sum
    val currentR = defaults.slice(i, defaults.length).sum

    /** Returns the difference between a theoretical confidence level for a
     *  given trial probability of default value.
     *
     *  @param p the trial probability of default value
    */
    def trial(p: Double) = sample.map(f(_, p, currentN, currentR))
      .reduce(_ + _) / mc.toDouble + interval - 1.0

    val message = s"Estimating PD for rating grade ${i + 1}"
    println(s"${message}, this might take a while...")

    upperBoundsDependentEvents.append(
      modifSecant(trial, 0.0, 0.1, 0, 100, 4))
  }

  // Printing the results for case 1: independent default events
  println("\nRating grade  PD")
  println("------------  ----")
  for (i <- 0 to (upperBoundsDependentEvents.length - 1)) {
    val value = doubleToString(upperBoundsDependentEvents(i) * 100, 2)

    println(s"${i + 1}             ${value}%")
  }

  // End of program
  println("\n" + "-"*80)
  println("END OF PROGRAM")
  println("-"*80 + "\n")
}