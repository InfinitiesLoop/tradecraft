package tradecraft.server

import org.clapper.classutil.ClassFinder
import tradecraft.core._
import tradecraft.core.model.GameState
import tradecraft.core.mod.Mod

import scala.collection.JavaConverters._

import scala.io.Source

class Server {
  private var running = true

  private def UpdatesPerSecond = 20L
  private def FramesPerSecond = 20L
  private def RenderTime = false

  private var services: List[Service] = List()
  private val gameState: GameState = new GameState()

  System.out.println("Loading mods...")
  private val mods: Map[String, Mod] = createMods()
  System.out.println(s"${mods.size} mods loaded.")

  // todo: this pattern is breaking down, we might want to define a ServerBuilder that can do all the init stuff
  // and then new up a Server instance with an already configured set of services, etc.
  var playersController: Option[PlayersController] = None
  var serviceThreads: List[Thread] = List()
  var commandRouter: Option[CommandRouter] = None
  var templateEngine: Option[TemplateEngine] = None

  private def createMods(): Map[String, Mod] = {
    val classInfo = ClassFinder.classInfoMap(ClassFinder().getClasses())
    val modClasses = ClassFinder.concreteSubclasses(classOf[Mod].getName, classInfo)
    modClasses.map(m => {
      // each mod gets a copy of a GameContext, which contains references to anything it might need
      // and will possibly serve as a container for things like event handlers
      val gameContext = new GameContext(gameState)
      val mod = Class.forName(m.name).getConstructor(classOf[GameContext]).newInstance(gameContext).asInstanceOf[Mod]
      (mod.name(), mod)
    }).toMap
  }

  private def startMods(): Unit = mods.foreach(_._2.start())

  private def setupRouter(): Unit = {
    val controllers = mods.flatMap(modInfo => modInfo._2.gameContext.getControllers).toSet
    commandRouter = Some(new CommandRouter(controllers, gameState))
  }

  def configureTemplateEngine(): Unit = {
    // TODO: we need to enumerate the resources in the loaded mods instead of this hard coded list.
    // But enumerating resources is harder than it should be....

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
    services = mods.flatMap(_._2.gameContext.getServices).toList
    serviceThreads = services.map(s => { new Thread(() => s.run())})

    serviceThreads.foreach(_.start())

    playersController = services
      .find(p => p.isInstanceOf[PlayersController])
      .map(s => s.asInstanceOf[PlayersController])
  }

  def stopServices(): Unit = {
    // stop services...
    services.foreach(s => s.close())

    // wait for services to stop...
    serviceThreads.foreach(t => t.join())

    // other stuff...
  }

  @scala.annotation.tailrec
  private def processInput(): Unit = {

    playersController.get.pollCommand match {
      case Some(command) =>
        commandRouter.get.handleCommand(templateEngine.get, command)
        //System.out.println(s"[SERVER] Got Command => [${command.userId}][${command.command}]")
        processInput()
      case _ =>
    }
  }

  def run(): Unit = {
    startMods()
    setupRouter()
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
