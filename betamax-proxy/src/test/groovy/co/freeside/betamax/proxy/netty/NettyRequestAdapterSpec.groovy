package co.freeside.betamax.proxy.netty

import io.netty.buffer.Unpooled
import io.netty.handler.codec.http.*
import spock.lang.*
import static io.netty.handler.codec.http.HttpHeaders.Names.*
import static io.netty.handler.codec.http.HttpMethod.GET
import static io.netty.util.CharsetUtil.UTF_8

@Unroll
class NettyRequestAdapterSpec extends Specification {

	FullHttpRequest nettyRequest = Mock(FullHttpRequest)

	void 'request can read basic fields'() {
		given:
		nettyRequest.method >> GET
		nettyRequest.uri >> 'http://freeside.co/betamax'

		and:
		def request = new NettyRequestAdapter(nettyRequest)

		expect:
		request.method == 'GET'
		request.uri == 'http://freeside.co/betamax'.toURI()
	}

	void 'request target includes query string'() {
		given:
		nettyRequest.uri >> 'http://freeside.co/betamax?q=1'

		and:
		def request = new NettyRequestAdapter(nettyRequest)

		expect:
		request.uri == new URI('http://freeside.co/betamax?q=1')
	}

	void 'request can read headers'() {
		given:
		def headers = new DefaultHttpHeaders()
		headers.add(IF_NONE_MATCH, "abc123")
		headers.add(ACCEPT_ENCODING, ["gzip", "deflate"])
		nettyRequest.headers() >> headers

		and:
		def request = new NettyRequestAdapter(nettyRequest)

		expect:
		request.getHeader(IF_NONE_MATCH) == 'abc123'
		request.getHeader(ACCEPT_ENCODING) == 'gzip, deflate'
	}

	void 'request headers are immutable'() {
		given:
		nettyRequest.headers() >> HttpHeaders.EMPTY_HEADERS

		and:
		def request = new NettyRequestAdapter(nettyRequest)

		when:
		request.headers[IF_NONE_MATCH] = ['abc123']

		then:
		thrown UnsupportedOperationException
	}

	void 'request body is readable as text'() {
		given:
		def bodyBytes = bodyText.getBytes('ISO-8859-1')
		nettyRequest.content() >> Unpooled.copiedBuffer(bodyBytes)
		def headers = new DefaultHttpHeaders()
		headers.set(CONTENT_TYPE, "application/x-www-form-urlencoded; charset=ISO-8859-1")
		nettyRequest.headers() >> headers

		and:
		def request = new NettyRequestAdapter(nettyRequest)

		expect:
		request.hasBody()
		request.bodyAsText.text == bodyText

		where:
		bodyText = "value=\u00a31"
	}

	void 'request body is readable as binary'() {
		given:
		def body = 'value=\u00a31'.getBytes('ISO-8859-1')
		nettyRequest.content() >> Unpooled.copiedBuffer(body)
		def headers = new DefaultHttpHeaders()
		headers.set(CONTENT_TYPE, "application/x-www-form-urlencoded; charset=ISO-8859-1")
		nettyRequest.headers() >> headers

		and:
		def request = new NettyRequestAdapter(nettyRequest)

		expect:
		request.hasBody()
		request.bodyAsBinary.bytes == body
	}

	void "request #description if the content buffer is #contentDescription"() {
		given:
		nettyRequest.content() >> content

		and:
		def request = new NettyRequestAdapter(nettyRequest)

		expect:
		request.hasBody() == consideredToHaveBody

		where:
		content                               | consideredToHaveBody
		Unpooled.copiedBuffer("O HAI", UTF_8) | true
		Unpooled.EMPTY_BUFFER                 | false
		null                                  | false

		description = consideredToHaveBody ? "has a body" : "does not have a body"
		contentDescription = content ? "${content.readableBytes()} bytes long" : "null"
	}

}
