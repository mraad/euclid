package com.esri.euclid

/**
  * Extent of a data set.
  *
  * @param xmin the lower left horizontal value.
  * @param ymin the lower left vertical value.
  * @param xmax the upper right horizontal value.
  * @param ymax the upper right vertical value.
  */
case class Extent(xmin: Double = Double.PositiveInfinity,
                  ymin: Double = Double.PositiveInfinity,
                  xmax: Double = Double.NegativeInfinity,
                  ymax: Double = Double.NegativeInfinity
                 ) {

  /**
    * Expand the extent with a Euclid instance.
    *
    * @param e a Euclid instance.
    * @return a new Extent instance.
    */
  def +(e: Euclid) = {
    Extent(
      xmin min e.x,
      ymin min e.y,
      xmax max e.x,
      ymax max e.y
    )
  }

  /**
    * Expand the extent with given x,y values.
    *
    * @param x the x value to include.
    * @param y the y value to include.
    * @return a new Extent instance.
    */
  def add(x: Double, y: Double) = {
    Extent(
      xmin min x,
      ymin min y,
      xmax max x,
      ymax max y
    )
  }

  /**
    * @return the width of the extent.
    */
  def width(): Double = xmax - xmin

  /**
    * @return the height of the extent.
    */
  def height(): Double = ymax - ymin

}

/**
  * Companion object
  */
object Extent extends Serializable {
  /**
    * Create an Extent instance from Euclid instances.
    *
    * @param traversable Euclid instance.
    * @return the extent of the Euclid instance.
    */
  def apply(traversable: TraversableOnce[Euclid]): Extent = traversable.foldLeft(new Extent())(_ + _)
}
