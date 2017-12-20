package tradecraft.core.mod

import tradecraft.core.annotations.{Answer, Command}
import tradecraft.core.{ActionContext, ActionResult, Controller, PromptActionResult}

class PlayerController extends Controller {
  @Command(path = "player/spawn")
  def spawn(actionContext: ActionContext): ActionResult = {
    PromptActionResult(Some("player/spawn"), "player/spawn_prompt", "", "player/spawn")
  }

  @Answer(path = "player/spawn")
  def spawnAnswer(actionContext: ActionContext): ActionResult = {
    System.out.println(s"Spawn a player with name ${actionContext.command.param}")
    null
  }
}
