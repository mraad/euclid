package com.esri.euclid

import scala.collection.mutable.ArrayBuffer
import scala.math._

/**
  * Calculate the Directional Distribution (Standard Deviational Ellipse)
  *
  * http://pro.arcgis.com/en/pro-app/tool-reference/spatial-statistics/h-how-directional-distribution-standard-deviationa.htm
  *
  * @param mx  x mean
  * @param my  y mean
  * @param sx  x std dev
  * @param sy  y std dev
  * @param deg ellipse rotation
  * @param n   number of points used.
  */
case class DirDist[T <: Euclid](mx: Double, my: Double, sx: Double, sy: Double, deg: Double, n: Int) {

  /**
    * @return the area of the directional distribution ellipse at one standard deviation.
    */
  def area(): Double = Pi * sx * sy

  /**
    * Generate ellipse outline.
    *
    * @param nPoint number of points on the outline, default is 180.
    * @return iterable of Euclid implementations
    */
  def outline(nPoint: Int = 180)(implicit ctor: EuclidConstructor[T]): Iterable[T] = {
    val arr = new ArrayBuffer[T](nPoint)
    var t = 0.0
    val dt = 1.0 / nPoint

    val alpha = (90 - deg).toRadians
    val cosA = math.cos(alpha)
    val sinA = math.sin(alpha)
    val maj = sx max sy
    val min = sx min sy
    var n = 0
    while (n < nPoint) {
      val r = t * 2.0 * math.Pi
      val x = maj * math.cos(r)
      val y = min * math.sin(r)
      val rx = x * cosA - y * sinA
      val ry = x * sinA + y * cosA
      arr.append(ctor.construct(mx + rx, my + ry))
      t += dt
      n += 1
    }
    arr
  }

  /**
    * Calculate and return the location of the major ends.
    *
    * @return Seq of two elements.
    */
  def majorEnds()(implicit ctor: EuclidConstructor[T]): Seq[T] = {
    val alpha = (90 - deg).toRadians
    val cosA = math.cos(alpha)
    val sinA = math.sin(alpha)
    val maj = sx max sy
    val min = sx min sy
    Seq(0.0, Math.PI).map(r => {
      val x = maj * math.cos(r)
      val y = min * math.sin(r)
      val rx = x * cosA - y * sinA
      val ry = x * sinA + y * cosA
      ctor.construct(mx + rx, my + ry)
    })
  }
}

/**
  * Companion object.
  */
object DirDist extends Serializable {
  /**
    * Create a directional distribution instance.
    *
    * @param datum     the input data.
    * @param minPoints the min number of points to use. default is 3.
    * @return DirDist option.
    */
  def apply[T <: Euclid](datum: Iterable[T], minPoints: Int = 3): Option[DirDist[T]] = {

    val mu = datum.foldLeft(OnlineMu())(_ + _)

    if (mu.n > minPoints) {
      val (arr, x2, y2, xy) = mu.deviations(datum)
      if (x2.abs < 0.0000001 || y2.abs < 0.0000001)
        None
      else {
        val a = x2 - y2
        val b = sqrt(a * a + 4 * xy * xy)
        val c = 2.0 * xy

        val ang = atan((a + b) / c)
        val rad = if (ang < 0.0) Pi + ang else ang
        val deg = rad.toDegrees
        val flip = deg > 45 && deg < 135

        val ca = cos(ang)
        val sa = sin(ang)

        val (sx, sy) = arr.foldLeft((0.0, 0.0)) {
          case ((sx, sy), (x, y)) => {
            val dx = x * ca - y * sa
            val dy = x * sa + y * ca
            (sx + dx * dx, sy + dy * dy)
          }
        }

        val (tx, ty) = if (flip) (sy, sx) else (sx, sy)

        val sigX = sqrt(2.0) * sqrt(tx / mu.n)
        val sigY = sqrt(2.0) * sqrt(ty / mu.n)

        Some(new DirDist[T](mu.mx, mu.my, sigX, sigY, deg, mu.n))
      }
    } else {
      None
    }
  }
}
