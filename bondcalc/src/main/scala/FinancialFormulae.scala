package com.gimledigital.bondcalc

import scala.math._

/**
 * This trait provides some standard financial formulas.
 *
 * No class methods or types are required for mixing in the trait.
 */
trait FinancialFormulae {
  /** Returns the capitalization factor for calculating the future value of a
   *  single cash flow.
   *
   * @param r the interest rate
   * @param t the number of periods
   */
  def capitalize (r: Double, t: Int): Double = {
    val factor = 1 + r/100
    if (t == 1) factor
    else factor * capitalize(r, t-1)
  }
  /** Returns the discount factor for calculating the present value of a single
   *  cash flow.
   *
   * @param r the interest rate
   * @param t the number of periods
   */
  def discount(r: Double, t: Int): Double = {
    val factor = 1/(1 + r/100)
    if (t == 1) factor
    else factor * discount(r, t-1)
  }
  /** Returns the capitalization factor for calculating the future value of a
   *  series of identical cash flows.
   *
   * @param r the interest rate
   * @param t the number of periods
   */
  def couponsReinvested(r: Double, t: Int): Double = {
    val values: Seq[Double] = (1 to t).map { per => capitalize(r, per) }
    values.foldLeft(0.0)(_ + _)
  }
  /** Returns the discount factor for calculating the present value of a series
   *  of identical cash flows.
   *
   * @param r the interest rate
   * @param t the number of periods
   */
  def couponsDiscounted(r: Double, t: Int): Double = {
    val values: Seq[Double] = (1 to t).map { per => discount(r, per) }
    values.foldLeft(0.0)(_ + _)
  }
  /** Returns the factor for calculating the present value of constant annuity.
   *
   * @param r the interest rate
   * @param t the number of periods
   */
  def annuityPV(r: Double, t: Int): Double = {
    val i = r / 100
    (1 - pow(1 + i, -t)) / i
  }
  /** Returns the present value of a series of periodic cash flows, where the
   *  amounts are not necessarily identical.
   *
   * @param r the interest rate
   * @param cf the series of cash flows
   */
  def cashFlowsPV(r: Double, cf: Seq[Double]): Double = {
    val idx = cf.indices.map(_ + 1)
    val values: Seq[Double] =  (cf zip idx) map { case (c, t) =>
      c * discount(r, t)
    }
    values.foldLeft(0.0)(_ + _)
  }
}