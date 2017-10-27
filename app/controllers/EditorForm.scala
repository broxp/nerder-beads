package controllers

import play.api.data._

object EditorForm {
  case class EditorData(mode: Option[String], name: Option[String], url: Option[String],
                        colorName: Option[String], r: Option[Int], g: Option[Int], b: Option[Int])

  val form = Form(
    Forms.mapping(
      "mode" -> Forms.optional(Forms.text),
      "name" -> Forms.optional(Forms.text),
      "url" -> Forms.optional(Forms.text),
      "colorName" -> Forms.optional(Forms.text),
      "r" -> Forms.optional(Forms.number),
      "g" -> Forms.optional(Forms.number),
      "b" -> Forms.optional(Forms.number)
    )(EditorData.apply)(EditorData.unapply)
  )
}
