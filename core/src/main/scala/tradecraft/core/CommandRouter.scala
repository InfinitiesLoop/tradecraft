package tradecraft.core

import java.lang.reflect.Method

import tradecraft.core.annotations.{Answer, Command}
import tradecraft.core.model.{GameObject, GameState}

class CommandRouter(controllers: Set[Controller], gameState: GameState) {
  private val routeTable = buildRouteTable()

  private def buildRouteTable(): Map[(String, String), (Controller, Method)] = {
    controllers.flatMap(c => {
      c.getClass.getMethods.flatMap(method => {

        method.getDeclaredAnnotationsByType(classOf[Command]).map(r => {
          ("command", r.path) -> (c, method)
        }) ++
        method.getDeclaredAnnotationsByType(classOf[Answer]).map(r => {
          ("answer", r.path) -> (c, method)
        }).toSeq

      })
    }).toMap
  }

  def handleCommand(templateEngine: TemplateEngine, userCommand: UserCommand): Unit = {
    System.out.println(s"Handling command: ${userCommand.commandInfo.`type`}")

    // todo: enforce that an unauthenticated user has to be on the root controller.

    val attachedTo = for {
      userId <- userCommand.connectedUser.getUserId
      // get the Player game object associated with this user.
      player <- gameState.getObjects("players", "userid", userId).headOption
      // get the object that player is attached to
      gameObject <- gameState.getConnectionsOut(player, "attached").headOption
    } yield gameObject

    routeTable.get((userCommand.commandInfo.`type`, userCommand.commandInfo.route)) match {
      case Some(methodInfo) =>
        val actionContext = ActionContext(
          templateEngine = templateEngine,
          controller = methodInfo._1,
          method = methodInfo._2,
          connectedUser = userCommand.connectedUser,
          command = userCommand.commandInfo,
          attachedObject = attachedTo)

        // get the controller for this kind of object and have it execute the action
        val actionResult = methodInfo._2.invoke(methodInfo._1, actionContext).asInstanceOf[ActionResult]

        if (actionResult != null) {
          // do the action
          actionResult.execute(actionContext)
        }
      case None =>
        System.out.println("No controller found.")
    }
  }
}

case class ActionContext(templateEngine: TemplateEngine,
                         controller: Controller,
                         method: Method,
                         connectedUser: ConnectedUser,
                         command: CommandInfo,
                         attachedObject: Option[GameObject])





