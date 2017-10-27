package models.logic

import java.awt.image.BufferedImage

object ImageProcessor {
  def convertFile(config: ConversionConfig,
                  input: BufferedImage, output: BufferedImage, shiftValR: Int,
                  shiftValG: Int, shiftValB: Int) = {
    val w = input.getWidth()
    val h = input.getHeight()
    for (x <- 0.to(w-1)) {
      for (y <- 0.to(h-1)) {
        val rgb = input.getRGB(x, y)
        val resRgb : Int = convert(config, rgb, shiftValR, shiftValG, shiftValB)
        output.setRGB(x, y, resRgb)
      }
    }
    output
  }

  def convert(config: ConversionConfig, rgb: Int, shiftValR: Int,
              shiftValG: Int, shiftValB: Int) : Int = {
    val r = colorComponent(rgb, shiftValR, 2)
    val g = colorComponent(rgb, shiftValG, 1)
    val b = colorComponent(rgb, shiftValB, 0)
    var dist = Double.MaxValue
    var bestMatch : NamedColor = null
    val distMethod = config.distanceMethod
    val sumMethod = config.sumMethod
    for (namedColor <- config.colors) {
      if (namedColor.active) {

        val newDist = distTo(distMethod, sumMethod, namedColor, r, g, b)
        if (newDist < dist) {
          dist = newDist
          bestMatch = namedColor
        }
      }
    }
    if (bestMatch == null) {
      0xFFFFFF
    } else
      color(bestMatch)
  }

  def colorComponent(rgb: Int, shiftVal: Int, i: Int) : Int = {
    Math.max(0, Math.min(255, (rgb >> (i * 8) & 0xFF) + shiftVal))
  }

  def distTo(distanceMethod: DistanceMethod, sumMethod: SumMethod,
             namedColor: NamedColor, r: Int, g: Int, b: Int) : Double = {
    val distR = distanceMethod.distance(r, namedColor.r)
    val distG = distanceMethod.distance(g, namedColor.g)
    val distB = distanceMethod.distance(b, namedColor.b)
    val newDist = sumMethod.sum(r, g, b, distR, distG, distB)
    newDist
  }

  def color(bestMatch: NamedColor) = {
    bestMatch.r << (2 * 8) | bestMatch.g << (1 * 8) | bestMatch.b << (0 * 8)
  }
}
