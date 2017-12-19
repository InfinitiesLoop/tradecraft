package tradecraft.core

object StringActionResult {
  def apply(str: String): StringActionResult = new StringActionResult(str)
}

class StringActionResult(str: String) extends ActionResult {
  def execute(actionContext: ActionContext): Unit = {
    actionContext.connectedUser.sendResponse(Response.render(str))
  }
}