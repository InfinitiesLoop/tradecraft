package tradecraft.core.mod

import tradecraft.core.GameContext

class PlayerMod(gameContext: GameContext) extends Mod(gameContext) {
  override def name(): String = "player"
  override def description(): String = "Provides player creation."

  gameContext.registerController(new PlayerController())
}
