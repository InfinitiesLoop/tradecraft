package tradecraft.mod.netty

import io.netty.channel.{ChannelHandlerContext, ChannelInboundHandlerAdapter}
import io.netty.util.{AttributeKey, ReferenceCountUtil}

object AuthHandler {
  val AuthAttributeKey: AttributeKey[Long] = AttributeKey.valueOf("authhandler.userid")
}

class AuthHandler(val playersController: NioPlayersController) extends ChannelInboundHandlerAdapter {

  override def channelRegistered(ctx: ChannelHandlerContext): Unit = {
    ctx.channel().attr(AuthHandler.AuthAttributeKey).set(0)
    super.channelRegistered(ctx)
  }

  override def channelUnregistered(ctx: ChannelHandlerContext): Unit = {
    super.channelUnregistered(ctx)
  }

  override def channelRead(ctx: ChannelHandlerContext, msg: Any): Unit = {
    ReferenceCountUtil.release(msg)
    val userId = ctx.channel().attr(AuthHandler.AuthAttributeKey).get()
    if (userId > 0) {
      super.channelRead(ctx, msg)
      return
    }

    // user not authenticated, so we must get an Auth user command,
    // ignoring anything else.
    msg.asInstanceOf[UserMessage] match {
      case UserMessage(None, Some(AuthMessage(name, pw))) =>
        // try to auth
        // TODO: actually do it
        // TODO: log4j
        System.out.println(s"Authenticated user ${name}.")
        ctx.channel().attr(AuthHandler.AuthAttributeKey).set(88)
        playersController.playerAuthenticated(88, name)
      case _ =>
        // not the right message type of an unauthenticated channel
    }
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = {
    // lame for now
    cause.printStackTrace()
    ctx.close
  }
}
