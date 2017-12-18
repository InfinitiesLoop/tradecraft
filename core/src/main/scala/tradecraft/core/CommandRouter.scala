package tradecraft.core

import tradecraft.core.model.{GameObject, GameState}

class CommandRouter(gameState: GameState) {
  private var controllers = Map[String, Controller]()

  def addRoute(gameObjectKind: String, controller: Controller): Unit =
    controllers += (gameObjectKind -> controller)

  def handleCommand(templateEngine: TemplateEngine, userCommand: UserCommand): Unit = {
    System.out.println(s"Handling command: ${userCommand.command}")

    val attachedTo = for {
      // get the Player game object associated with this user.
      player <- gameState.getObjects("players", "userid", userCommand.userId).headOption
      // get the object that player is attached to
      gameObject <- gameState.getConnectionsOut(player, "attached").headOption
    } yield gameObject

    // note that if the player is not attached to anything, we use a fake 'root' object.
    // this happens when a player has just been created and needs to be spawned by the root controller.
    val attachedToKind = attachedTo.map(_.kind).getOrElse("root")
    controllers.get(attachedToKind) match {
      case Some(controller) =>
        val actionContext = ActionContext(
          templateEngine = templateEngine,
          controller = controller,
          userCommand = userCommand,
          attachedObject = attachedTo)

        // get the controller for this kind of object and have it execute the action
        val actionResult = controller.action(actionContext)

        // do the action
        actionResult.execute(actionContext)

      case None =>
        System.out.println("No controller found.")
    }

  }
}

class RootController extends Controller {
  override def action(actionContext: ActionContext): ActionResult = {
    ViewActionResult("core/spawn_player.ftl")
  }
}

case class ActionContext(templateEngine: TemplateEngine,
                         controller: Controller,
                         userCommand: UserCommand,
                         attachedObject: Option[GameObject])





