package co.freeside.betamax.proxy.netty;

import io.netty.buffer.*;
import io.netty.channel.*;
import io.netty.util.*;

public final class RelayHandler extends ChannelInboundHandlerAdapter {
    private static final String name = "RELAY_HANDLER";

    public static String getName() {
        return name;
    }

    private final Channel relayChannel;

    public RelayHandler(Channel relayChannel) {
        this.relayChannel = relayChannel;
    }

    @Override
    public void channelActive(ChannelHandlerContext context) {
        context.writeAndFlush(Unpooled.EMPTY_BUFFER);
    }

    @Override
    public void channelRead(ChannelHandlerContext context, Object message) {
        if (relayChannel.isActive()) {
            relayChannel.writeAndFlush(message);
        } else {
            ReferenceCountUtil.release(message);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext context) {
        NettyHelpers.closeOnFlush(relayChannel);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause) {
        cause.printStackTrace();
        context.close();
    }

}
