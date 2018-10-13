package eurostat

import scala.collection.mutable.ListBuffer

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.JsonNode

/** Parsing of a json-stat string. Use the functions to get specific items from
 *  the dataset.
 *
 * @param jsonString the string to be parsed
 */
class JsonParser(jsonString: String) {
  val mapper = new ObjectMapper

  val rootNode: JsonNode = mapper.readTree(jsonString)

  /** Returns the descriptive label for the dataset*/
  def getLabel: String = rootNode.get("label").asText

  /** Returns the dataset size */
  def getSize: Int = (rootNode.get("size")).get(3).asInt

  /** Returns the currency of the data */
  def getCurrency: String = {
    val dimensionNode: JsonNode = rootNode.get("dimension")
    val currencyNode: JsonNode = dimensionNode.get("currency")
    val categoryNode: JsonNode = currencyNode.get("category")
    val labelNode: JsonNode = categoryNode.get("label")

    labelNode.fieldNames.next
  }

  /** Returns the description of the currency of the data */
  def getCurrencyDescription = {
    val dimensionNode: JsonNode = rootNode.get("dimension")
    val currencyNode: JsonNode = dimensionNode.get("currency")
    val categoryNode: JsonNode = currencyNode.get("category")
    val labelNode: JsonNode = categoryNode.get("label")

    labelNode.elements.next.toString.replaceAll("^\"|\"$", "")
  }

  /** Returns the ovservation values of the dataset */
  def getValue: Map[Int, Float] = {
    val value = (rootNode.get("value")).toString
    val valueNode = mapper.readTree(value)

    val fieldNames = valueNode.fieldNames
    val keys = new ListBuffer[Int]
    while (fieldNames.hasNext()){
      keys += fieldNames.next.toInt
    }

    val elements = valueNode.elements
    val values = new ListBuffer[Float]
    while (elements.hasNext()){
      values += elements.next.asDouble.toFloat
    }

    (keys zip values).toMap
  }

  /** Returns the category labels of the dataset */
  def getTime: Map[Int, String] = {
    val dimensionNode: JsonNode = rootNode.get("dimension")
    val timeNode: JsonNode = dimensionNode.get("time")
    val categoryNode: JsonNode = timeNode.get("category")
    val indexNode: JsonNode = categoryNode.get("index")

    val elements = indexNode.elements
    val keys = new ListBuffer[Int]
    while (elements.hasNext()){
      keys += elements.next.asInt
    }

    val fieldNames = indexNode.fieldNames
    val values = new ListBuffer[String]
    while (fieldNames.hasNext()){
      values += fieldNames.next.toString
    }

    (keys zip values).toMap
  }

}