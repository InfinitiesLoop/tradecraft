package tradecraft.core

class ViewActionResult(viewName: String) extends ActionResult {
  def execute(actionContext: ActionContext): Unit = {
    // todo: integrate a view engine! probably add something for a view model to get passed in here too.
    actionContext.userCommand.connectedUser.sendResponse(Response.render("view = " + viewName))
  }
}

object ViewActionResult {
  def apply(viewName: String): ViewActionResult = new ViewActionResult(viewName)
}