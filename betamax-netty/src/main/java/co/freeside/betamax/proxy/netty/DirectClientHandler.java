package co.freeside.betamax.proxy.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public final class DirectClientHandler extends ChannelInboundHandlerAdapter {
    private static final String name = "DIRECT_CLIENT_HANDLER";

    public static String getName() {
        return name;
    }
    private final CallbackNotifier cb;

    public DirectClientHandler(CallbackNotifier cb) {
        this.cb = cb;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.pipeline().remove(this);
        cb.onSuccess(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable throwable) {
        cb.onFailure(ctx, throwable);
    }
}
