package co.freeside.betamax.proxy.netty;

import io.netty.channel.ChannelHandlerContext;

public interface CallbackNotifier {
    void onSuccess(ChannelHandlerContext outboundCtx);
    void onFailure(ChannelHandlerContext outboundCtx, Throwable cause);
}
