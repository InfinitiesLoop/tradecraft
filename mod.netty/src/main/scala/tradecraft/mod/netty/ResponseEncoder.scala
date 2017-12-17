package tradecraft.mod.netty

import java.nio.charset.StandardCharsets

import io.netty.channel.{ChannelHandlerContext, ChannelOutboundHandlerAdapter, ChannelPromise}
import tradecraft.core.Response

class ResponseEncoder extends ChannelOutboundHandlerAdapter {
  override def write(ctx: ChannelHandlerContext, msg: Any, promise: ChannelPromise): Unit = {
    val response = msg.asInstanceOf[Response]
    val jsonBytes = response.toJson.getBytes(StandardCharsets.UTF_8)

    val encoded = ctx.alloc.buffer(jsonBytes.length)
    encoded.writeBytes(jsonBytes)
    ctx.write(encoded, promise)
  }
}
