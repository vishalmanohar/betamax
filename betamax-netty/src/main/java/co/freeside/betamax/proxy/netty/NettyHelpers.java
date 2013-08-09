package co.freeside.betamax.proxy.netty;

import io.netty.buffer.*;
import io.netty.channel.*;

class NettyHelpers {
    public static void closeOnFlush(Channel ch) {
        if (ch.isActive()) {
            ch.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }

    private NettyHelpers() {}
}
