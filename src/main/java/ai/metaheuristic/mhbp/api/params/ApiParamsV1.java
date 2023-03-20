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

package ai.metaheuristic.mhbp.api.params;

import ai.metaheuristic.mhbp.Enums;
import ai.metaheuristic.mhbp.data.BaseParams;
import ai.metaheuristic.mhbp.exceptions.CheckIntegrityFailedException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

@Data
public class ApiParamsV1 implements BaseParams  {

    public final int version=1;

    @Override
    public boolean checkIntegrity() {
        if (api.basicAuth==null && api.tokenAuth==null) {
            throw new CheckIntegrityFailedException("(api.basicAuth==null && api.tokenAuth==null)");
        }
        return true;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BasicAuthV1 {
        public String username;
        public String password;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TokenAuthV1 {
        public String token;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApiV1 {
        public String code;
        public Enums.AuthType authType;

        @Nullable
        public BasicAuthV1 basicAuth;

        @Nullable
        public TokenAuthV1 tokenAuth;
    }

    public final ApiV1 api = new ApiV1();
}
