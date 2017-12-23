package tradecraft.core.mod

import tradecraft.core.annotations.{Answer, Command}
import tradecraft.core._
import tradecraft.core.model.GameObject

class PlayerController(gameContext: GameContext) extends Controller {
  private def gameState = gameContext.gameState

  @Command(path = "player/spawn")
  def spawn(actionContext: ActionContext): ActionResult = {
    PromptActionResult(Some("player/spawn"), "player/spawn_prompt", "", "player/spawn")
  }

  @Answer(path = "player/spawn")
  def spawnAnswer(actionContext: ActionContext): ActionResult = {
    val userId = actionContext.connectedUser.getUserId
    if (userId.isEmpty) {
      throw ControllerException("User must be authenticated.")
    }
    val existingPlayer = gameState.getObjects("player", "userid", userId.get).headOption
    if (existingPlayer.isDefined) {
      throw ControllerException(s"Player already exists for User ${userId.get}.")
    }

    var player = GameObject("player", Map("userid" -> userId.get))
    player = gameState.insertObject(player)
    System.out.println(s"Spawned a player with name ${actionContext.command.param} (id=${player.id})")

    null
    // todo
  }
}
