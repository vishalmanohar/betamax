package co.freeside.betamax.tape

import co.freeside.betamax.matcher.rules.BodyMatcher
import co.freeside.betamax.matcher.rules.HeadersMatcher
import co.freeside.betamax.matcher.rules.HostMatcher
import co.freeside.betamax.matcher.rules.PathMatcher
import co.freeside.betamax.message.tape.RecordedRequest
import co.freeside.betamax.util.message.BasicRequest
import spock.lang.Issue
import spock.lang.Specification

import static org.apache.http.HttpHeaders.ACCEPT_ENCODING
import static org.apache.http.HttpHeaders.CACHE_CONTROL

@Issue('https://github.com/robfletcher/betamax/issues/9')
class RequestMatcherSpec extends Specification {

	void 'by default matches method and url'() {
		given:
		def request1 = new RecordedRequest(method: 'GET', uri: 'http://freeside.co/betamax'.toURI())
		def request2 = new RecordedRequest(method: 'HEAD', uri: 'http://freeside.co/betamax'.toURI())
		def request3 = new RecordedRequest(method: 'GET', uri: 'http://freeside.co/betamax?q=1'.toURI())

		and:
		def request = new BasicRequest(method: 'GET', uri: 'http://freeside.co/betamax'.toURI())
		def requestMatcher = new RequestMatcher(request)

		expect:
		requestMatcher.matches(request1)
		!requestMatcher.matches(request2)
		!requestMatcher.matches(request3)
	}

	void 'can match host'() {
		given:
		def request1 = new RecordedRequest(method: 'GET', uri: 'http://freeside.co/betamax'.toURI())
		def request2 = new RecordedRequest(method: 'GET', uri: 'http://freeside.co/grails-fields'.toURI())
		def request3 = new RecordedRequest(method: 'HEAD', uri: 'http://freeside.co/betamax'.toURI())
		def request4 = new RecordedRequest(method: 'GET', uri: 'http://icanhascheezburger.com/'.toURI())

		and:
		def request = new BasicRequest(method: 'GET', uri: 'http://freeside.co/betamax'.toURI())
		def requestMatcher = new RequestMatcher(request, new HostMatcher())

		expect:
		requestMatcher.matches(request1)
		requestMatcher.matches(request2)
		requestMatcher.matches(request3)
		!requestMatcher.matches(request4)
	}

	void 'can match path'() {
		given:
		def request1 = new RecordedRequest(method: 'GET', uri: 'http://freeside.co/betamax'.toURI())
		def request2 = new RecordedRequest(method: 'GET', uri: 'http://robfletcher.github.com/grails-enhanced-scaffolding'.toURI())
		def request3 = new RecordedRequest(method: 'HEAD', uri: 'http://freeside.co/betamax'.toURI())
		def request4 = new RecordedRequest(method: 'GET', uri: 'http://icanhascheezburger.com/betamax'.toURI())

		and:
		def request = new BasicRequest(method: 'GET', uri: 'http://freeside.co/betamax'.toURI())
		def requestMatcher = new RequestMatcher(request, new PathMatcher())

		expect:
		requestMatcher.matches(request1)
		!requestMatcher.matches(request2)
		requestMatcher.matches(request3)
		requestMatcher.matches(request4)
	}

	void 'can match headers'() {
		given:
		def request1 = new RecordedRequest(method: 'GET', uri: 'http://freeside.co/betamax'.toURI(), headers: [(ACCEPT_ENCODING): 'gzip, deflate'])
		def request2 = new RecordedRequest(method: 'GET', uri: 'http://icanhascheezburger.com/'.toURI(), headers: [(ACCEPT_ENCODING): 'gzip, deflate'])
		def request3 = new RecordedRequest(method: 'GET', uri: 'http://freeside.co/betamax'.toURI(), headers: [(ACCEPT_ENCODING): 'none'])
		def request4 = new RecordedRequest(method: 'GET', uri: 'http://freeside.co/betamax'.toURI(), headers: [(ACCEPT_ENCODING): 'gzip, deflate', (CACHE_CONTROL): 'no-cache'])

		and:
		def request = new BasicRequest(method: 'GET', uri: 'http://freeside.co/betamax'.toURI(), headers: [(ACCEPT_ENCODING): ['gzip', 'deflate']])
		def requestMatcher = new RequestMatcher(request, new HeadersMatcher())

		expect:
		requestMatcher.matches(request1)
		requestMatcher.matches(request2)
		!requestMatcher.matches(request3)
		!requestMatcher.matches(request4)
	}

	void 'can match post body'() {
		given:
		def request1 = new RecordedRequest(method: 'POST', uri: 'http://freeside.co/betamax'.toURI(), body: 'q=1')
		def request2 = new RecordedRequest(method: 'POST', uri: 'http://freeside.co/betamax'.toURI(), body: 'q=2')
		def request3 = new RecordedRequest(method: 'POST', uri: 'http://freeside.co/betamax'.toURI(), body: 'q=1&r=1')

		and:
		def request = new BasicRequest(method: 'POST', uri: 'http://freeside.co/betamax'.toURI(), body: 'q=1')
		def requestMatcher = new RequestMatcher(request, new BodyMatcher())

		expect:
		requestMatcher.matches(request1)
		!requestMatcher.matches(request2)
		!requestMatcher.matches(request3)
	}

}
