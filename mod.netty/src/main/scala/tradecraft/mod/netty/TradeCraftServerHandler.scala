package tradecraft.mod.netty

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import tradecraft.core.{Request, UserCommand}

class TradeCraftServerHandler(playersController: NioPlayersController) extends ChannelInboundHandlerAdapter {

  override def channelRegistered(ctx: ChannelHandlerContext): Unit = {
    super.channelRegistered(ctx)
  }

  override def channelRead(ctx: ChannelHandlerContext, msg: Any): Unit = {
    val request = msg.asInstanceOf[Request]
    val userId = ctx.channel().attr(AuthHandler.AuthAttributeKey).get()
    val connectedUser = ctx.channel().attr(NioPlayersController.connectedUserKey).get()
    playersController.enqueue(UserCommand(userId, connectedUser, request.command.getOrElse(UserCommand.Refresh)))
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = {
    // lame for now
    cause.printStackTrace()
    ctx.close
  }
}
