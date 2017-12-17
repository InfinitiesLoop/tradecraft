package tradecraft.core

trait Controller {
  def action(actionContext: ActionContext): ActionResult
}
