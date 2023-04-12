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
import ai.metaheuristic.mhbp.api.scheme.ApiScheme;
import ai.metaheuristic.mhbp.beans.Api;
import ai.metaheuristic.mhbp.beans.Session;
import ai.metaheuristic.mhbp.data.NluData;
import ai.metaheuristic.mhbp.events.EvaluateProviderEvent;
import ai.metaheuristic.mhbp.questions.QuestionData;
import ai.metaheuristic.mhbp.questions.QuestionAndAnswerService;
import ai.metaheuristic.mhbp.repositories.ApiRepository;
import ai.metaheuristic.mhbp.sec.UserContextService;
import ai.metaheuristic.mhbp.session.SessionTxService;
import ai.metaheuristic.mhbp.utils.JsonUtils;
import ai.metaheuristic.mhbp.utils.S;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static ai.metaheuristic.mhbp.Enums.OperationStatus.ERROR;
import static ai.metaheuristic.mhbp.Enums.OperationStatus.OK;
import static ai.metaheuristic.mhbp.data.ApiData.*;

/**
 * @author Sergio Lissner
 * Date: 3/19/2023
 * Time: 9:31 PM
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ProviderQueryService {

    public final ProviderApiSchemeService providerService;
    public final UserContextService userContextService;
    private final ApiRepository apiRepository;

    public final QuestionAndAnswerService questionAndAnswerService;
    public final SessionTxService sessionTxService;

    public void evaluateProvider(EvaluateProviderEvent event) {
        Session s = null;
        try {
            Api api = apiRepository.findById(event.apiId()).orElse(null);
            if (api==null) {
                return;
            }
            s = sessionTxService.create(api.code);

            log.debug("call EvaluateProviderService.evaluateProvider({})", event.apiId());
            List<QuestionData.QuestionWithAnswerToAsk> questions = questionAndAnswerService.getQuestionToAsk(api.code);

            for (QuestionData.QuestionWithAnswerToAsk question : questions) {
                ProviderData.QueriedData queriedData = new ProviderData.QueriedData(question.q(), null);
                ProviderData.QuestionAndAnswer qaa = processQuery(queriedData, ProviderQueryService::asQueriedInfoWithError);
                questionAndAnswerService.process(s, question, qaa);
            }
            sessionTxService.finish(s, Enums.SessionStatus.finished);
        } catch (Throwable th) {
            log.error("417.020 Error, need to investigate ", th);
            if (s!=null) {
                sessionTxService.finish(s, Enums.SessionStatus.finished_with_error);
            }
        }
    }

    public static QueriedInfoWithError asQueriedInfoWithError(ProviderData.QueriedData queriedData) {
        final NluData.QueriedInfo queriedInfo = new NluData.QueriedInfo("question", List.of(new NluData.Property("question", queriedData.queryText())));
        return new QueriedInfoWithError(queriedInfo, null);
    }

    public ProviderData.QuestionAndAnswer processQuery(ProviderData.QueriedData queriedData,
                                               Function<ProviderData.QueriedData, QueriedInfoWithError> getQueriedInfoWithErrorFunc) {
        try {
            if (S.b(queriedData.queryText())) {
                return new ProviderData.QuestionAndAnswer(ERROR, "Required parameter wasn't specified");
            }
            final FullQueryResult result = process(queriedData, getQueriedInfoWithErrorFunc);
            if (result.queryResult.error!=null ) {
                return new ProviderData.QuestionAndAnswer(ERROR, result.queryResult.error.error);
            }
            if (CollectionUtils.isEmpty(result.queryResult.answers) || S.b(result.queryResult.answers.get(0))) {
                return new ProviderData.QuestionAndAnswer(ERROR, "Answer is empty");
            }
            return new ProviderData.QuestionAndAnswer(queriedData.queryText(), result.queryResult.answers.get(0), OK, null);
        }
        catch (Throwable e) {
            log.error("Error", e);
            return new ProviderData.QuestionAndAnswer(ERROR, e.getMessage());
        }
    }

    public FullQueryResult process(ProviderData.QueriedData queriedData,
                                           Function<ProviderData.QueriedData, QueriedInfoWithError> getQueriedInfoWithErrorFunc) throws JsonProcessingException {
        QueryResult queryResult = processInternal(queriedData, getQueriedInfoWithErrorFunc);

        String json = JsonUtils.getMapper().writeValueAsString(queryResult);
        final FullQueryResult fullQueryResult = new FullQueryResult(queryResult, json);
        return fullQueryResult;
    }

    private QueryResult processInternal(ProviderData.QueriedData queriedData,
                                                Function<ProviderData.QueriedData, QueriedInfoWithError> getQueriedInfoWithErrorFunc) {
        QueryResult queryResult;
        if (queriedData.queryText().length()>255) {
            return QueryResult.asError("Query can't be processed, text of query is too long, max 255 chars. actual length" + queriedData.queryText().length(),
                    Enums.QueryResultErrorType.query_too_long);
        }
        try {
            QueriedInfoWithError queriedInfoWithError = getQueriedInfoWithErrorFunc.apply(queriedData);
            if (queriedInfoWithError.error!=null) {
                queryResult = new QueryResult(null, false, queriedInfoWithError.error);
            }
            else if (queriedInfoWithError.queriedInfo!=null) {
                List<SchemeAndParamResult> results = providerService.queryProviders(queriedInfoWithError.queriedInfo);

                List<String> processedAnswers = new ArrayList<>();
                for (SchemeAndParamResult r : results) {
                    ApiScheme.ResponseMeta responseMeta = ProviderQueryUtils.getFieldForLookingFor(r.schemeAndParams.scheme, queriedInfoWithError.queriedInfo);
                    if (responseMeta==null) {
                        continue;
                    }
                    String processedAnswer = ProviderQueryUtils.processAnswerFromApi(r.result, responseMeta);
                    processedAnswers.add(processedAnswer);
                }

                queryResult = new QueryResult(processedAnswers, true);
            }
            else {
                throw new IllegalStateException();
            }
        }
        catch (Throwable th) {
            log.error("Error", th);
            queryResult = QueryResult.asError("Query can't be processed at this time, server error: " + th.getMessage(), Enums.QueryResultErrorType.server_error);
        }
        return queryResult;
    }

}
