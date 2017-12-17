package tradecraft.core

object Response {

  def render(line: String): RenderResponse = new RenderResponse(List(line))
  def render(lines: List[String]): RenderResponse = new RenderResponse(lines)
}

class Response {}
class RenderResponse(lines: List[String]) extends Response
