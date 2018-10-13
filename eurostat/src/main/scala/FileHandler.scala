package eurostat

import java.io.{File, FileWriter, FileNotFoundException, IOException}

class FileHandler(path: String) {
  /** Writes a string to a file
   *  @param string the string to be written
   *  @param fileName the name of the file
   */
  def stringToFile(string: String, fileName: String) {
    val file = new File(s"${path}/${fileName}")
    file.getParentFile().mkdirs() // Creates path if it does not already exist

    val writer = new FileWriter(s"${path}/${fileName}")
    writer.write(string)
    writer.close
  }

  /** Returns a string that is read from a file
   *
   * @param fileName the file to read
   */
  def fileToString(fileName: String): String = {
    val jsonStream = io.Source.fromFile(s"${path}/${fileName}")
    val jsonString = jsonStream.mkString
    jsonStream.close

    jsonString
  }
}