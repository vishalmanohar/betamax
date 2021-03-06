package co.freeside.betamax.proxy.netty;

import java.net.*;
import io.netty.bootstrap.*;
import io.netty.channel.*;
import io.netty.channel.nio.*;
import io.netty.channel.socket.nio.*;

/**
 * A Netty-based implementation of the Betamax proxy server.
 */
public class NettyBetamaxServer {

    private final int port;
    private final ChannelInitializer channelInitializer;
    private EventLoopGroup group;
    private Channel channel;

    public NettyBetamaxServer(int port, ChannelInitializer channelInitializer) {
        this.port = port;
        this.channelInitializer = channelInitializer;
    }

    public InetSocketAddress run() throws Exception {
        group = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(group)
                .channel(NioServerSocketChannel.class)
                .childHandler(channelInitializer)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

        channel = bootstrap.bind("localhost", port).sync().channel();
        return (InetSocketAddress) channel.localAddress();
    }

    public void shutdown() throws InterruptedException {
        if (channel != null) channel.close().sync();
        if (group != null) group.shutdownGracefully().sync();
    }

}