package tradecraft.core

object UserCommand {
  def apply(userName: String, command: String): UserCommand = {
    new UserCommand(userName, command)
  }
  def Refresh = ""
}

class UserCommand(val userName: String, val line: String) {
}
