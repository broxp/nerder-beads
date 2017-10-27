package models.logic

trait SumMethod {
  def sum(r: Int, g: Int, b: Int, deltaR: Double, deltaG: Double, deltaB: Double): Double
}

object SumMethod {
  case object SumUnweighted extends SumMethod {
    def sum(r: Int, g: Int, b: Int, deltaR: Double, deltaG: Double, deltaB: Double) ={
      deltaR + deltaG + deltaB
    }
  }

  case object SumWeighted extends SumMethod {
    def sum(r: Int, g: Int, b: Int, deltaR: Double, deltaG: Double, deltaB: Double) ={
      val r2 = Math.max(1, r)
      val g2 = Math.max(1, g)
      val b2 = Math.max(1, b)
      val compSum = r2 + g2 + b2;
      (r2 / compSum) * deltaR + (g2 / compSum) * deltaG + (b2 / compSum) * deltaB
    }
  }
}
