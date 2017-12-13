package tradecraft.mod.netty

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import tradecraft.core.UserCommand

class TradeCraftServerHandler(playersController: NioPlayersController) extends ChannelInboundHandlerAdapter {

  override def channelRegistered(ctx: ChannelHandlerContext): Unit = {
    super.channelRegistered(ctx)
  }

  override def channelRead(ctx: ChannelHandlerContext, msg: Any): Unit = {
    val um = msg.asInstanceOf[UserMessage]
    val userName = ctx.channel().attr(AuthHandler.AuthAttributeKey).get()
    playersController.enqueue(UserCommand(userName, um.command.getOrElse(UserCommand.Refresh)))
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = {
    // lame for now
    cause.printStackTrace()
    ctx.close
  }
}
