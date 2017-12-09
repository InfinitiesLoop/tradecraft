package tradecraft.core

object UserCommand {
  def apply(line: String): UserCommand = {
    new UserCommand(line)
  }
}

class UserCommand(val line: String) {
}
