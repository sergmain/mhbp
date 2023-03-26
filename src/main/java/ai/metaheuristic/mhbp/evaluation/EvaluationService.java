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

package ai.metaheuristic.mhbp.evaluation;

import ai.metaheuristic.mhbp.Enums;
import ai.metaheuristic.mhbp.beans.Session;
import ai.metaheuristic.mhbp.data.EvalStatusGrouped;
import ai.metaheuristic.mhbp.data.EvaluationData;
import ai.metaheuristic.mhbp.repositories.AnswerRepository;
import ai.metaheuristic.mhbp.repositories.SessionRepository;
import ai.metaheuristic.mhbp.utils.ControllerUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ai.metaheuristic.mhbp.data.EvaluationData.*;
import static ai.metaheuristic.mhbp.data.EvaluationData.EvalStatuses;

/**
 * @author Sergio Lissner
 * Date: 3/26/2023
 * Time: 3:36 AM
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class EvaluationService {

    public final SessionRepository sessionRepository;
    public final AnswerRepository answerRepository;

    public EvalStatuses getStatuses(Pageable pageable) {
        pageable = ControllerUtils.fixPageSize(10, pageable);

        List<Session> sessions = sessionRepository.getSessions(pageable);
        if (sessions.isEmpty()) {
            return new EvalStatuses(List.of());
        }

        final List<Long> sessionIds = sessions.stream().map(o -> o.id).collect(Collectors.toList());
        final List<Object[]> statuses = answerRepository.getStatusesJpql(sessionIds);
        List<EvalStatus> list = new ArrayList<>();
        for (Object[] status : statuses) {
//String sessionId, long startedOn, @Nullable Long finishedOn, String sessionStatus,
//String safe,
//float normal, float fail, float error,
//String providerCode, String modelInfo) {
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

            EvalStatus es = new EvalStatus(
                    s.id, s.startedOn, s.finishedOn, Enums.SessionStatus.to(s.status).toString(),
                    null,
                    normalPercent, failPercent, errorPercent, s.providerCode, "n/a"
            );
            list.add(es);
        }
        return new EvalStatuses(list);
    }
}
