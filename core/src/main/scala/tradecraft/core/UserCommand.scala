package tradecraft.core

object UserCommand {
  def apply(userId: Long, command: String): UserCommand = {
    new UserCommand(userId, command)
  }
  def Refresh = ""
}

class UserCommand(val userId: Long, val line: String) {
}
