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
import ai.metaheuristic.mhbp.beans.Session;
import ai.metaheuristic.mhbp.data.ApiData;
import ai.metaheuristic.mhbp.data.NluData;
import ai.metaheuristic.mhbp.events.EvaluateProviderEvent;
import ai.metaheuristic.mhbp.questions.QuestionData;
import ai.metaheuristic.mhbp.questions.QuestionAndAnswerService;
import ai.metaheuristic.mhbp.sec.UserContextService;
import ai.metaheuristic.mhbp.session.SessionTxService;
import ai.metaheuristic.mhbp.utils.JsonUtils;
import ai.metaheuristic.mhbp.utils.S;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static ai.metaheuristic.mhbp.Enums.OperationStatus.ERROR;
import static ai.metaheuristic.mhbp.Enums.OperationStatus.OK;

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

    public final QuestionAndAnswerService questionAndAnswerService;
    public final ProviderQueryService providerQueryService;
    public final SessionTxService sessionTxService;

    public void evaluateProvider(EvaluateProviderEvent event) {
        try {
            Session s = sessionTxService.create();

            log.debug("call EvaluateProviderService.evaluateProvider({})", event.providerCode());
            List<QuestionData.QuestionToAsk> questions = questionAndAnswerService.getQuestionToAsk();

            for (QuestionData.QuestionToAsk question : questions) {
                ProviderData.QueriedData queriedData = new ProviderData.QueriedData(question.q(), null, null);
                ProviderData.QuestionAndAnswer qaa = processQuery(queriedData, ProviderQueryService::asQueriedInfoWithError);
                questionAndAnswerService.process(s, qaa);
            }
        } catch (Throwable th) {
            log.error("417.020 Error, need to investigate ", th);
        }
    }

    public static ApiData.QueriedInfoWithError asQueriedInfoWithError(ProviderData.QueriedData queriedData) {
        final NluData.QueriedInfo queriedInfo = new NluData.QueriedInfo("question", List.of(new NluData.Property("question", queriedData.queryText())));
        return new ApiData.QueriedInfoWithError(queriedInfo, null);
    }

    public ProviderData.QuestionAndAnswer processQuery(ProviderData.QueriedData queriedData,
                                               Function<ProviderData.QueriedData, ApiData.QueriedInfoWithError> getQueriedInfoWithErrorFunc) {
        try {
            if (S.b(queriedData.queryText())) {
                return new ProviderData.QuestionAndAnswer(ERROR, "Required parameter wasn't specified");
            }
            final ApiData.FullQueryResult result = process(queriedData, getQueriedInfoWithErrorFunc);
            if (result.queryResult.error!=null ) {
                return new ProviderData.QuestionAndAnswer(ERROR, result.queryResult.error.error);
            }
            return new ProviderData.QuestionAndAnswer(queriedData.queryText(), result.json, OK, null);
        }
        catch (Throwable e) {
            log.error("Error", e);
            return new ProviderData.QuestionAndAnswer(ERROR, e.getMessage());
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
