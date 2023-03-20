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

package ai.metaheuristic.mhbp.provider;

import ai.metaheuristic.mhbp.Enums;
import ai.metaheuristic.mhbp.api.model.ApiModel;
import ai.metaheuristic.mhbp.data.ApiData;
import ai.metaheuristic.mhbp.sec.UserContextService;
import ai.metaheuristic.mhbp.utils.JsonUtils;
import ai.metaheuristic.mhbp.utils.RestUtils;
import ai.metaheuristic.mhbp.utils.S;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * @author Sergio Lissner
 * Date: 3/19/2023
 * Time: 9:31 PM
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ProviderQueryService {

    public final ProviderApiModelService providerService;
    public final UserContextService userContextService;

    public ResponseEntity<String> processQuery(ProviderData.QueriedData queriedData,
                                               Function<ProviderData.QueriedData, ApiData.QueriedInfoWithError> getQueriedInfoWithErrorFunc) {
        try {
            if (S.b(queriedData.queryText())) {
                return RestUtils.returnError(HttpStatus.BAD_REQUEST, "Required parameter wasn't specified");
            }
            final ApiData.FullQueryResult result = process(queriedData, getQueriedInfoWithErrorFunc);
            if (result.queryResult.error!=null ) {
                HttpStatus status = HttpStatus.BAD_REQUEST;
                if (result.queryResult.error.errorType == Enums.QueryResultErrorType.cant_understand) {
                    status = HttpStatus.NOT_IMPLEMENTED;
                }
                if (result.queryResult.error.error != null) {
                    return RestUtils.returnError(status, result.queryResult.error.error);
                }
                else {
                    return new ResponseEntity<>(status);
                }
            }
            return new ResponseEntity<>(result.json, HttpStatus.OK);
        }
        catch (Throwable e) {
            log.error("Error", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ApiData.FullQueryResult process(ProviderData.QueriedData queriedData,
                                           Function<ProviderData.QueriedData, ApiData.QueriedInfoWithError> getQueriedInfoWithErrorFunc) throws JsonProcessingException {
        ApiData.QueryResult queryResult = processInternal(queriedData, getQueriedInfoWithErrorFunc);

        String json = JsonUtils.getMapper().writeValueAsString(queryResult);
        final ApiData.FullQueryResult fullQueryResult = new ApiData.FullQueryResult(queryResult, json);
        return fullQueryResult;
    }

    private ApiData.QueryResult processInternal(ProviderData.QueriedData queriedData,
                                                Function<ProviderData.QueriedData, ApiData.QueriedInfoWithError> getQueriedInfoWithErrorFunc) {
        ApiData.QueryResult queryResult;
        if (queriedData.queryText().length()>255) {
            return ApiData.QueryResult.asError("Query can't be processed, text of query is too long, max 255 chars. actual length" + queriedData.queryText().length(),
                    Enums.QueryResultErrorType.query_too_long);
        }
        try {
            ApiData.QueriedInfoWithError queriedInfoWithError = getQueriedInfoWithErrorFunc.apply(queriedData);
            if (queriedInfoWithError.error!=null) {
                queryResult = new ApiData.QueryResult(null, false, queriedInfoWithError.error);
            }
            else {
                List<ApiData.ModelAndParamResult> results = providerService.queryProviders(queriedInfoWithError.queriedInfo);

                List<String> processedAnswers = new ArrayList<>();
                for (ApiData.ModelAndParamResult r : results) {
                    ApiModel.Meta meta = ProviderQueryUtils.getFieldForLookingFor(r.modelAndParams.model, queriedInfoWithError.queriedInfo);
                    if (meta==null) {
                        continue;
                    }
                    String processedAnswer = ProviderQueryUtils.processAnswerFromApi(r.result, meta);
                    processedAnswers.add(processedAnswer);
                }

                queryResult = new ApiData.QueryResult(processedAnswers, true);
            }
        }
        catch (Throwable th) {
            log.error("Error", th);
            queryResult = ApiData.QueryResult.asError("Query can't be processed at this time, server error: " + th.getMessage(), Enums.QueryResultErrorType.server_error);
        }
        return queryResult;
    }

}
