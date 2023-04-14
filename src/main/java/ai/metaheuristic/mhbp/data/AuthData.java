/*
 *    Copyright 2023, Sergio Lissner, Innovation platforms, LLC
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package ai.metaheuristic.mhbp.data;

import ai.metaheuristic.mhbp.Enums;
import lombok.*;
import org.springframework.data.domain.Slice;

import java.util.Collections;

/**
 * @author Sergio Lissner
 * Date: 4/13/2023
 * Time: 12:09 AM
 */
public class AuthData {

    public static class SimpleAuth {
        public long id;
        public String code;
        public String params;

        public SimpleAuth(ai.metaheuristic.mhbp.beans.Auth auth) {
            this.id = auth.id;
            this.code = auth.code;
            this.params = auth.getParams();
        }
    }

    @RequiredArgsConstructor
    public static class Auths extends BaseDataClass {
        public final Slice<SimpleAuth> auths;
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    @NoArgsConstructor
    public static class Auth extends BaseDataClass {
        public SimpleAuth auth;

        public Auth(String errorMessage) {
            this.errorMessages = Collections.singletonList(errorMessage);
        }

        public Auth(SimpleAuth auth, String errorMessage) {
            this.auth = auth;
            this.errorMessages = Collections.singletonList(errorMessage);
        }

        public Auth(SimpleAuth auth) {
            this.auth = auth;
        }
    }

}
