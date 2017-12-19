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

        // do the action
        actionResult.execute(actionContext)
      case None =>
        System.out.println("No controller found.")
    }
  }
}

class RootController extends Controller {
  @Command(path = "root/login")
  def login(actionContext: ActionContext): ActionResult = {
    // this is the very first command executed after a connection is established.
    // the client doesn't actually send it, it's just implied by the connection being made.
    PromptActionResult(Some("core/login.ftl"), "core/login_username.ftl", "", "root/username")
  }

  @Answer(path = "root/username")
  def answerUserName(ctx: ActionContext): ActionResult = {
    ctx.connectedUser.setData("username", ctx.command.param)

    // now ask for their password
    PromptActionResult(None, "core/login_password.ftl", "", "root/password")
  }

  @Answer(path = "root/password")
  def answerPassword(ctx: ActionContext): ActionResult = {
    val username: String = ctx.connectedUser.getData("username").getOrElse("")
    //val password = ctx.command.param

    // todo: actually authenticate
    System.out.println(s"Authenticated user ${username}.")
    ctx.connectedUser.authenticatedAs(88)

    // now that they are logged in we'll give them the root action
    index(ctx)
  }

  @Command(path = "root")
  def index(ctx: ActionContext): ActionResult = {
    // give them a refresh of their attached object,
    // or if not attached to anything, take them to the spawn player process.

    ctx.attachedObject match {
      case None =>
        RedirectActionResult("player/spawn")
      case Some(go) =>
        RedirectActionResult(go.kind)
    }
  }
}

class PlayerController extends Controller {
  @Command(path = "player/spawn")
  def spawn(actionContext: ActionContext): ActionResult = {
    PromptActionResult(Some("player/spawn.ftl"), "player/spawn_prompt.ftl", "", "player/spawn")
  }

  @Answer(path = "player/spawn")
  def spawnAnswer(actionContext: ActionContext): ActionResult = {
    System.out.println(s"Spawn a player with name ${actionContext.command.param}")
    null
  }
}

case class ActionContext(templateEngine: TemplateEngine,
                         controller: Controller,
                         method: Method,
                         connectedUser: ConnectedUser,
                         command: CommandInfo,
                         attachedObject: Option[GameObject])





