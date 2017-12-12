package tradecraft.mod.netty

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter

class TradeCraftServerHandler(playersController: NioPlayersController) extends ChannelInboundHandlerAdapter {
  override def channelRead(ctx: ChannelHandlerContext, msg: Any): Unit = {
    System.out.println("GOT A MESSAGE")
    // release the message
    msg.asInstanceOf[ByteBuf].release
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = {
    // lame for now
    cause.printStackTrace()
    ctx.close
  }
}
