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

package co.freeside.betamax.matcher.rules

import co.freeside.betamax.message.Request

/**
 * Implements a request matching rule for finding recordings on a tape.
 */
class HeadersMatcher implements Matcher {

    List<String> keys = null;

    HeadersMatcher(){
    }

    HeadersMatcher(List<String> keys){
        this.keys = keys;
    }

    int compare(Request a, Request b) {
        if(keys == null){
            def result = a.headers.size() <=> b.headers.size()
            if (result != 0) {
                return result
            }

            if (a.headers.keySet() != b.headers.keySet()) {
                return -1 // wouldn't work if we cared about ordering...
            }
            for (header in a.headers) {
                result = header.value <=> b.headers[header.key]
                if (result != 0) {
                    return result
                }
            }
            return 0;
        } else {
            int compare = 0
            keys.each {
                compare += a.headers[it] <=> b.headers[it]
            }
            return compare
        }
    }
}
