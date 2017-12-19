package tradecraft.core.mod

import tradecraft.core.GameContext

abstract class Mod(val gameContext: GameContext) {
  def name(): String
  def description(): String

  def start(): Unit = {}
}
