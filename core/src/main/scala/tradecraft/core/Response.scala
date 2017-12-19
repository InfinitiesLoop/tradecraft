package tradecraft.core

import org.json4s.native.JsonMethods._
import org.json4s.JsonDSL._

object Response {

  def render(text: String): RenderResponse = new RenderResponse(text)

  def prompt(text: String, defaultValue: String, answerRoute: String): PromptResponse =
    new PromptResponse(text, defaultValue, answerRoute)

  def redirect(path: String): RedirectResponse =
    new RedirectResponse(path)
}

abstract class Response {
  def toJson: String
}

class RenderResponse(text: String) extends Response {
  override def toJson: String = {
    val json = ("type" -> "render") ~ ("render" -> text)
    compact(render(json))
  }
}

class PromptResponse(text: String, defaultValue: String, answerRoute: String) extends Response {
  override def toJson: String = {
    val json =
      ("type" -> "prompt") ~
      ("render" -> text) ~
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