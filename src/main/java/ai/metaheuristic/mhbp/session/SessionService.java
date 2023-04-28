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
import ai.metaheuristic.mhbp.beans.Session;
import ai.metaheuristic.mhbp.data.ErrorData;
import ai.metaheuristic.mhbp.data.RequestContext;
import ai.metaheuristic.mhbp.data.SimpleAnswerStats;
import ai.metaheuristic.mhbp.repositories.*;
import ai.metaheuristic.mhbp.utils.ControllerUtils;
import ai.metaheuristic.mhbp.utils.S;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
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
    public final KbRepository kbRepository;
    public final ChapterRepository chapterRepository;

    @AllArgsConstructor
    @NoArgsConstructor
    public static class EvalsDesc {
        public long evaluationId;
        public String apiInfo;
        public List<String> chapters;
    }

    public SessionStatuses getStatuses(Pageable pageable) {
        pageable = ControllerUtils.fixPageSize(10, pageable);

        Map<Long, Session> sessions = sessionRepository.getSessions(pageable).stream()
                .collect(Collectors.toMap(Session::getId, Function.identity()));

        if (sessions.isEmpty()) {
            return new SessionStatuses(Page.empty(pageable));
        }

        final List<Long> sessionIds = sessions.keySet().stream().sorted(Comparator.reverseOrder()).toList();
        final List<SimpleAnswerStats> statuses = answerRepository.getStatusesJpql(sessionIds);
        List<SessionStatus> list = new ArrayList<>();
        Map<Long, EvalsDesc> localCache = new HashMap<>();
        for (Long sessionId : sessionIds) {
            Session s = sessions.get(sessionId);
            List<SimpleAnswerStats> sessionStats = statuses.stream().filter(o->o.sessionId==sessionId).toList();
            long total = sessionStats.stream().mapToLong(o->o.total).sum();
            long failed = sessionStats.stream().mapToLong(o->o.failed).sum();
            long error = sessionStats.stream().mapToLong(o->o.systemError).sum();

            float normalPercent = 0;
            float failPercent = 0;
            float errorPercent = 0;
            if (total!=0) {
                normalPercent = ((float)(total-failed - error)) / total;
                failPercent = (float)failed / total;
                errorPercent = (float)error / total;
            }
            long evaluationId = s.evaluationId;

            Evaluation e = evaluationRepository.findById(evaluationId).orElse(null);
            EvalsDesc evalsDesc = localCache.computeIfAbsent(evaluationId, evaluationId1 -> getEvalsDesc(e));
            SessionStatus es = new SessionStatus(
                    s.id, s.startedOn, s.finishedOn, Enums.SessionStatus.to(s.status).toString(), null,
                    normalPercent, failPercent, errorPercent, s.providerCode, evalsDesc.apiInfo,  evalsDesc.evaluationId, String.join(", ", evalsDesc.chapters)
            );
            list.add(es);
        }
//        var sorted = list.stream().sorted((o1, o2)->Long.compare(o2.sessionId(), o1.sessionId())).collect(Collectors.toList());
        return new SessionStatuses(new PageImpl<>(list, pageable, list.size()));
    }

    public static final String UNKNOWN = "<unknown>";

    private EvalsDesc getEvalsDesc(@Nullable Evaluation e) {
        if (e==null) {
            return new EvalsDesc(-1, UNKNOWN, List.of("<unknown>"));
        }
        Api api = apiRepository.findById(e.apiId).orElse(null);
        if (api==null) {
            return new EvalsDesc(-1, UNKNOWN, List.of("<unknown>"));
        }
        if (S.b(api.code)) {
            return new EvalsDesc(-1, UNKNOWN, List.of("<unknown>"));
        }
        EvalsDesc evalsDesc = new EvalsDesc(e.id, api.code, new ArrayList<>());

        for (String chapterId : e.chapterIds) {
            String chapterCode = chapterRepository.findById(Long.parseLong(chapterId)).map(o->o.code).orElse(null);
            if (!S.b(chapterCode)) {
                evalsDesc.chapters.add(chapterCode);
            }
        }
        if (evalsDesc.chapters.isEmpty()) {
            evalsDesc.chapters.add("<Error retrieving KBs' chapters >");
        }
        return evalsDesc;
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
