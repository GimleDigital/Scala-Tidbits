package eurostat

import scala.io.Source

import java.net.URL
import java.net.HttpURLConnection

/** Handles  data exchange over an Internet connection in the REST modality.
 *
 *  No params are needed since they are handled in the functions of the class
 */
class RESTHandler {
  /** Returns a valid connection over internet or else prints an error message.
   *
   *  @param request the string to be used as Internet address (http://...)
   */
  def getConnection(request: String): Option[URL] = {

    val url = new URL(request)

    val connection = url.openConnection().asInstanceOf[HttpURLConnection]
    connection.setConnectTimeout(5000)
    connection.setReadTimeout(5000)
    connection.connect()

    val responseCode = connection.getResponseCode
    val responseMessage = connection.getResponseMessage

    if (responseCode >= 400) {
      println(s"Error $responseCode: $responseMessage")
      None
    }
    else Some(url)
  }

  /** Returns the result of a REST request over Internet.
   *
   * @param request a valid http connection to be used for the request
   */
  def getResult(connection: URL): String = {
    val result = Source.fromURL(connection)

    result.mkString
  }
}