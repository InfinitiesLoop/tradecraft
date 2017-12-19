package tradecraft.mod.netty

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import tradecraft.core.{CommandInfo, Request, UserCommand}

class TradeCraftServerHandler(playersController: NioPlayersController) extends ChannelInboundHandlerAdapter {

  override def channelRegistered(ctx: ChannelHandlerContext): Unit = {
    super.channelRegistered(ctx)
  }

  override def channelRead(ctx: ChannelHandlerContext, msg: Any): Unit = {
    val request = msg.asInstanceOf[Request]
    val connectedUser = ctx.channel().attr(NioPlayersController.connectedUserKey).get()

    playersController.enqueue(UserCommand(
      connectedUser,
      // todo: maybe CommandInfo is lame and we should just forward Request
      CommandInfo(
        request.`type`,
        request.route,
        request.param.getOrElse(""))
    ))
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = {
    // lame for now
    cause.printStackTrace()
    ctx.close
  }
}
