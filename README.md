# TradeCraft

### What is this?

It's an attempt at a game. The name is terrible and only a placeholder.
It's developed in my free time, and heavily inspired by some of the older
sci-fi text-based multiplayer games from the good ole BBS days. If you know
what a BBS is, you probably know what I'm referring to. If you don't, then
this probably isn't for you.

### The Vision

The game is text-based, although hopefully with VT100 terminal-like
support, including ANSI for coloring and extended ASCII characters for
a richer experience than just text.

Some main tenants of the architecture I have in mind are worth noting:

1. Designed to run with dumb thin clients. Clients should make requests
to the server via a single persistent socket, and receive commands they
should faithfully execute in response. A request doesn't already result
in a response though, and responses can come without requests. Calling them
requests and responses is probably wrong, but you know, naming things.
The point is, multiple clients could be written in whatever the appropriate
language for the target platform is, and it should be relatively easy.
The game never runs locally, although a local terminal client may exist,
which would just be yet another dumb client (probably easiest to write too,
since we obviously have terminal support there).

1. Web-based. I'd love for the game to be stupid easy to jump into, like
a lot of browser games are. Nothing to install. Just go to the url, maybe
connect your facebook account so you don't have to go through a registration
form, and you're in. This means I need some VT100-like behavior in the
browser. Should be a fun challenge if it doesn't exist already.

1. Mod-based. Absolutely everything about the game that can be implemented
as a mod, will be implemented as a mod. The engine itself will simply be a
driver for coordinating the mods and letting them get a crack at the actions
the user is taking and to mutate the game state. In the far future, I hope
to host the game in a way that allows people to dynamically choose the
mods they want to enable -- which might include turning _off_ some of the core
mods that ship with the game. For example -- the core game may have the
notion of Ports that exist in sectors that players can trade at for goods.
By turning off the core Ports mod and enabling some other community written
mod, users could get a very different game. Mods written by the community
go through an approval process and then become available for everyone to use.

1. Extendable without version hell. Mods have a tendency to get into version
hell in a lot of games. Especially when mods start depending on each other.
Not here, no way. Mods shall depend on the core engine libraries out of
necessity, but no mod shall ever need to depend on another.

1. Data is king. I hope to accomplish the previous goal by ensuring that
all communication between mods and with the game is done with data
structures only -- and only the ones the core game provides. Basically,
the data that represents the Game State is really what everything is about,
and mods are really just ways of _animating the data_.

1. Small to medium scale games. I see there being dozens of players in a
single 'server' instance, but no more than that. It's not an MMO. Single
player is possible, but that's just a server with a user limit of 1.
Games could be really long-running though -- like years long. The eventual
infrastructure for running servers will probably involve docker containers
that snapshot the game and all its mods, then get instanced. So old servers
can continue to run even as the game and mods versions are updated.

1. A single game state graph. Every object in a server instance is a node
in a labeled directed graph structure. Everything, even the player itself.
Objects are related to others via the labeled edges of that graph. For
example, a player in Sector A, which contains a Planet X. That's just 3
nodes in the graph with two edges: Player--attached-->Sector,
Planet--attached-->Sector. Any type of edge is possible. For example,
a sector may have exits to other sectors, as in Sector1--exit-->Sector2.

1. Single threaded? Since games are meant to be small to medium sized in
terms of the number of objects (and players) that are in a server, I hope
that a lot of the game can remain simple and single threaded. Mods might
employ threads to handle I/O or expensive operations, but the core of the
engine is single threaded, if I can get away with that. We'll see :)


## Contributing

Contact me. But also take a look at the Issues list. As I think of things
that can be worked on in parallel easily enough, I'll be creating issues
there.

## Running Locally

It's so rough right now. But run the server. Then telnet to the socket:

```
telnet localhost 8088
```

You send JSON commands and receive JSON responses. First thing you need to
do is authenticate with your account. Auth is faked out for now, so use
any username and password you want.

```
{"auth":{"name":"username","pw":"password"}}
```

After that, you can send JSON commands that look like this:

```
{"command":"foo"}
```

That command basically represents what would have happened if the client
typed 'foo' and hit enter. The server gets to decide what that means.
Which at the moment is nothing...
