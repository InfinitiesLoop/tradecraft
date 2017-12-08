package tradecraft.core

object GameCommand {
  def apply(line: String): GameCommand = {
    new GameCommand(line)
  }
}

class GameCommand(val line: String) {
}
