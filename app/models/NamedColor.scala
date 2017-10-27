package models.logic

trait ColorKind {
}

object ColorKind extends Enumeration {
  case object Normal extends ColorKind
  case object Transp extends ColorKind
  case object Glowtransp extends ColorKind
}

case class NamedColor(name: String, code: String, r: Int, g: Int, b: Int,
                      kind: ColorKind, count: Int, var active: Boolean = true)
