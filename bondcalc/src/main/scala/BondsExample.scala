package com.gimledigital.bondcalc

import scala.math.{abs, round}
import java.math.{BigDecimal, MathContext, RoundingMode}
import scala.annotation.tailrec

object BondsExample extends App {
  // Bond characteristics
  val price: Double = 120.44
  val principal: Double = 100.0
  val coupon: Double = 8.5
  val periods: Int = 4

  println("--------------------------------------------------------------------------------")
  println(s"EXAMPLE: Price $price, Principal $principal, coupon $coupon % and $periods periods")

  // Calculating the yield
  val bond: VanillaBond = new VanillaBond(principal, coupon, periods)

  val mathcontext: MathContext = new MathContext(2, RoundingMode.HALF_DOWN)
  val bondyield: Double = bond.priceToYield(price, 0)

  val original: BigDecimal = new BigDecimal(bondyield)
  val result: BigDecimal = original.setScale(2, BigDecimal.ROUND_HALF_UP)

  println("--------------------------------------------------------------------------------")
  println(s"SOLUTION: The bond's yield to maturity is $result %")
  println("--------------------------------------------------------------------------------")
}
