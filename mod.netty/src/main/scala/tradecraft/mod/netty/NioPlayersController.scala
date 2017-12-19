package tradecraft.mod.netty

import tradecraft.core.{CommandInfo, PlayersController, UserCommand}
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.{ChannelFuture, ChannelHandlerContext, ChannelInitializer, ChannelOption}
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.codec.json.JsonObjectDecoder
import io.netty.util.AttributeKey
// TODO: PlayersController should probably be called the UsersController

object NioPlayersController {
  val connectedUserKey: AttributeKey[NioConnectedUser] = AttributeKey.valueOf("nioplayerscontroller.connecteduser")
}

class NioPlayersController extends PlayersController {
  var channelFuture: Option[ChannelFuture] = None
  var masterGroup = new NioEventLoopGroup
  var workerGroup = new NioEventLoopGroup

  var connectedUsers: Set[NioConnectedUser] = Set()


  override def run(): Unit = {
    val self = this
    try {
      val b = new ServerBootstrap
      b.group(masterGroup, workerGroup)
        .channel(classOf[NioServerSocketChannel])
        .childHandler(new ChannelInitializer[SocketChannel] {
          override def channelUnregistered(ctx: ChannelHandlerContext): Unit = {
            // de-associate the channel with the connected user
            // probably don't actually have to do this but should make the user able to be cleaned up sooner
            connectedUsers -= ctx.channel().attr(NioPlayersController.connectedUserKey).get()
            ctx.channel().attr(NioPlayersController.connectedUserKey).set(null)
          }
          override def initChannel(ch: SocketChannel): Unit = {
            // This channel is associated with a user
            val user = new NioConnectedUser(ch)
            connectedUsers += user
            ch.attr(NioPlayersController.connectedUserKey).set(user)

            ch.pipeline()
              .addLast(new JsonObjectDecoder())
              .addLast(new RequestDecoder())
              .addLast(new TradeCraftServerHandler(self))
              .addLast(new ResponseEncoder())

            // upon connecting we kick off the login form
            enqueue(UserCommand(user, CommandInfo("command", "auth/login", null)))
          }
        })
        .option(ChannelOption.SO_BACKLOG, int2Integer(128))
        .childOption(ChannelOption.SO_KEEPALIVE, boolean2Boolean(true))

      // Bind and start to accept incoming connections.
      channelFuture = Some(b.bind(8088).sync)
      System.out.println("Listening on port 8088 for users.")
      
      // block until closed
      channelFuture.map(c => c.channel().closeFuture().sync())
    } finally {
      workerGroup.shutdownGracefully()
      masterGroup.shutdownGracefully()
    }
  }

  override def close(): Unit = {
    super.close()
  }

  def enqueue(userCommand: UserCommand): Unit = {
    queue.add(userCommand)
  }
}
