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
import ai.metaheuristic.mhbp.api.params.ApiParams;
import ai.metaheuristic.mhbp.api.scheme.ApiScheme;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * @author Sergio Lissner
 * Date: 3/19/2023
 * Time: 9:12 PM
 */
public class ApiData {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SimpleError {
        public String error;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NewApi {
        public String name;
        public String code;
        public String description;
        public Enums.AuthType authType;
        public String username;
        public String password;
        public String token;
        public String url;
        public String text;

        public Enums.AuthType[] authTypes = Enums.AuthType.values();
    }

/*
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class ApisResult extends BaseDataClass {
        public Page<SimpleApi> apis;
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    @NoArgsConstructor
    public static class ApiResult extends BaseDataClass {
        public SimpleApi api;

        public ApiResult(String errorMessage) {
            this.errorMessages = Collections.singletonList(errorMessage);
        }

        public ApiResult(SimpleApi api, String errorMessage) {
            this.api = api;
            this.errorMessages = Collections.singletonList(errorMessage);
        }

        public ApiResult(SimpleApi api) {
            this.api = api;
        }
    }

*/
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Error {
        public String error;
        public Enums.QueryResultErrorType errorType;
    }

    // this is a result of querying of info provider
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class QueryResult {
        public List<String> answers;
        public boolean success;

        @Nullable
        @JsonInclude(value= JsonInclude.Include.NON_NULL)
        public Error error;

        public QueryResult(List<String> answers, boolean success) {
            this.answers = answers;
            this.success = success;
        }

        public static QueryResult asError(String error, Enums.QueryResultErrorType errorType) {
            return new QueryResult(null, false, new Error(error, errorType));
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FullQueryResult {
        public QueryResult queryResult;
        public String json;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class QueriedInfoWithError {
        public @Nullable NluData.QueriedInfo queriedInfo;
        public @Nullable ApiData.Error error;

        public static QueriedInfoWithError asError(String error, Enums.QueryResultErrorType errorType) {
            return new QueriedInfoWithError(null, new Error(error, errorType));
        }
    }


    @Data
    @AllArgsConstructor
    public static class SchemeAndParams {
        public ApiScheme scheme;
        public ApiParams params;
    }


    @Data
    @AllArgsConstructor
    public static class SchemeAndParamResult {
        public SchemeAndParams schemeAndParams;
        public String result;
    }

    public static class Api {
        public long id;
        public String name;
        public String code;
        public String params;
        public String scheme;

        public Api(ai.metaheuristic.mhbp.beans.Api api) {
            super();
            this.id = api.id;
            this.name = api.name;
            this.code = api.code;
            this.params = api.getParams();
            this.scheme = api.getScheme();
        }
    }

    @RequiredArgsConstructor
    public static class Apis extends BaseDataClass {
        public final Slice<Api> apis;
    }
}
