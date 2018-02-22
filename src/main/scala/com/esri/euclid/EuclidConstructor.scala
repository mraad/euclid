package com.esri.euclid

trait EuclidConstructor[T <: Euclid] extends Serializable {
  def construct(x: Double, y: Double): T
}
