package co.freeside.betamax.proxy.netty;

import javax.net.ssl.*;
import co.freeside.betamax.proxy.netty.ssl.*;
import io.netty.channel.*;
import io.netty.channel.socket.*;
import io.netty.handler.ssl.*;

public class HttpsChannelInitializer extends HttpChannelInitializer {

    public HttpsChannelInitializer(int workerThreads, ChannelHandler handler) {
        super(workerThreads, handler);
    }

    @Override
    public void initChannel(SocketChannel channel) throws Exception {
        super.initChannel(channel);

        SSLEngine engine = SslContextFactory.getServerContext().createSSLEngine();
        engine.setUseClientMode(false);

        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addFirst("ssl", new SslHandler(engine));
    }
}
