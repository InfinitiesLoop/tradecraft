package tradecraft.server

import tradecraft.core.model.GameState
import tradecraft.core._

import scala.io.Source

class Server {
  private var running = true

  private def UpdatesPerSecond = 20L
  private def FramesPerSecond = 20L
  private def RenderTime = false
  var services: List[Service] = List()

  // todo: this pattern is breaking down, we might want to define a ServerBuilder that can do all the init stuff
  // and then new up a Server instance with an already configured set of services, etc.
  var playersController: Option[PlayersController] = None
  var serviceThreads: Option[List[Thread]] = None
  val gameState: GameState = new GameState()
  private val controllers = Set[Controller](new RootController, new PlayerController)
  val commandRouter: CommandRouter = new CommandRouter(controllers, gameState)
  var templateEngine: Option[TemplateEngine] = None

  def addService(service: Service): Unit = {
    services = services :+ service
  }

  def configureTemplateEngine(): Unit = {
    // ultimately the list of templates should be gathered from all the loaded mods, perhaps by
    // automatically scanning all their resources for a 'templates' directory and then using their
    // mod name at the first part of the name given to it. But, we would also want a mod to be able
    // to provide a replacement template for an existing one, so some API for providing them explicitly
    // would be needed as well.
    // Also, right now we just store all the templates as strings in memory, which is probably fine, but
    // as many templates will probably go unused most the time, we should probably lazily load them.
    templateEngine = Some(new FreeMarkerTemplateEngine(Map(
      "player/spawn.ftl" -> Source.fromResource("templates/core/spawn_player.ftl").mkString,
      "player/spawn_prompt.ftl" -> Source.fromResource("templates/core/spawn_player_prompt.ftl").mkString,
      "core/login.ftl" -> Source.fromResource("templates/core/login.ftl").mkString,
      "core/login_username.ftl" -> Source.fromResource("templates/core/login_username.ftl").mkString,
      "core/login_password.ftl" -> Source.fromResource("templates/core/login_password.ftl").mkString
    )))
  }

  def startServices(): Unit = {
    serviceThreads = Some(services.map(s => {
      new Thread(() => s.run())
    }))

    serviceThreads.get.foreach(t => t.start())

    playersController = services
      .find(p => p.isInstanceOf[PlayersController])
      .map(s => s.asInstanceOf[PlayersController])
  }

  def stopServices(): Unit = {
    // stop services...
    services.foreach(s => s.close())

    // wait for services to stop...
    serviceThreads.get.foreach(t => t.join())

    // other stuff...
  }

  @scala.annotation.tailrec
  private def processInput(): Unit = {

    playersController.get.pollCommand match {
      case Some(command) =>
        commandRouter.handleCommand(templateEngine.get, command)
        //System.out.println(s"[SERVER] Got Command => [${command.userId}][${command.command}]")
        processInput()
      case _ =>
    }
  }

  def run(): Unit = {
    startServices()
    configureTemplateEngine()

    var initialTime = System.nanoTime
    val timeU: Double = 1000000000D / UpdatesPerSecond
    val timeF: Double = 1000000000D / FramesPerSecond
    var deltaU = 0D
    var deltaF = 0D
    var frames = 0L
    var ticks = 0L
    var timer = System.currentTimeMillis

    while (running) {
      val currentTime = System.nanoTime
      deltaU = deltaU + ((currentTime - initialTime) / timeU)
      deltaF = deltaF + ((currentTime - initialTime) / timeF)
      initialTime = currentTime
      if (deltaU >= 1) {
        processInput()
        //update()
        ticks = ticks + 1
        deltaU = deltaU - 1
      }
      if (deltaF >= 1) {
        //sendClientUpdates()
        frames = frames + 1
        deltaF = deltaF - 1
      }
      if (System.currentTimeMillis - timer > 1000) {
        if (RenderTime) System.out.println(s"UPS: ${ticks}, FPS: ${frames}")
        frames = 0
        ticks = 0
        timer = timer + 1000
      }
    }

    stopServices()
  }

  def stop(): Unit = {
    running = false
  }
}
