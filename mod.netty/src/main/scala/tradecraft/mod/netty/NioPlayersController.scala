package tradecraft.mod.netty

import tradecraft.core.{PlayersController, UserCommand}
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.{ChannelFuture, ChannelInitializer, ChannelOption}
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.codec.json.JsonObjectDecoder

class NioPlayersController extends PlayersController {
  var channelFuture: Option[ChannelFuture] = None
  var masterGroup = new NioEventLoopGroup
  var workerGroup = new NioEventLoopGroup

  override def run(): Unit = {
    val self = this
    try {
      val b = new ServerBootstrap
      b.group(masterGroup, workerGroup)
        .channel(classOf[NioServerSocketChannel])
        .childHandler(new ChannelInitializer[SocketChannel] {
          override def initChannel(ch: SocketChannel): Unit = {
            ch.pipeline()
              .addLast(new JsonObjectDecoder())
              .addLast(new UserMessageDecoder())
              .addLast(new AuthHandler(self))
              .addLast(new TradeCraftServerHandler(self))
          }
        })
        .option(ChannelOption.SO_BACKLOG, int2Integer(128))
        .childOption(ChannelOption.SO_KEEPALIVE, boolean2Boolean(true))

      // Bind and start to accept incoming connections.
      channelFuture = Some(b.bind(8088).sync)
      // block until closed
      channelFuture.map(c => c.channel().closeFuture().sync())
    } finally {
      workerGroup.shutdownGracefully()
      masterGroup.shutdownGracefully()
    }
  }

  def playerAuthenticated(userId: Long, userName: String): Unit = {
    queue.add(UserCommand(userId, UserCommand.Refresh))
  }

  override def close(): Unit = {
    super.close()
  }

  def enqueue(userCommand: UserCommand): Unit = {
    queue.add(userCommand)
  }
}
