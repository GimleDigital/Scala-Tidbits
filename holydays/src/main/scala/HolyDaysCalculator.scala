package holydays

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.geometry.Insets
import scalafx.scene.control._
import scalafx.scene.Scene
import scalafx.scene.paint.Color._
import java.text.NumberFormat
import java.util.{Calendar, Locale}

import scalafx.scene.layout.{GridPane, VBox}
import scalafx.util.converter.FormatStringConverter

/** A small sample app with GUI that calculates the weekday of New Year's Day 
 *  and the date of Easter Sunday for any years between upper and lower limits.
 *
 *  No params are needed since the app works for any year within the limits
 */
object HolyDaysCalculator extends JFXApp with DateUtilities {
  val calendar = Calendar.getInstance()
  
  /** Current year is obtained from the system */
  val currentYear = calendar.get(Calendar.YEAR )
  
  /** Lower limit, must be equal to or less than the current year. */
  val lowerLimit: Int = 1900

  /** Upper limit, must be equal to or greater than the current year. */
  val upperLimit: Int = 2100

  /** Returns verbose information on which year is used for the calculations
   *
   *  @param year the year to be used
   */
  def selectedYear(year: Int): String = {
    s"The selected year is $year"
  }
  
  /** Returns verbose information on which weekday is New Year's Day.
   *
   *  @param year the year to be used
   */
  def newYearsDay(year: Int): String = {
    val c = new Computus(year)
	val w = getWeekday(c.getNewYearsDay)
	s"New Year's Day is a $w"
  }

  /** Returns verbose information on the date of Easter Sunday.
   *
   *  @param year the year to be used
   */  
  def easterSunday(year: Int): String = {
    val c = new Computus(year)
	val t = c.getEasterSunday
	val m = getMonthName(t._1)
	val d = t._2
	s"Easter Sunday falls on $m $d"
  }

  stage = new JFXApp.PrimaryStage {
    title.value = "Holy Days Calculator"
	width = 768
	height = 432
    scene = new Scene {
      fill = ANTIQUEWHITE
      content = new VBox {
	    spacing = 10
	    padding = Insets(50)

		/** Creating an object to be used for conversion */
		val currencyFormat = NumberFormat.getIntegerInstance()

		/** Ensures conversion to be visualized as integer */ 
		val converter = new FormatStringConverter[Number](currencyFormat)

		/** Initializes slider control */
	    val slider = new Slider(lowerLimit, upperLimit, currentYear) {
          majorTickUnit = 100
		  showTickMarks = true
		  minorTickCount = 1
		  snapToTicks = false
		}	
 
        /** Initializes label with instructions on usage of the app.*/
	    val explanationLabel = new Label(
		  "Please move the slider to change year:")
		
		/** Initializes label with information on the selected year. */
		val selectedYearLabel = new Label()  {
		  text = selectedYear(currentYear)
        }

		/** Initializes label with information on weekday of New Year's Day. */
		val newYearsDayLabel = new Label()  {
		  text = newYearsDay(currentYear)
        }

		/** Initializes label with information about the date Easter Sunday. */
		val easterSundayLabel = new Label() {
		  text = easterSunday(currentYear)
		}

		/** Adds a listener to slider, changes affects other elements. */ 
		slider.valueProperty.addListener{ 
		  (o: javafx.beans.value.ObservableValue[_ <: Number], oldVal: Number, newVal: Number) =>
		    selectedYearLabel.text = selectedYear(newVal.intValue)
		    newYearsDayLabel.text = newYearsDay(newVal.intValue)
		    easterSundayLabel.text = easterSunday(newVal.intValue)
		}

		/** Displays all the window elements */
		children.addAll(
		  explanationLabel,
		  slider,
	      selectedYearLabel,
		  newYearsDayLabel,
		  easterSundayLabel)
	  }
	}
  }
}