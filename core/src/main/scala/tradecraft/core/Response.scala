package tradecraft.core

import org.json4s.native.JsonMethods._
import org.json4s.JsonDSL._

object Response {

  def render(line: String): RenderResponse = new RenderResponse(List(line))
  def render(lines: List[String]): RenderResponse = new RenderResponse(lines)

  def prompt(line: String, defaultValue: String, answerRoute: String): PromptResponse =
    new PromptResponse(List(line), defaultValue, answerRoute)

  def redirect(path: String): RedirectResponse =
    new RedirectResponse(path)
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

class PromptResponse(lines: List[String], defaultValue: String, answerRoute: String) extends Response {
  override def toJson: String = {
    val json =
      ("type" -> "prompt") ~
      ("render" -> lines) ~
      ("default" -> defaultValue) ~
      ("route" -> answerRoute)

    compact(render(json))
  }
}

class RedirectResponse(path: String) extends Response {
  override def toJson: String = {
    val json = ("type" -> "redirect") ~ ("route" -> path)

    compact(render(json))
  }
}