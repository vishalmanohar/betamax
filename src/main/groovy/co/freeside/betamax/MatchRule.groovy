/*
 * Copyright 2011 Rob Fletcher
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package co.freeside.betamax

import co.freeside.betamax.matcher.rules.*

/**
 * Implements a request matching rule for finding recordings on a tape.
 */
enum MatchRule {
    method(new MethodMatcher()),
	uri(new URIMatcher()),
	host(new HostMatcher()),
	path(new PathMatcher()),
	port(new PortMatcher()),
	query(new QueryMatcher()),
	fragment(new FragmentMatcher()),
    headers(new HeadersMatcher()),
	body (new BodyMatcher());

    private  final Matcher matcher;

    MatchRule(Matcher matcher){
        this.matcher = matcher;
    }

    Matcher getMatcher(){
       this.matcher;
    }
}
