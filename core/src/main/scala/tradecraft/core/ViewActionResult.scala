package tradecraft.core

object ViewActionResult {
  def apply(viewName: String, viewData: AnyRef = null): ViewActionResult = new ViewActionResult(viewName, viewData: AnyRef)
}
class ViewActionResult(viewName: String, viewData: AnyRef) extends ActionResult {
  def execute(actionContext: ActionContext): Unit = {
    val result = actionContext.templateEngine.render(viewName, viewData)
    actionContext.connectedUser.sendResponse(Response.render(result))
  }
}

object PromptActionResult {
  def apply(viewName: Option[String], promptViewName: String, defaultValue: String,
            answerRoute: String, viewData: AnyRef = null): PromptActionResult =
    new PromptActionResult(viewName, promptViewName, defaultValue, answerRoute, viewData)
}
class PromptActionResult(
                          viewName: Option[String],
                          promptViewName: String,
                          defaultValue: String,
                          answerRoute: String,
                          viewData: AnyRef) extends ActionResult {
  def execute(actionContext: ActionContext): Unit = {
    // render the view, if any
    if (viewName.isDefined) {
      val result = actionContext.templateEngine.render(viewName.get, viewData)
      actionContext.connectedUser.sendResponse(Response.render(result))
    }
    // render the prompt
    val result = actionContext.templateEngine.render(promptViewName, viewData)
    actionContext.connectedUser.sendResponse(Response.prompt(result, defaultValue, answerRoute))
  }
}

object RedirectActionResult {
  def apply(path: String): RedirectActionResult = new RedirectActionResult(path)
}
class RedirectActionResult(path: String) extends ActionResult {
  def execute(actionContext: ActionContext): Unit = {
    actionContext.connectedUser.sendResponse(Response.redirect(path))
  }
}