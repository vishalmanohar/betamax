package co.freeside.betamax.tape

import co.freeside.betamax.message.Request

class MatchedInteractions {
    Map<RequestMatcher, Integer> matchedInteractions = new HashMap<RequestMatcher, Integer>();

    int getIndex(Request request){
        def result = this.findRequest(request)
        result == null ? -1 : result.value
    }

    void setIndex(RequestMatcher requestMatcher, Integer index){
        def result = this.findRequest(requestMatcher.request)
        if(result == null) matchedInteractions.put(requestMatcher, index)
        else result.value = index;
    }

    private java.util.Map.Entry findRequest(Request request){
        return matchedInteractions.find { it.key.matches(request) }
    }
}

