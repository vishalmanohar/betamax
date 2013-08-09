package co.freeside.betamax.proxy.netty;

import java.net.*;
import io.netty.channel.*;
import io.netty.channel.socket.*;

public class TunnelingHttpChannelInitializer extends HttpChannelInitializer {

    static final String HANDLER_HTTP_CONNECT = "connector";

    private final SocketAddress proxyAddress;

    public TunnelingHttpChannelInitializer(int workerThreads, ChannelHandler handler, SocketAddress proxyAddress) {
        super(workerThreads, handler);
        this.proxyAddress = proxyAddress;
    }

    @Override
    public void initChannel(SocketChannel channel) throws Exception {
        super.initChannel(channel);

        channel.pipeline().addAfter("betamaxHandler", HANDLER_HTTP_CONNECT, new ProxyConnectHandler(proxyAddress));
    }
}
