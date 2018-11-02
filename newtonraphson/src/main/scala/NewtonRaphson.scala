package newtonraphson

import breeze.linalg.{DenseMatrix, inv}
import breeze.numerics.pow

object NewtonRaphson extends App {
  /** The initial qualified guess for the values of the n variables that are
   *  sought for by using the Newton-Raphson method.
   */
  val seedValues = DenseMatrix(2.0, 4.0)

  /** Maximum number of iterations for the Newton-Raphson method (a safety
   *  measure).
   */
  val maxIterations = 100

  /** Returns the value of n functions, evaluated for specific values of n
   *  variables. You might change the right-hand side definition of these
   *  functions, in this case the jacob method below should also be revised.
   *
   *   @param m the array of values for the n variables
   */
  def function = (m: DenseMatrix[Double]) =>
    DenseMatrix(5.0 * pow(m(0,0), 2) + 8.0 * pow(m(1,0), 2) - 9.0,
                3.0 * pow(m(0,0), 2) + 3.0 * pow(m(1,0), 2) - 4.0)

  /** The n * n Jacobian matrix of first-order derivatives for the functions
   *  specified in the method func above, evaluated for specific values of the
   *  n variables. If func is changed, this method should be revised as well.
   *
   *   @param m the array of values for the n variables
   */
  def jacobian = (m: DenseMatrix[Double]) =>
    DenseMatrix((10.0 * m(0,0), 16 * m(1,0)), (6 * m(0,0), 6 * m(1,0)))

  /** Returns the value of x for which the function value is zero, by using
   *  Newton's method of approximation.
   *
   *  @param f the functions that shall be evaluated
   *  @param j the jacobian parameters, i.e the first order differentials
   *  @param xk the seed values of x, used for the first iteration
   *  @param iter the number of iterations so far
   */
  def newtonRaphson(f: DenseMatrix[Double] => DenseMatrix[Double],
                j: DenseMatrix[Double] => DenseMatrix[Double],
                xk: DenseMatrix[Double], iter: Int): DenseMatrix[Double] = {
    val x = xk - inv(j(xk)) * f(xk)
    val y = f(x) + j(x) * (x - xk)

    if (y == DenseMatrix.zeros[Double](y.rows, y.cols)) x
    else if (iter >= maxIterations) {
      println(
        s"The limit of $maxIterations iterations was reached, process stopped.")
      DenseMatrix.zeros[Double](xk.rows, xk.cols)
    }
    else {
      newtonRaphson(f, j, x, iter + 1) // Not resolved, run next iteration
    }
  }

  val results = newtonRaphson(function, jacobian, seedValues, 1)

  println(s"\nResults:\n\n$results\n")
}
