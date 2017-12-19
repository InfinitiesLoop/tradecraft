package tradecraft.core

case class Request(`type`: String,
                   route: String,
                   param: String)

// examples of requests sent by clients:

// logging in:
//{ "type": "answer", "route": "root/username", "param": "myname" }
//{ "type": "answer", "route": "root/password", "param": "mypassword" }

// land on planets in attached sector
//{ "type": "command", "route": "sector", "param": "l" }