package models.logic

import scala.collection.mutable.ArrayBuffer

case class ConversionConfig(colors: ArrayBuffer[NamedColor], distanceMethod: DistanceMethod, sumMethod: SumMethod)
