package tradecraft.core.mod

import tradecraft.core.{GameContext, ResourceTemplate}

class AuthMod(gameContext: GameContext) extends Mod(gameContext) {
  override def name(): String = "auth"
  override def description(): String = "Provides a basic authentication controller."

  gameContext.registerController(new AuthController)

  private val cl = classOf[AuthMod].getClassLoader
  gameContext.registerTemplate(ResourceTemplate("auth/login", "templates/core/login.ftl", cl))
  gameContext.registerTemplate(ResourceTemplate("auth/login/username", "templates/core/login_username.ftl", cl))
  gameContext.registerTemplate(ResourceTemplate("auth/login/password", "templates/core/login_password.ftl", cl))
}
