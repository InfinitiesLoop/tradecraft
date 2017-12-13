package tradecraft.mod.netty

case class AuthMessage(name: String, pw: String)

case class UserMessage(command: Option[String],
                       auth: Option[AuthMessage])

// { "t": "command", "args": [] }
// { "command": "a" }
// { "number": 1234 }
// { "auth": { "name": "foo", pw: "hash" } }