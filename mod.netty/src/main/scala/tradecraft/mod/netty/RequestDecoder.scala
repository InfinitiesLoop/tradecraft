package tradecraft.mod.netty

import java.nio.charset.StandardCharsets

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import org.json4s.DefaultFormats
import org.json4s.native.JsonParser
import tradecraft.core.Request

class RequestDecoder extends ByteToMessageDecoder {
  protected def decode(ctx: ChannelHandlerContext, in: ByteBuf, out: java.util.List[Object]): Unit = {
    implicit val formats: DefaultFormats = DefaultFormats

    val byteBuf = in.readBytes(in.readableBytes())
    val json = byteBuf.toString(StandardCharsets.UTF_8)

    (JsonParser.parseOpt(json) match {
      case Some(obj) =>
        obj.extractOpt[Request]
      case _ =>
        None
    }).map(um => out.add(um))
  }
}
