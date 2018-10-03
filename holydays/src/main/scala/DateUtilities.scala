package holydays
/**
 * Provides some basic tools for handling days and weeks.
 *
 * No class methods or types are required for mixing in the trait.
 */
trait DateUtilities {
  /** Lookup collection for names and lengths of months */
  private val months = IndexedSeq(
                         ("January", 31), ("February", 28), ("March", 31),
                         ("April", 30),("May", 31), ("June", 30), ("July", 31),
                         ("August", 31), ("September", 30), ("October", 31),
                         ("November", 30), ("December", 31))

  /**  Lookup collection for names of weekdays */
  private val weekdays = IndexedSeq("Monday", "Tuesday", "Wednesday",
                                    "Thursday", "Friday", "Saturday", "Sunday")

  /** Returns 1 if the year is a leap year, else 0
   *
   *  @param year a year i the Christian Era
   */
  def leapYear(year:Int) = (if (year % 4 == 0) 1 else 0) -
                           (if (year % 100 == 0) 1 else 0) +
                           (if (year % 400 == 0) 1 else 0)

  /** Returns the name of the weekday
   *
   *  @param day the number of the day (allowed values are 1 to 7)
   */
  def getWeekday(day: Int): String = weekdays(day - 1)

  /** Returns the name of the month
   *
   *  @param day the number of the month (allowed values are 1 to 12)
   */
  def getMonthName(month: Int): (String) = months(month - 1)._1

  /** Returns the length of the month
   *
   *  @param day the number of the month (allowed values are 1 to 12)
   *  @param year the number of the year (should be a positive integer)
   */
  def getMonthLength(month: Int, year: Int): Int =  month match {
    case 2 => months(month - 1)._2 + leapYear(year)
    case _ => months(month - 1)._2
  }
}
