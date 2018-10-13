package eurostat

import scala.collection.mutable.ListBuffer

import scalafx.application.JFXApp
import scalafx.collections.ObservableBuffer
import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.geometry.Insets
import scalafx.scene.control._
import scalafx.scene.Scene
import scalafx.scene.paint.Color._
import scalafx.scene.layout.VBox
import scalafx.event.ActionEvent

/** A small sample app with GUI that downloads data from Eurostat to a file,
 *  parse from json-stat to java objects and displays the data in a graph.
 *
 *  No params are needed to run the app.
 */
object EuropeanStatisticsApp extends JFXApp with Eurostat {
  /** Initial REST request for application startup */
  val defaultRequest = s"$requestUrl${requestFilters(defaultCurrency)}"

  /** Path where data files are stored (relative to app path) */
  val defaultPath = "data"

  /** Filename is built with the request parameters. */
  val fileName = s"$datasetCode.$format"

  val restHandler = new RESTHandler
  val fileHandler = new FileHandler(defaultPath)

  /** Returns a category graph, after downloading data to a file, retrieving
   *  the data from the file and finally building the graph.
   *
   *  @param currency the currency to be used when requesting the data and
   *  building the graph
   */
  def buildNewGraph(currency: String) = {
    val request = s"$requestUrl${requestFilters(currency)}"

    /** The connection is retrieved as an Option value*/
    val url = restHandler.getConnection(request)
    if (!url.isDefined) System.exit(0)  /** It was retrieved as an Option */

    val result = restHandler.getResult(url.get)

    fileHandler.stringToFile(result, fileName)
    val jsonString = fileHandler.fileToString(fileName)

    val jsonParser = new JsonParser(jsonString)

    val label = jsonParser.getLabel
    val xAxis = "Exchange rate"
    val yAxis = "Year"

    val categoryChart = new CategoryChart(label, xAxis, yAxis)

    val seriesName =
      s"${jsonParser.getCurrency} - ${jsonParser.getCurrencyDescription}"

    val size = jsonParser.getSize
    val time = jsonParser.getTime
    val value = jsonParser.getValue
    val dataset = utils.getTimeSeries(size, time, value)

    categoryChart.getChart(seriesName, dataset)
  }

  /** Building the application GUI. */
  stage = new JFXApp.PrimaryStage {
    title.value = "European Statistics App"
    width = 768
    height = 512
    scene = new Scene {
      fill = AZURE
      content = new VBox {
        spacing = 10
        padding = Insets(10)

        /** Initializes a label with instructions on usage of the app.*/
        val explanationLabel = new Label(
          "Please choose a currency, the graph will be refreshed automatically")

        /** Initializes a combo box to choose currency for the graph. */
        val currencyCombo = new ComboBox(currencyChoices) {
          value = defaultCurrency

          /** Choosing a new value fires an update of the graph. */
          onAction =  {
            val item = value
            _ => { val oldChart = children.last
                   val newChart = buildNewGraph(item.value)

                   children.remove(oldChart)
                   children.add(newChart)
                 }
          }
        }

        /** The initial graph is retrieved for the default currency. */
        val  categoryChart = buildNewGraph(defaultCurrency)

        children.addAll(explanationLabel, currencyCombo, categoryChart)
      }
    }
  }
}