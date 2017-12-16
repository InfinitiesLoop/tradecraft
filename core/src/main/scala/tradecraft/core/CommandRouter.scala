package tradecraft.core

import tradecraft.core.model.GameState

class CommandRouter(gameState: GameState) {
  private var handlers = Map[String, Controller]()

  def addRoute(gameObjectKind: String, commandHandler: Controller): Unit =
    handlers += (gameObjectKind -> commandHandler)

  def handleCommand(userCommand: UserCommand): Unit = {

    val attachedTo = for {
      // get the Player game object associated with this user.
      player <- gameState.getObjects("players", "userid", userCommand.userId).headOption
      // get the object that player is attached to
      gameObject <- gameState.getConnectionsOut(player, "attached").headOption
    } yield gameObject

    // note that if the player is not attached to anything, we use a fake 'root' object.
    // this happens when a player has just been created and needs to be spawned by the root controller.
    val attachedToKind = attachedTo.map(go => go.kind).getOrElse("root")

    // get the controller for this kind of object and have it execute the action
    val actionResult = handlers.get(attachedToKind).map(handler => handler.action(userCommand))

    // do the action
    // todo: we need CONTEXT here so the action can write to the player.
    actionResult.foreach(_.execute())
  }
}

class RootController extends Controller {
  override def action(userCommand: UserCommand): ActionResult = {
    ViewActionResult("spawn_player")
  }
}

trait Controller {
  def action(userCommand: UserCommand): ActionResult
}

trait ActionResult {
  def execute(): Unit
}

object ViewActionResult {
  def apply(viewName: String): ActionResult = new ViewActionResult(viewName)
}
class ViewActionResult(viewName: String) extends ActionResult {
  def execute(): Unit = {
    // todo: render the view
    
    System.out.println("would have have rendered view: " + viewName)
  }
}