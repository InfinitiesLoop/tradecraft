package tradecraft.core

import tradecraft.core.model.GameState

class GameContext(val gameState: GameState) {
  private var controllers = Set[Controller]()
  private var services = Set[Service]()
  private var templates = Set[Template]()

  def registerController(controllers: Controller*): Unit = this.controllers ++= controllers
  def registerService(services: Service*): Unit = this.services ++= services
  def registerTemplate(templates: Template*): Unit = this.templates ++= templates

  def getServices: Set[Service] = services
  def getControllers: Set[Controller] = controllers
  def getTemplates: Set[Template] = templates
}
