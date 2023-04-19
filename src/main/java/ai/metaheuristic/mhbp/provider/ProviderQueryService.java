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
import ai.metaheuristic.mhbp.beans.Evaluation;
import ai.metaheuristic.mhbp.beans.Session;
import ai.metaheuristic.mhbp.data.NluData;
import ai.metaheuristic.mhbp.events.EvaluateProviderEvent;
import ai.metaheuristic.mhbp.questions.QuestionAndAnswerService;
import ai.metaheuristic.mhbp.questions.QuestionData;
import ai.metaheuristic.mhbp.repositories.ApiRepository;
import ai.metaheuristic.mhbp.repositories.EvaluationRepository;
import ai.metaheuristic.mhbp.repositories.KbRepository;
import ai.metaheuristic.mhbp.sec.UserContextService;
import ai.metaheuristic.mhbp.session.SessionTxService;
import ai.metaheuristic.mhbp.utils.JsonUtils;
import ai.metaheuristic.mhbp.utils.S;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Stream;

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
    public final EvaluationRepository evaluationRepository;
    public final ApiRepository apiRepository;
    public final KbRepository kbRepository;

    public final QuestionAndAnswerService questionAndAnswerService;
    public final SessionTxService sessionTxService;

    public void evaluateProvider(EvaluateProviderEvent event) {
        final AtomicReference<Session> s = new AtomicReference<>(null);
        try {
            Evaluation evaluation = evaluationRepository.findById(event.evaluationId()).orElse(null);
            if (evaluation==null) {
                return;
            }
            Api api = apiRepository.findById(evaluation.apiId).orElse(null);
            if (api==null) {
                return;
            }
            s.set(sessionTxService.create(evaluation, api, event.accountId()));

            log.debug("call EvaluateProviderService.evaluateProvider({})", event.evaluationId());
            Stream<QuestionData.QuestionWithAnswerToAsk> questions = questionAndAnswerService.getQuestionToAsk(evaluation.kbIds, event.limit());

            questions.forEach(question-> {
                ProviderData.QueriedData queriedData = new ProviderData.QueriedData(question.q(), null);
                ProviderData.QuestionAndAnswer qaa = processQuery(api, queriedData, ProviderQueryService::asQueriedInfoWithError);
                questionAndAnswerService.process(s.get(), question, qaa);
            });
            sessionTxService.finish(s.get(), Enums.SessionStatus.finished);
        } catch (Throwable th) {
            log.error("417.020 Error, need to investigate ", th);
            if (s.get()!=null) {
                sessionTxService.finish(s.get(), Enums.SessionStatus.finished_with_error);
            }
        }
    }

    public static QueriedInfoWithError asQueriedInfoWithError(ProviderData.QueriedData queriedData) {
        final NluData.QueriedPrompt queriedInfo = new NluData.QueriedPrompt(queriedData.queryText());
        return new QueriedInfoWithError(queriedInfo, null);
    }

    public ProviderData.QuestionAndAnswer processQuery(Api api, ProviderData.QueriedData queriedData,
                                               Function<ProviderData.QueriedData, QueriedInfoWithError> getQueriedInfoWithErrorFunc) {
        try {
            if (S.b(queriedData.queryText())) {
                return new ProviderData.QuestionAndAnswer(ERROR, "Required parameter wasn't specified");
            }
            final FullQueryResult result = process(api, queriedData, getQueriedInfoWithErrorFunc);
            if (result.queryResult.error!=null ) {
                return new ProviderData.QuestionAndAnswer(ERROR, result.queryResult.error.error);
            }
            if (S.b(result.queryResult.answer)) {
                return new ProviderData.QuestionAndAnswer(ERROR, "Answer is empty");
            }
            return new ProviderData.QuestionAndAnswer(queriedData.queryText(), result.queryResult.answer, OK, null, result.queryResult.raw);
        }
        catch (Throwable e) {
            log.error("Error", e);
            return new ProviderData.QuestionAndAnswer(ERROR, e.getMessage());
        }
    }

    public FullQueryResult process(Api api, ProviderData.QueriedData queriedData,
                                           Function<ProviderData.QueriedData, QueriedInfoWithError> getQueriedInfoWithErrorFunc) throws JsonProcessingException {
        QueryResult queryResult = processInternal(api, queriedData, getQueriedInfoWithErrorFunc);

        String json = JsonUtils.getMapper().writeValueAsString(queryResult);
        final FullQueryResult fullQueryResult = new FullQueryResult(queryResult, json);
        return fullQueryResult;
    }

    private QueryResult processInternal(Api api, ProviderData.QueriedData queriedData,
                                                Function<ProviderData.QueriedData, QueriedInfoWithError> getQueriedInfoWithErrorFunc) {
        QueryResult queryResult;
        if (queriedData.queryText().length()>255) {
            return QueryResult.asError("Query can't be processed, text of query is too long, max 255 chars. actual length" + queriedData.queryText().length(),
                    Enums.QueryResultErrorType.query_too_long);
        }
        try {
            QueriedInfoWithError queriedInfoWithError = getQueriedInfoWithErrorFunc.apply(queriedData);
            if (queriedInfoWithError.error!=null) {
                queryResult = new QueryResult(null, false, queriedInfoWithError.error, null);
            }
            else if (queriedInfoWithError.queriedInfo!=null) {
                SchemeAndParamResult r = providerService.queryProviders(api, queriedInfoWithError.queriedInfo);

                ApiScheme.Response response = api.getApiScheme().scheme.response;
                if (response==null) {
                    throw new IllegalStateException();
                }
                String processedAnswer = ProviderQueryUtils.processAnswerFromApi(r.result, response);
                queryResult = new QueryResult(processedAnswer, true, r.raw);
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
