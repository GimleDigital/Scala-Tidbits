package com.gimledigital.bondcalc

import scala.annotation.tailrec

/** A plain vanilla bond is a financial instrument with standard characteristics.
 *
 * @param principal the amount lent at the beginning and received at maturity
 * @param couponRate the paid interest rate
 * @param periods the number of coupons
 */
case class VanillaBond(principal: Double, couponRate: Double, periods: Int)
  extends FinancialFormulae {
  val coupon: Double = principal * couponRate / 100
  val cashflows: Seq[Double] = Seq.fill(periods - 1)(coupon) ++ Seq(coupon + principal)

  /* Returns the discounted value of the bond's cash flows.
   *
   * @param r the interest rate used for discounting
   */
  def netPresentValue(r: Double): Double = {
    cashFlowsPV(r, cashflows)
  }

  /* Recursive function that returns the bond's yield to maturity. If the discounted
   * value of the cash flows equals the initial payment, the current yield is correct.
   * If not, we make another recursive call to try with a slightly higher yield.
   *
   * Param@ p the price of the bond
   * Param@ y the suggested yield
   */
  @tailrec final def priceToYield(p: Double, y: Double): Double = {
    val fv: Double = p / 100 * principal
    if (netPresentValue(y) - fv < 0.01) y
    else  priceToYield(p , y + 0.00001)
  }
}
