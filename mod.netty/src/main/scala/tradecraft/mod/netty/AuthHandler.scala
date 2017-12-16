package tradecraft.mod.netty

import io.netty.channel.{ChannelHandlerContext, ChannelInboundHandlerAdapter}
import io.netty.util.{AttributeKey, ReferenceCountUtil}
import tradecraft.core.{AuthRequest, Request}

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
    val userId = ctx.channel().attr(AuthHandler.AuthAttributeKey).get()
    if (userId > 0) {
      // this channel is already authenticated, so this handler can just ignore everything.
      super.channelRead(ctx, msg)
      return
    }

    val connectedUser = ctx.channel().attr(NioPlayersController.connectedUserKey).get()
    try {
      // user not authenticated, so we must get an Auth user command,
      // ignoring anything else.
      msg.asInstanceOf[Request] match {
        case Request(None, Some(AuthRequest(name, pw))) =>
          // try to auth
          // TODO: actually do it
          // TODO: log4j
          System.out.println(s"Authenticated user ${name}.")
          ctx.channel().attr(AuthHandler.AuthAttributeKey).set(88)
          playersController.playerAuthenticated(88, connectedUser, name)
        case _ =>
          // not the right message type of an unauthenticated channel,
          // ignore it basically.
      }
    } finally {
      ReferenceCountUtil.release(msg)
    }
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = {
    // lame for now
    cause.printStackTrace()
    ctx.close
  }
}
