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

package ai.metaheuristic.mhbp.session;

import ai.metaheuristic.mhbp.Enums;
import ai.metaheuristic.mhbp.beans.Api;
import ai.metaheuristic.mhbp.beans.Evaluation;
import ai.metaheuristic.mhbp.beans.Kb;
import ai.metaheuristic.mhbp.beans.Session;
import ai.metaheuristic.mhbp.data.ErrorData;
import ai.metaheuristic.mhbp.data.RequestContext;
import ai.metaheuristic.mhbp.repositories.*;
import ai.metaheuristic.mhbp.utils.ControllerUtils;
import ai.metaheuristic.mhbp.utils.S;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ai.metaheuristic.mhbp.data.SessionData.SessionStatus;
import static ai.metaheuristic.mhbp.data.SessionData.SessionStatuses;

/**
 * @author Sergio Lissner
 * Date: 3/26/2023
 * Time: 3:36 AM
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SessionService {

    public final SessionTxService sessionTxService;
    public final SessionRepository sessionRepository;
    public final AnswerRepository answerRepository;
    public final EvaluationRepository evaluationRepository;
    public final ApiRepository apiRepository;

    public SessionStatuses getStatuses(Pageable pageable) {
        pageable = ControllerUtils.fixPageSize(10, pageable);

        List<Session> sessions = sessionRepository.getSessions(pageable);
        if (sessions.isEmpty()) {
            return new SessionStatuses(new SliceImpl<>(List.of(), pageable, false));
        }

        final List<Long> sessionIds = sessions.stream().map(o -> o.id).collect(Collectors.toList());
        final List<Object[]> statuses = answerRepository.getStatusesJpql(sessionIds);
        List<SessionStatus> list = new ArrayList<>();
        Map<Long, String> localCache = new HashMap<>();
        for (Object[] status : statuses) {
            long sessionId = (long)status[0];
            Session s = sessions.stream().filter(o->o.id==sessionId).findFirst().orElseThrow();
            float normalPercent = 0;
            float failPercent = 0;
            float errorPercent = 0;
            float total = (long) status[1];
            if (total!=0) {
                normalPercent = (long) status[2] / total;
                failPercent = (long) status[3] / total;
                errorPercent = (long) status[4] / total;
            }
            long evaluationId = (long)status[5];

            String apiInfo = localCache.computeIfAbsent(evaluationId, this::getApiInfo);

            SessionStatus es = new SessionStatus(
                    s.id, s.startedOn, s.finishedOn, Enums.SessionStatus.to(s.status).toString(),
                    null,
                    normalPercent, failPercent, errorPercent, s.providerCode, apiInfo
            );
            list.add(es);
        }
        var sorted = list.stream().sorted((o1, o2)->Long.compare(o2.sessionId(), o1.sessionId())).collect(Collectors.toList());
        return new SessionStatuses(new PageImpl<>(sorted, pageable, list.size()));
    }

    public static final String UNKNOWN = "<unknown>";

    private String getApiInfo(Long evaluationId) {
        Evaluation e = evaluationRepository.findById(evaluationId).orElse(null);
        if (e==null) {
            return UNKNOWN;
        }
        Api api = apiRepository.findById(e.apiId).orElse(null);
        if (api==null) {
            return UNKNOWN;
        }
        return  S.b(api.code) ? UNKNOWN : api.code;
    }

    public ErrorData.ErrorsResult getErrors(Pageable pageable, Long sessionId, RequestContext context) {
        Session s = sessionRepository.findById(sessionId).orElse(null);
        if (s==null) {
            return new ErrorData.ErrorsResult("Session wasn't found");
        }
        if (s.companyId!=context.getCompanyId()) {
            return new ErrorData.ErrorsResult("Wrong sessionId");
        }
        ErrorData.ErrorsResult result = sessionTxService.getErrors(pageable, sessionId);
        return result;
    }
}
