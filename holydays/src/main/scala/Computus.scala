package holydays

/** Represents the ecclesiastical rules for deriving some moving holidays. These
 *  were originally expressed in tables, the translation to formulas follows
 *  "Explanatory Supplement to the Astronomical Almanac" by Urban and Seidelman.
 *
 * @param Y the year for which the calculus should be applied
 */
class Computus(Y: Int) {
  /** Dominical Letter, e.g. Sunday's position this year */
  private val N: Int = 7 - (Y + Y / 4 - Y / 100 + Y / 400 - 1) % 7

  /* Golden number, e.g. a year's position in the Metonic Cycle */
  private val G: Int = 1 + Y % 19

  /** Unadjusted Epact, e.g the age of the  moon on January 1 */
  private val U: Int = (11 * G - 10) % 30

  /** The hundred part of the year */
  private val H: Int = Y / 100

  /** The solar equation adds 1 each time a leap day is dropped */
  private val SOL: Int = H - H / 4 - 12

  /** The lunar equation adds 1 on 8 occasions over a 2500-years cycle */
  private val LUN: Int = (H - 15 - (H - 17) / 25) / 3

  /** Adds 1 when epact = 24, or if  epact = 25 and G >= 12 */
  private val V: Int = U / 24 - U / 25 + (G / 12) * (U / 25 - U / 26)

  /** Epact adjusted for differences ecclesiastical vs astronomical calendar */
  private val E = (11 * G - 10) % 30 - (SOL - LUN) % 30 + V

  /** The ecclestiastical full moon, can be the second or third of the year */
  private val R: Int = if (E < 24) 45 - E else 75 - E

  /** Taking into account the weekday of the full moon */
  private val C: Int = 1 + (R + 2) % 7

  /** Easter Sunday expressed as a day count starting on March 1 */
  private val S: Int = R + (7 + N - C) % 7 // number of days

  /** Returns month and day of Easter Sunday
   *
   *  No params are needed, since the year is already set in the class instance
   */
  def getEasterSunday: (Int, Int) = (3 + S / 32, 1 + (S - 1) % 31)

  /** Returns month and day of New Year's Day
   *
   *  No params are needed, since the year is already set in the class instance
   */
  def getNewYearsDay: Int = (7 - N) % 7 + 1
}
