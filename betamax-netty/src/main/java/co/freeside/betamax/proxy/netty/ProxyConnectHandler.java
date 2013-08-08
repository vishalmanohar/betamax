package co.freeside.betamax.proxy.netty;

import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import static io.netty.handler.codec.http.HttpMethod.*;
import static io.netty.handler.codec.http.HttpVersion.*;

public class ProxyConnectHandler extends SimpleChannelInboundHandler<HttpRequest> {

    @Override
    public boolean acceptInboundMessage(Object message) throws Exception {
        System.err.printf("Evaluating %s%n", message);
        return super.acceptInboundMessage(message) && CONNECT.equals(((HttpRequest) message).getMethod());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext context, HttpRequest request) {
        System.err.println("I actually got a CONNECT. Now what?");
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.METHOD_NOT_ALLOWED);
        context.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

}
