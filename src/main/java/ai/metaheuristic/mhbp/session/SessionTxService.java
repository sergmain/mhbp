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
import ai.metaheuristic.mhbp.beans.*;
import ai.metaheuristic.mhbp.data.*;
import ai.metaheuristic.mhbp.repositories.AnswerRepository;
import ai.metaheuristic.mhbp.repositories.SessionRepository;
import ai.metaheuristic.mhbp.yaml.answer.AnswerParams;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Sergio Lissner
 * Date: 3/22/2023
 * Time: 2:51 AM
 */
@Service
@RequiredArgsConstructor
public class SessionTxService {

    public final SessionRepository sessionRepository;
    public final AnswerRepository answerRepository;

    @Transactional
    public Session create(Evaluation evaluation, Api api, Long accountId) {
        Session s = new Session();
        s.evaluationId = evaluation.id;
        s.companyId = evaluation.companyId;
        s.accountId = accountId;
        s.startedOn = System.currentTimeMillis();
        s.providerCode = api.id+":"+api.code;
        s.status = Enums.SessionStatus.created.code;

        sessionRepository.save(s);
        return s;
    }

    @Transactional
    public void finish(Session s, Enums.SessionStatus status) {
        s.finishedOn = System.currentTimeMillis();
        s.status = status.code;
        sessionRepository.save(s);
    }

    @Transactional
    public OperationStatusRest deleteSessionById(@Nullable Long sessionId, RequestContext context) {
        if (sessionId==null) {
            return OperationStatusRest.OPERATION_STATUS_OK;
        }
        Session sourceCode = sessionRepository.findById(sessionId).orElse(null);
        if (sourceCode == null) {
            return new OperationStatusRest(Enums.OperationStatus.OK,
                    "#565.250 session wasn't found, sessionId: " + sessionId, null);
        }
        sessionRepository.deleteById(sessionId);
        return OperationStatusRest.OPERATION_STATUS_OK;
    }

    @Transactional(readOnly = true)
    public ErrorData.ErrorsResult getErrors(Pageable pageable, Long sessionId)  {
        Page<Answer> answers = answerRepository.findAllBySessionId(pageable, sessionId);
        List<ErrorData.SimpleError> list = answers.stream().flatMap(answer -> {
            AnswerParams ap = answer.getAnswerParams();
            return ap.getResults().stream()
                    .map(o->new ErrorData.SimpleError(answer.id, answer.sessionId, o.p, o.e, o.a, o.r));
        })
        .toList();
        var sorted = list.stream().sorted((o1, o2)->Long.compare(o2.id, o1.id)).collect(Collectors.toList());
        return new ErrorData.ErrorsResult(new PageImpl<>(sorted, pageable, list.size()));
    }

}
