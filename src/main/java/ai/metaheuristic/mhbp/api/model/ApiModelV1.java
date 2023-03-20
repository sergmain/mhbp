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

package ai.metaheuristic.mhbp.api.model;

import ai.metaheuristic.mhbp.Enums;
import ai.metaheuristic.mhbp.data.BaseParams;
import ai.metaheuristic.mhbp.exceptions.CheckIntegrityFailedException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.List;

@Data
public class ApiModelV1 implements BaseParams {

    public final int version=1;

    @Override
    public boolean checkIntegrity() {
        if (model==null) {
            throw new CheckIntegrityFailedException("(model==null)");
        }
        if (model.basicAuth==null && model.tokenAuth==null) {
            throw new CheckIntegrityFailedException("(api.basicAuth==null && api.tokenAuth==null)");
        }
        return true;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BasicAuthV1 {
        public String usernameParam;
        public String passwordParam;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TokenAuthV1 {
        public String tokenParam;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MetaV1 {
        public String object;
        @Nullable
        public String desc;
        @Nullable
        public String uri;
        @Nullable
        public String param;
        @Nullable
        public List<MetaV1> attrs;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResponseV1 {
        public List<MetaV1> attrs = new ArrayList<>();
    }

    @Data
    @NoArgsConstructor
    public static class MetaWithResponseV1 {
        public final MetaV1 meta = new MetaV1();
        public final ResponseV1 response = new ResponseV1();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ModelV1 {
        public String apiDocuUrl;
        @Nullable
        public BasicAuthV1 basicAuth;

        @Nullable
        public TokenAuthV1 tokenAuth;

        public final MetaV1 baseMeta = new MetaV1();

        public final List<MetaWithResponseV1> metas = new ArrayList<>();
    }

    public String code;
    public Enums.AuthType authType;
    public ModelV1 model;
}
