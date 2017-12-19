package tradecraft.core.mod

import tradecraft.core.GameContext

class AuthMod(gameContext: GameContext) extends Mod(gameContext) {
  override def name(): String = "auth"
  override def description(): String = "Provides a basic authentication controller."

  gameContext.registerController(new AuthController)
}
