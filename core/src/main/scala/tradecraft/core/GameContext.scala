package tradecraft.core

import tradecraft.core.model.GameState

class GameContext(gameState: GameState) {
  private var controllers = Set[Controller]()
  private var services = Set[Service]()

  def registerController(controllers: Controller*): Unit = this.controllers ++= controllers
  def registerService(services: Service*): Unit = this.services ++= services

  def getServices: Set[Service] = services
  def getControllers: Set[Controller] = controllers
}
