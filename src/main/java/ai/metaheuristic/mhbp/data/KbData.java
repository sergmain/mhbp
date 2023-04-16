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

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;

import java.util.Collections;

/**
 * @author Sergio Lissner
 * Date: 4/16/2023
 * Time: 12:29 AM
 */
public class KbData {

    public static class SimpleKb {
        public long id;
        public String code;
        public String params;

        public SimpleKb(ai.metaheuristic.mhbp.beans.Kb auth) {
            this.id = auth.id;
            this.code = auth.code;
            this.params = auth.getParams();
        }
    }

    @RequiredArgsConstructor
    public static class Kbs extends BaseDataClass {
        public final Slice<SimpleKb> auths;
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    @NoArgsConstructor
    public static class Kb extends BaseDataClass {
        public SimpleKb auth;

        public Kb(String errorMessage) {
            this.errorMessages = Collections.singletonList(errorMessage);
        }

        public Kb(SimpleKb auth, String errorMessage) {
            this.auth = auth;
            this.errorMessages = Collections.singletonList(errorMessage);
        }

        public Kb(SimpleKb auth) {
            this.auth = auth;
        }
    }

}
