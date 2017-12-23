package tradecraft.core.mod

import tradecraft.core.{GameContext, ResourceTemplate}

class PlayerMod(gameContext: GameContext) extends Mod(gameContext) {
  override def name(): String = "player"
  override def description(): String = "Provides player creation."

  gameContext.registerController(new PlayerController(gameContext))

  private val cl = classOf[PlayerMod].getClassLoader
  gameContext.registerTemplate(ResourceTemplate("player/spawn", "templates/core/spawn_player.ftl", cl))
  gameContext.registerTemplate(ResourceTemplate("player/spawn_prompt", "templates/core/spawn_player_prompt.ftl", cl))
}
