package co.freeside.betamax.proxy.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public final class DirectClientInitializer extends ChannelInitializer<SocketChannel> {

    private final CallbackNotifier callbackNotifier;

    public DirectClientInitializer(CallbackNotifier callbackNotifier) {
        this.callbackNotifier = callbackNotifier;
    }

    @Override
    public void initChannel(SocketChannel socketChannel) {
        ChannelPipeline channelPipeline = socketChannel.pipeline();
        channelPipeline.addLast(DirectClientHandler.getName(), new DirectClientHandler(callbackNotifier));
    }
}
