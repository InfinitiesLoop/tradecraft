package tradecraft.core

import org.json4s.native.JsonMethods._
import org.json4s.JsonDSL._

object Response {

  def render(line: String): RenderResponse = new RenderResponse(List(line))
  def render(lines: List[String]): RenderResponse = new RenderResponse(lines)
}

abstract class Response {
  def toJson: String
}
class RenderResponse(lines: List[String]) extends Response {
  override def toJson: String = {
    val json = ("type" -> "render") ~ ("render" -> lines)
    compact(render(json))
  }
}
