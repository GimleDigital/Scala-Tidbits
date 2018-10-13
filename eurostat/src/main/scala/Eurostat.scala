package eurostat

/** Specific information about the connection to the Eurostat data service. It
 *  is used to build a query in the format of a REST request
 */
trait Eurostat {
  /** Fixed part of the request related to the website. */
  val host_url = "http://ec.europa.eu/eurostat/wdds"
  /** Fixed part of the request related to the service. */
  val service = "rest/data"
  /** fixed part of the request related to the version of the service. */
  val version = "v2.1"
  /** data format to be returned (json or unicode). */
  val format = "json"
  /** Language used for metadata (en/fr/de). */
  val lang = "en"
  /** Unique code identifier of the queried dataset. */
  val datasetCode = "ert_bil_eur_a"

  /** Currencies to be chosen for the query filter (this is only a subset of
   *  the currencies available at Eurostat).
   */
  val currencyChoices = List("CAD", "CHF", "CZK", "GBP", "JPY", "PLN", "SEK",
                             "USD")
  /** The default currency to be used when the app is initialized */
  val defaultCurrency = "SEK"

  /** Returns all parts of the request except the filter part */
  def requestUrl: String = {
    s"$host_url/$service/$version/$format/$lang/$datasetCode"
  }

  /** Returns the filter part of the request, applying the chosen currency */
  def requestFilters(currency: String): String = {
    s"?precision=1&lastTimePeriod=10&currency=${currency}&statinfo=AVG"
  }
}