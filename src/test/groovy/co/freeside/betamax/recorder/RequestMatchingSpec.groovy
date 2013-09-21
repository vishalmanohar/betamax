package co.freeside.betamax.recorder

import co.freeside.betamax.Recorder
import co.freeside.betamax.httpclient.BetamaxRoutePlanner
import co.freeside.betamax.matcher.rules.HostMatcher
import co.freeside.betamax.util.httpbuilder.BetamaxRESTClient
import groovyx.net.http.RESTClient
import spock.lang.*
import static co.freeside.betamax.MatchRule.host
import static co.freeside.betamax.util.FileUtils.newTempDir
import static org.apache.http.HttpHeaders.VIA

@Issue('https://github.com/robfletcher/betamax/issues/9')
class RequestMatchingSpec extends Specification {

	@Shared @AutoCleanup('deleteDir') File tapeRoot = newTempDir('tapes')
	@Shared Recorder recorder = new Recorder(tapeRoot: tapeRoot)
	@Shared RESTClient http = new BetamaxRESTClient()

	void setupSpec() {
		tapeRoot.mkdirs()
	}

	@Unroll('#method request for #uri returns "#responseText"')
	void 'default match is method and uri'() {
		given:
		new File(tapeRoot, 'method_and_uri_tape.yaml').text = '''\
!tape
name: method and uri tape
interactions:
- recorded: 2011-08-23T20:24:33.000Z
  request:
    method: GET
    uri: http://xkcd.com/
  response:
    status: 200
    headers: {Content-Type: text/plain}
    body: get method response from xkcd.com
- recorded: 2011-08-23T20:24:33.000Z
  request:
    method: POST
    uri: http://xkcd.com/
  response:
    status: 200
    headers: {Content-Type: text/plain}
    body: post method response from xkcd.com
- recorded: 2011-08-23T20:24:33.000Z
  request:
    method: GET
    uri: http://qwantz.com/
  response:
    status: 200
    headers: {Content-Type: text/plain}
    body: get method response from qwantz.com
'''
		when:
		def response = recorder.withTape('method and uri tape') {
			http."$method"(uri: uri)
		}

		then:
		response.data.text == responseText

		where:
		method | uri
		'get'  | 'http://xkcd.com/'
		'post' | 'http://xkcd.com/'
		'get'  | 'http://qwantz.com/'

		responseText = "$method method response from ${uri.toURI().host}"
	}

	void 'can match based on host'() {
		given:
		new File(tapeRoot, 'host_match_tape.yaml').text = '''\
!tape
name: host match tape
interactions:
- recorded: 2011-08-23T20:24:33.000Z
  request:
    method: GET
    uri: http://xkcd.com/936/
  response:
    status: 200
    headers: {Content-Type: text/plain}
    body: get method response from xkcd.com
'''
		when:
		def response = recorder.withTape('host match tape', [match: [new HostMatcher()]]) {
			http.get(uri: 'http://xkcd.com/875/')
		}

		then:
		response.getFirstHeader(VIA)?.value == 'Betamax'
		response.data.text == 'get method response from xkcd.com'
	}
}
