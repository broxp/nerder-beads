package models

import scala.collection.mutable.ArrayBuffer

case class Img(var w: Int, var h: Int, var arr : Array[Byte], var arrSrc : Array[Byte])

case class Editor(var mode: String, name: String, colors: ArrayBuffer[Color], img: Img, var url: String)

case class Color(name: String, code: String, r: Int, g: Int, b: Int,
                 quantity: Int = -1, var active : Boolean = true)