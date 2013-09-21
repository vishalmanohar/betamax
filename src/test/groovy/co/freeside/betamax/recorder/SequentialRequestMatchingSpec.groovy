package co.freeside.betamax.recorder

import co.freeside.betamax.Recorder
import co.freeside.betamax.TapeMode
import co.freeside.betamax.matcher.rules.HostMatcher
import co.freeside.betamax.matcher.rules.URIMatcher
import co.freeside.betamax.util.httpbuilder.BetamaxRESTClient
import groovyx.net.http.RESTClient
import spock.lang.*

import static co.freeside.betamax.util.FileUtils.newTempDir
import static org.apache.http.HttpHeaders.VIA

class SequentialRequestMatchingSpec extends Specification {

	@Shared @AutoCleanup('deleteDir') File tapeRoot = newTempDir('tapes')
	@Shared Recorder recorder = new Recorder(tapeRoot: tapeRoot)
	@Shared RESTClient http = new BetamaxRESTClient()

	void setupSpec() {
		tapeRoot.mkdirs()
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
    uri: http://xkcd.com/875/
  response:
    status: 200
    headers: {Content-Type: text/plain}
    body: get method response from xkcd.com
- recorded: 2011-08-23T20:24:33.000Z
  request:
    method: GET
    uri: http://xkcd.com/876/
  response:
    status: 200
    headers: {Content-Type: text/plain}
    body: get 876 method response from xkcd.com
- recorded: 2011-08-23T20:24:33.000Z
  request:
    method: GET
    uri: http://xkcd.com/875/
  response:
    status: 200
    headers: {Content-Type: text/plain}
    body: get new method response from xkcd.com
'''
		when:

        def response = recorder.withTape('host match tape', [mode: TapeMode.READ_SEQUENTIAL ,match: [new URIMatcher()]]) {
              [
               http.get(uri: 'http://xkcd.com/876/'),
               http.get(uri: 'http://xkcd.com/875/'),
               http.get(uri: 'http://xkcd.com/875/'),
               http.get(uri: 'http://xkcd.com/875/'),
              ]
        }
        then:
            response[0].data.text == 'get 876 method response from xkcd.com'
            response[1].data.text == 'get method response from xkcd.com'
            response[2].data.text == 'get new method response from xkcd.com'
            response[3].data.text == 'get new method response from xkcd.com'
	}
}
