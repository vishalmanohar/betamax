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

    private final int standardPort;
    private final int securePort;
    private final ChannelInitializer standardChannelInitializer;
    private final ChannelInitializer secureChannelInitializer;
    private EventLoopGroup group;
    private Channel standardChannel;
    private Channel secureChannel;

    public NettyBetamaxServer(int standardPort, int securePort, ChannelInitializer standardChannelInitializer, ChannelInitializer secureChannelInitializer) {
        this.standardPort = standardPort;
        this.securePort = securePort;
        this.standardChannelInitializer = standardChannelInitializer;
        this.secureChannelInitializer = secureChannelInitializer;
    }

    public InetSocketAddress run() throws Exception {
        group = new NioEventLoopGroup();

        standardChannel = startChannel(standardPort, standardChannelInitializer);
        secureChannel = startChannel(securePort, secureChannelInitializer);

        return (InetSocketAddress) standardChannel.localAddress();
    }

    private Channel startChannel(int port, ChannelInitializer channelInitializer) throws InterruptedException {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(group)
                .channel(NioServerSocketChannel.class)
                .childHandler(channelInitializer)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);
        return bootstrap.bind("localhost", port).sync().channel();
    }

    public void shutdown() throws InterruptedException {
        if (standardChannel != null) standardChannel.close().sync();
        if (secureChannel != null) secureChannel.close().sync();
        if (group != null) group.shutdownGracefully().sync();
    }

}