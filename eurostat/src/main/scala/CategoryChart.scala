package eurostat

import scalafx.Includes._
import scalafx.scene.chart.LineChart
import scalafx.scene.chart.CategoryAxis
import scalafx.scene.chart.NumberAxis
import scalafx.scene.chart.XYChart
import scalafx.scene.Node
import scalafx.collections.ObservableBuffer

/** Represents a category chart to be displayed.
 *
 *  @param chartTitle the title of the chart
 *  @param categoryAxis the label of the horizontal axis
 *  @param numberAxis the label of the vertical axis
 */
class CategoryChart(chartTitle: String, categoryAxis: String, numberAxis: String) {
  /** Returns a category chart.
   *
   *  @param seriesName the name of the time series
   *  @param dataset the dataset of labels and values to be displayed
   */
  def getChart(seriesName: String, dataset: List[(String, Float)]) = {
    // add starting data
    val series = new XYChart.Series[String, Number] {
      name = seriesName

      for (item <- dataset) {
         data() += XYChart.Data[String, Number](item._1, item._2)
      }
    }

    new LineChart[String, Number](CategoryAxis(categoryAxis), NumberAxis(numberAxis)) {
      title = chartTitle
      data() += series
    }
  }
}