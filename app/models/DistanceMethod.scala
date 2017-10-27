package models.logic

trait DistanceMethod {
	def distance(compTarget: Int, compPalette: Int): Double
}

object DistanceMethod {
	case object DistanceAbs  extends  DistanceMethod {
		def distance(compTarget: Int, compPalette: Int): Double = {
			Math.abs(compTarget - compPalette)
		}
	}

	case object DistanceQuadratic  extends  DistanceMethod {
		def distance(compTarget: Int, compPalette: Int): Double = {
			val delta = compTarget - compPalette
			delta * delta
		}
	}
}