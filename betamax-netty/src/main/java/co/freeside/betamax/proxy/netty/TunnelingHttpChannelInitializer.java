package co.freeside.betamax.proxy.netty;

import io.netty.channel.*;
import io.netty.channel.socket.*;

public class TunnelingHttpChannelInitializer extends HttpChannelInitializer {

    public TunnelingHttpChannelInitializer(int workerThreads, ChannelHandler handler) {
        super(workerThreads, handler);
    }

    @Override
    public void initChannel(SocketChannel channel) throws Exception {
        super.initChannel(channel);

        channel.pipeline().addAfter("decoder", "connector", new ProxyConnectHandler());
    }
}
