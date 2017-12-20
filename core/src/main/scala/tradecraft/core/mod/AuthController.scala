package tradecraft.core.mod

import tradecraft.core._
import tradecraft.core.annotations.{Answer, Command}

class AuthController extends Controller {
  @Command(path = "auth/login")
  def login(actionContext: ActionContext): ActionResult = {
    // this is the very first command executed after a connection is established.
    // the client doesn't actually send it, it's just implied by the connection being made.
    PromptActionResult(Some("auth/login"), "auth/login/username", "", "auth/username")
  }

  @Answer(path = "auth/username")
  def answerUserName(ctx: ActionContext): ActionResult = {
    ctx.connectedUser.setData("username", ctx.command.param)

    // now ask for their password
    PromptActionResult(None, "auth/login/password", "", "auth/password")
  }

  @Answer(path = "auth/password")
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
