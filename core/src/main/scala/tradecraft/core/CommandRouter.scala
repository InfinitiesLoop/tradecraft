package tradecraft.core

import tradecraft.core.model.{GameObject, GameState}

class CommandRouter(gameState: GameState) {
  private var handlers = Map[String, Controller]()

  def addRoute(gameObjectKind: String, commandHandler: Controller): Unit =
    handlers += (gameObjectKind -> commandHandler)

  def handleCommand(userCommand: UserCommand): Unit = {
    System.out.println(s"Handling command: ${userCommand.command}")

    val attachedTo = for {
      // get the Player game object associated with this user.
      player <- gameState.getObjects("players", "userid", userCommand.userId).headOption
      // get the object that player is attached to
      gameObject <- gameState.getConnectionsOut(player, "attached").headOption
    } yield gameObject

    // note that if the player is not attached to anything, we use a fake 'root' object.
    // this happens when a player has just been created and needs to be spawned by the root controller.
    val attachedToKind = attachedTo.map(go => go.kind).getOrElse("root")
    val handler = handlers.get(attachedToKind)
    val actionContext = ActionContext(
      userCommand = userCommand,
      attachedObject = attachedTo)

    // get the controller for this kind of object and have it execute the action
    val actionResult = handlers.get(attachedToKind).map(handler => handler.action(actionContext))

    // do the action
    actionResult.foreach(_.execute(actionContext))
  }
}

class RootController extends Controller {
  override def action(actionContext: ActionContext): ActionResult = {
    //ViewActionResult("spawn_player")
    StringActionResult("<span player>")
  }
}

// todo: why can't I hold all these classes??

case class ActionContext(userCommand: UserCommand, attachedObject: Option[GameObject])

trait Controller {
  def action(actionContext: ActionContext): ActionResult
}

trait ActionResult {
  def execute(actionContext: ActionContext): Unit
}

object StringActionResult {
  def apply(str: String): StringActionResult = new StringActionResult(str)
}
class StringActionResult(str: String) extends ActionResult {
  def execute(actionContext: ActionContext): Unit = {
    actionContext.userCommand.connectedUser.sendResponse(Response.render(str))
  }
}

object ViewActionResult {
  def apply(viewName: String): ViewActionResult = new ViewActionResult(viewName)
}
class ViewActionResult(viewName: String) extends ActionResult {
  def execute(actionContext: ActionContext): Unit = {
    // todo: integrate a view engine! probably add something for a view model to get passed in here too.
    actionContext.userCommand.connectedUser.sendResponse(Response.render("view = " + viewName))
  }
}