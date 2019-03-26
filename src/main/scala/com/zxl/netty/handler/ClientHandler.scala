package com.zxl.netty.handler

import com.zxl.netty.message.RegiesterMsg
import io.netty.buffer.{ByteBuf, Unpooled}
import io.netty.channel.{ChannelHandlerContext, ChannelInboundHandlerAdapter}

class ClientHandler extends ChannelInboundHandlerAdapter {

  override def channelActive(ctx: ChannelHandlerContext): Unit = {
//    println("channelActive")
//    val content = "hello server"
//    ctx.writeAndFlush(Unpooled.copiedBuffer(content.getBytes("UTF-8")))
      ctx.writeAndFlush(RegiesterMsg("hello server"))
  }


  override def channelRead(ctx: ChannelHandlerContext, msg: scala.Any): Unit = {
//    println("channelRead")
//    val byteBuf = msg.asInstanceOf[ByteBuf]
//    val bytes = new Array[Byte](byteBuf.readableBytes())
//    byteBuf.readBytes(bytes)
//    val message = new String(bytes, "UTF-8")
//    println(message)

  }

  override def channelReadComplete(ctx: ChannelHandlerContext): Unit = {
    println("channelReadComplete")
    ctx.flush()
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = {
    println("exceptionCaught")
    ctx.close()
  }
}
