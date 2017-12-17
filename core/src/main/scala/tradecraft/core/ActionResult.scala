package tradecraft.core

trait ActionResult {
  def execute(actionContext: ActionContext): Unit
}
