package tradecraft.mod.netty

import tradecraft.core.GameContext
import tradecraft.core.mod.Mod

class NettyMod(gameContext: GameContext) extends Mod(gameContext) {
  override def name(): String = "netty"
  override def description(): String = "Provides a socket based PlayersController so users can play remotely."

  gameContext.registerService(new NioPlayersController())
}
