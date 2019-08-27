package com.gimledigital.pdestimation

import scala.math.{abs, BigInt, max, pow, round, signum, sqrt}
import scala.collection.mutable.ListBuffer

/** Utilities and helper functions */
package object utils {
  /** Returns a rounded value.
   *
   *  @param number the number to be rounded
   *  @param precision the number of decimals
   */
  def rounded(number: Double, precision: Int): Double = {
    (round(number * pow(10, precision.toFloat)) /
    pow(10, precision.toFloat)).toDouble
  }

  def doubleToString(value: Double, decimals: Int): String = {
    val roundedValue = rounded(value, decimals)
    s"%.${decimals}f".formatLocal(java.util.Locale.US, roundedValue)
  }

  /** Returns the sign (+ or -) of a number.
   *
   *  @param number the number to be examined
   */
  def getSign(number: Double) = if (signum(number) == 1) "+" else "-"

  /** Returns the factorial of a positive integer
   *
   *  @param k the integer
   */
  def factorial(k: BigInt): BigInt = {
    if (k == 0) 1
    else k * factorial(k - 1)
  }

  /** Returns the binomial coefficient for n and k (n over k).
   *
   *  @param n the upper integer
   *  @param k the lower integer
   */
  def binom(n: Int, k: Int) = {
    (factorial(n) / (factorial(k) * factorial(n - k))).toInt
  }

  /** Returns the value of x for which the function value is zero. This is a
   *  modified version of the secant method, where a condition have been added
   *  in order to avoid negative trial values.
   *
   *  @param f the function that is evaluated
   *  @param x0 the lower seed value of x to be used in the first iteration
   *  @param xk the upper seed value of x to be used in the first iteration
   *  @param iter the current iteration
   *  @param maxiter the maximum number of iterations (a safety measure)
   *  @param precision the precision for evaluating the stop-go condition
   */
  def modifSecant(f: Double => Double, x0: Double, xk: Double, iter: Int,
      maxIter: Int, precision: Int): Double = {
    if (iter > maxIter) {
      println("ERROR: Maximum number of iterations have been reached.")

      0.0
    }
    else {
      val x = xk - (f(xk) * (xk - x0)) / (f(xk) - f(x0))
      val y = (f(xk) - f(x0)) / (xk - x0)

      if (utils.rounded(y, precision) == 0.0) xk // Resolved
      else modifSecant(f, max(xk, 0.0), max(x,0.0), iter + 1, maxIter,
        precision) // Next iteration
    }
  }

  /** Provides a simple hash map with two keys instead of one
   *
   *  No parameters are required
   */
  class DoubleKeyMap[K1, K2, V] {
    val wrapped = new scala.collection.mutable.HashMap[(K1, K2), V]

    // Inserts a key-key-value item in the map
    def put(k1: K1)(k2: K2)(v: V) = wrapped.put((k1, k2), v)

    // Obtains a value from the map
    def apply(k1: K1)(k2: K2) = wrapped((k1, k2))
  }
}
