package surfacechart

import scala.math.{max, min, pow}

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.beans.property.DoubleProperty
import scalafx.scene.input.MouseEvent
import scalafx.scene.input.ScrollEvent
import scalafx.scene.layout.StackPane
import scalafx.scene.PerspectiveCamera
import scalafx.scene.Scene

import scalafx.scene.paint.Color
import scalafx.scene.transform.Rotate

/** A small sample app that draws a 3D graph, where some function is displayed
 *  as a surface. The graph can be rotated by mouse drag-drop and the scroll
 *  wheel can be used to zoom in-out.
 *
 *  The code is based on an example found at https://stackoverflow.com/
 *  questions/31073007/how-to-create-a-3d-surface-chart-with-javafx and has been
 *  simplified and ported to ScalaFx.
 */
object SurfaceChart extends JFXApp {
  val size = 400 // Chart axes
  val amplitude = 350 // Surface amplitude, should be less than size

  val cubeColor = Color.DarkCyan // Chart background
  val gridColor = Color.White // Chart background grid
  val meshColor1 = Color.Red // Chart surface
  val meshColor2 = Color.Yellow

  // Initial values for animations
  private var mousePosX = 0.0
  private var mousePosY = 0.0

  private var mouseOldX = 0.0
  private var mouseOldY = 0.0

  private final val rotateX = new Rotate(20, Rotate.XAxis )
  private final val rotateY = new Rotate(135, Rotate.YAxis )

  /** The x and z values to be passed to the function, should be within suitable
   *  ranges.
   */
  val xValues = (0 to size).map(_ / 2.0 - 100).toArray
  val zValues = (0 to size).map(_ / 2.0 - 100).toArray

  /** Returns the value of the Goldstein–Price function. It is passed as an
   *  argument to the class that draws the surfaces and can be substituted for
   *  another function of convenience.
   *
   *  @param x the first variable
   *  @param z the second variable
   */
  def func = (x: Double, z: Double) => {
    val f1 = 1 + pow(x + z + 1, 2) * (19 - 14 * x + 3 * pow(x, 2) - 14 * z +
             6 * x * z + 3 * pow(z, 2))
    val f2 = 30 + pow(2 * x - 3 * z, 2) * (18 - 32 * x + 12 * pow(x, 2) +
             48 * z - 36 * x * z + 27 * pow(z, 2))

    (f1 * f2).toFloat
  }

  // Building the graphical user interface
  stage = new JFXApp.PrimaryStage {
    val fName = "Goldstein–Price" // Change this if another function is used
    val xInterval = s"x \u2208 (${xValues.min}, ${xValues.max})"
    val zInterval = s"z \u2208 (${zValues.min}, ${zValues.max})"

    // Window title
    title = (s"Evaluation of the $fName Function for $xInterval, $zInterval")
      .toUpperCase

    scene = new Scene(1154, 680) {
      fill = Color.AntiqueWhite

      camera = new PerspectiveCamera

      // Mouse drag and drop is used for rotating the graph
      onMousePressed = (me: MouseEvent) => {
        mouseOldX = me.sceneX
        mouseOldY = me.sceneY
      }
      onMouseDragged = (me: MouseEvent) => {
        mousePosX = me.sceneX
        mousePosY = me.sceneY
        rotateX.angle = rotateX.getAngle - (mousePosY - mouseOldY)
        rotateY.angle = rotateY.getAngle + (mousePosX - mouseOldX)
        mouseOldX = mousePosX
        mouseOldY = mousePosY
      }

      // Creating the graph surface and background cube
      val surface = new Surface(func, xValues, zValues, amplitude, meshColor1,
                                meshColor2)

      val cube = new Cube(size, cubeColor, gridColor) {
        children.add(surface.getMeshView)
        transforms.addAll(rotateX, rotateY)
      }

      val stackPane = new StackPane {
        // Centering the graoh in the window
        translateX = 377
        translateY = 100

        // Zoom limits
        val MAX_SCALE = 10.0
        val MIN_SCALE = 0.1

        // Scroll is used for zooming in and out
        onScroll = (event: ScrollEvent) => {
          if (event.eventType == ScrollEvent.Scroll) {
            val delta = 1.2
            var scale = scaleX.toDouble

            if (event.deltaY < 0) scale /= delta
            else scale *= delta

            scale = min(max(scale, MIN_SCALE), MAX_SCALE)

            scaleX = scale
            scaleY = scale

            event.consume
          }
        }

        children.add(cube)
      }

      content.add(stackPane)
    }
  }
}
