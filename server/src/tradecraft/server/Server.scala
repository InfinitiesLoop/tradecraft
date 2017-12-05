package tradecraft.server

class Server {
  private var running = true

  private def UpdatesPerSecond = 20L
  private def FramesPerSecond = 20L
  private def RenderTime = true

  def run(): Unit = {
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
        //getInput()
        //update()
        ticks = ticks + 1
        deltaU = deltaU - 1
      }
      if (deltaF >= 1) {
        //render()
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
  }

  def stop(): Unit = {
    running = false
  }
}
