package tradecraft.core

case class AuthRequest(name: String, pw: String)

case class Request(command: Option[String],
                   auth: Option[AuthRequest])

// { "command": "a" }
// { "auth": { "name": "foo", pw: "hash" } }