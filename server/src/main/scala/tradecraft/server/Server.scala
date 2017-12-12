package tradecraft.server

import tradecraft.core.{PlayersController, Service}

class Server {
  private var running = true

  private def UpdatesPerSecond = 20L
  private def FramesPerSecond = 20L
  private def RenderTime = false
  var services: List[Service] = List()

  var playersController: Option[PlayersController] = None
  var serviceThreads: Option[List[Thread]] = None

  def addService(service: Service): Unit = {
    services = services :+ service
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
        System.out.println(s"[SERVER] Got Command => ${command.line}")
        processInput()
      case _ =>
    }
  }

  def run(): Unit = {
    startServices()

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
