package co.freeside.betamax.proxy.netty;

import io.netty.channel.*;
import io.netty.channel.nio.*;
import io.netty.channel.socket.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.*;

/**
 * Configures up a channel to handle HTTP requests and responses.
 */
public class HttpChannelInitializer extends ChannelInitializer<SocketChannel> {

    public static final int MAX_CONTENT_LENGTH = 65536;

    static final String HANDLER_HTTP_DECODER = "decoder";
    static final String HANDLER_HTTP_AGGREGATOR = "aggregator";
    static final String HANDLER_HTTP_ENCODER = "encoder";
    static final String HANDLER_CHUNKED_WRITER = "chunkedWriter";

    private final ChannelHandler handler;
    protected final EventLoopGroup workerGroup;

    public HttpChannelInitializer(int workerThreads, ChannelHandler handler) {
        this.handler = handler;

        if (workerThreads > 0) {
            workerGroup = new NioEventLoopGroup(workerThreads);
        } else {
            workerGroup = null;
        }
    }

    @Override
    public void initChannel(SocketChannel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast(HANDLER_HTTP_DECODER, new HttpRequestDecoder());
        pipeline.addLast(HANDLER_HTTP_AGGREGATOR, new HttpObjectAggregator(MAX_CONTENT_LENGTH));
        pipeline.addLast(HANDLER_HTTP_ENCODER, new HttpResponseEncoder());
        pipeline.addLast(HANDLER_CHUNKED_WRITER, new ChunkedWriteHandler());
        if (workerGroup == null) {
            pipeline.addLast("betamaxHandler", handler);
        } else {
            pipeline.addLast(workerGroup, "betamaxHandler", handler);
        }
    }
}
