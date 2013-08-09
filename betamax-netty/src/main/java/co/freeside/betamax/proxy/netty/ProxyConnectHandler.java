package co.freeside.betamax.proxy.netty;

import java.net.*;
import io.netty.bootstrap.*;
import io.netty.channel.*;
import io.netty.channel.socket.nio.*;
import io.netty.handler.codec.http.*;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Values.KEEP_ALIVE;
import static io.netty.handler.codec.http.HttpMethod.*;

public class ProxyConnectHandler extends SimpleChannelInboundHandler<HttpRequest> {

    private final SocketAddress proxyAddress;
    private final Bootstrap bootstrap = new Bootstrap();

    public ProxyConnectHandler(SocketAddress proxyAddress) {
        this.proxyAddress = proxyAddress;
    }

    @Override
    public boolean acceptInboundMessage(Object message) throws Exception {
        System.err.printf("Evaluating %s%n", message);
        return super.acceptInboundMessage(message) && isConnectRequest((HttpRequest) message);
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext context, final HttpRequest request) {
        CallbackNotifier callback = new CallbackNotifier() {
            @Override
            public void onSuccess(final ChannelHandlerContext outboundContext) {
                context.channel()
                        .writeAndFlush(createConnectResponse())
                        .addListener(new ChannelFutureListener() {
                            @Override
                            public void operationComplete(ChannelFuture channelFuture) {
                                outboundContext.channel().pipeline().addLast(new RelayHandler(context.channel()));
                                context.channel().pipeline().addLast(new RelayHandler(outboundContext.channel()));
                            }
                        });
            }

            @Override
            public void onFailure(ChannelHandlerContext outboundCtx, Throwable cause) {
                HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_GATEWAY);
                context.channel().writeAndFlush(response);
                NettyHelpers.closeOnFlush(context.channel());
            }
        };

        final Channel inboundChannel = context.channel();
        bootstrap.group(inboundChannel.eventLoop())
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new DirectClientInitializer(callback));

        bootstrap.connect(proxyAddress);
    }

    private HttpResponse createConnectResponse() {
        DefaultHttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        response.headers().add(CONNECTION, KEEP_ALIVE);
        response.headers().add("Proxy-Connection", KEEP_ALIVE);
        return response;
    }

    private boolean isConnectRequest(HttpRequest request) {
        return CONNECT.equals(request.getMethod());
    }
}
