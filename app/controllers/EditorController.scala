package controllers

import java.awt.image.BufferedImage
import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import java.net.URL
import java.nio.charset.Charset
import java.util.Base64
import javax.imageio.ImageIO
import javax.inject.Inject

import controllers.EditorForm._
import models._
import models.logic._
import play.api.data._
import play.api.db.Database
import play.api.mvc._

import scala.collection.mutable._

class EditorController @Inject()(cc: MessagesControllerComponents, db: Database)
  extends MessagesAbstractController(cc) {

  val colors = ArrayBuffer(
    Color("white", "h01", 236, 237, 237),
    Color("cream", "h02", 240, 232, 185),
    Color("yellow", "h03", 240, 185, 1),
    Color("orange", "h04", 230, 79, 39),
    Color("red", "h05", 182, 49, 54),
    Color("pink", "h06", 225, 136, 159),
    Color("purple", "h07", 105, 74, 130),
    Color("dark blue ", "h08", 44, 70, 144),
    Color("light blue", "h09", 48, 92, 176),
    Color("green", "h10", 37, 104, 71),
    Color("light green", "h11",73, 174, 137),
    Color("brown", "h12", 83, 65, 55),
    Color("black", "h18", 46, 47, 49)
  )

  var defaultBase64 =
    "iVBORw0KGgoAAAANSUhEUgAAAAoAAAAKCAYAAACNMs+9AAAABGdBTUEAALGP" +
      "C/xhBQAAAAlwSFlzAAALEwAACxMBAJqcGAAAAAd0SU1FB9YGARc5KB0XV+IA" +
      "AAAddEVYdENvbW1lbnQAQ3JlYXRlZCB3aXRoIFRoZSBHSU1Q72QlbgAAAF1J" +
      "REFUGNO9zL0NglAAxPEfdLTs4BZM4DIO4C7OwQg2JoQ9LE1exdlYvBBeZ7jq" +
      "ch9//q1uH4TLzw4d6+ErXMMcXuHWxId3KOETnnXXV6MJpcq2MLaI97CER3N0" +
      "vr4MkhoXe0rZigAAAABJRU5ErkJggg=="

  var defaultUrl = "https://www.spriters-resource.com/resources/game_icons/1/683.png"

  val ascii = Charset.forName("ASCII")
  val defaultArray: Array[Byte] = Base64.getDecoder.decode(defaultBase64.getBytes(ascii))
  val ed = Editor("main", "Nerder Beads", colors, Img(10, 10, defaultArray, defaultArray), defaultUrl)

  def index = Action {
    // TODO remove, DEBUG only
    SqlHelpers.initDB(db, colors : _*)

    Ok(views.html.index())
  }

  def navigate(id: String) = Action { implicit request: MessagesRequest[_] =>
    ed.mode = id
    Redirect(routes.EditorController.listItems).flashing("info" -> ("Navigated to " + id + "!"))
  }

  def deleteItem(id: String) = Action { implicit request: MessagesRequest[_] =>
    colors.remove(colors.indexWhere(_.code == id))
    Redirect(routes.EditorController.listItems).flashing("info" -> ("Color " + id + " deleted!"))
  }

  def toggleItem(id: String) = Action { implicit request: MessagesRequest[_] =>
    colors.find(_.code == id).map(x => x.active = !x.active)
    Redirect(routes.EditorController.listItems).flashing("info" -> ("Color " + id + " toggled!"))
  }

  def listItems = Action { implicit request: MessagesRequest[_] =>
    Ok(views.html.listItems(ed, EditorForm.form))
  }

  def onSuccessDo(msg : String, target: Call) (fun : EditorData => Unit) = Action {
    implicit request: MessagesRequest[_] =>
      val successFunction = { data: EditorData =>
        fun(data)
        refresh
        Redirect(target).flashing("info" -> msg)
      }
      val errorFunction = { formWithErrors: Form[EditorData] =>
        println("Err: "+ formWithErrors)
        BadRequest(views.html.listItems(ed, formWithErrors))
      }
      val formValidationResult = EditorForm.form.bindFromRequest
      formValidationResult.fold(errorFunction, successFunction)
  }

  def createItem = {
    onSuccessDo("created", routes.EditorController.listItems) { data =>
      val name = data.colorName.getOrElse("n")
      val code = name + "_" + colors.size
      val c = Color(name, code,
        data.r.getOrElse(0), data.g.getOrElse(0), data.b.getOrElse(0))
      colors.append(c)
      ed.mode = "main"
    }
  }

  def loadImage = {
    onSuccessDo("loaded", routes.EditorController.listItems) { data =>
      println("data :" + data)
      ed.url = data.url.getOrElse(defaultUrl)
    }
  }

  def returnImageArray(arr : Unit => Array[Byte]) = Action {
    implicit request: MessagesRequest[_] =>
      refresh
      Ok(arr()).withHeaders("Content-Type" -> "image/png")
  }

  def getImage(id: String) = {
    returnImageArray(_ => ed.img.arrSrc)
  }

  def processImage(id: String) ={
    returnImageArray(_ => ed.img.arr)
  }

  def getColors = {
    colors.map {
      c => new NamedColor("h1", "h1", c.r, c.g, c.b, ColorKind.Normal, 1, c.active)
    }
  }

  // TODO Async
  def refresh = {
    Helpers.fixHttps
    try {
      val input = new URL(ed.url).openStream
      ed.img.arrSrc = Helpers.toArr(input)
    } catch {
      case x : Exception =>
        x.printStackTrace // TODO Error handling
        ed.img.arrSrc = defaultArray
    }

    val img = ImageIO.read(new ByteArrayInputStream(ed.img.arrSrc))
    val conf = ConversionConfig(getColors, DistanceMethod.DistanceQuadratic, SumMethod.SumUnweighted)
    val outImg = new BufferedImage(img.getWidth, img.getHeight, BufferedImage.TYPE_3BYTE_BGR)
    ImageProcessor.convertFile(conf, img, outImg, 50, 50, 50)
    val out = new ByteArrayOutputStream
    ImageIO.write(outImg, "PNG", out)
    ed.img.arr = out.toByteArray
    ed.img.w = img.getWidth
    ed.img.h = img.getHeight
  }
}