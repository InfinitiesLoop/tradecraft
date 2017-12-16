package tradecraft.core

object Response {
  case class RenderResponse(lines: List[String])

  def render(line: String): RenderResponse = RenderResponse(List(line))
  def render(lines: List[String]): RenderResponse = RenderResponse(lines)
}
class Response {}
