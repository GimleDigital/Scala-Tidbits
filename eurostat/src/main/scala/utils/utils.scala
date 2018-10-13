package eurostat

import scala.collection.mutable.ListBuffer

/** Utilities and help functions */
package object utils {
  /** Returns a time series as a list of tuples of labels and values
   *
   * @param size the number of items in the time series
   * @param time an indexed map of category labels
   * @param value an indexed map of series values
   */
  def getTimeSeries(size: Int, time: Map[Int, String],
                    value: Map[Int, Float]): List[(String, Float)] = {
    val timeSeries = new ListBuffer[(String, Float)]
    val list = 0 to size - 1
    for (item <- list) {
      val x = time.apply(item)
      val y = value.apply(item)

      val z = (x, y)
      timeSeries += z
    }
    timeSeries.toList
  }
}