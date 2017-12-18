package tradecraft.core

class ViewActionResult(viewName: String, viewData: AnyRef) extends ActionResult {
  def execute(actionContext: ActionContext): Unit = {
    val result = actionContext.templateEngine.render(viewName, viewData)
    actionContext.userCommand.connectedUser.sendResponse(Response.render(result))
  }
}

object ViewActionResult {
  def apply(viewName: String, viewData: AnyRef = null): ViewActionResult = new ViewActionResult(viewName, viewData: AnyRef)
}