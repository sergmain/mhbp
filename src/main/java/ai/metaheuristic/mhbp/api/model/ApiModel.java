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
public class ApiModel implements BaseParams {

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
    public static class BasicAuth {
        public String usernameParam;
        public String passwordParam;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TokenAuth {
        // this field contains the name of the field which will be inited with actual token
        // actual value of token isn't part of model
        // see ai.metaheuristic.info.yaml.api.params.ApiParamsYaml.TokenAuth
        public String tokenParam;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Meta {
        public String object;
        @Nullable
        public String desc;
        @Nullable
        public String uri;
        @Nullable
        public String param;
        @Nullable
        public List<Meta> attrs;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        public boolean asText;
        public List<Meta> attrs;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MetaWithResponse {
        public Meta meta;
        public Response response;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Model {
        public String apiDocuUrl;
        @Nullable
        public BasicAuth basicAuth;

        @Nullable
        public TokenAuth tokenAuth;

        public Meta baseMeta;

        public final List<MetaWithResponse> metas = new ArrayList<>();
    }

    public String code;
    public Enums.AuthType authType;
    public Model model;
}
