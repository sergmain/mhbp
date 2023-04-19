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

package ai.metaheuristic.mhbp.questions;

import ai.metaheuristic.mhbp.Enums;
import ai.metaheuristic.mhbp.beans.Answer;
import ai.metaheuristic.mhbp.beans.Session;
import ai.metaheuristic.mhbp.provider.ProviderData;
import ai.metaheuristic.mhbp.repositories.AnswerRepository;
import ai.metaheuristic.mhbp.yaml.answer.AnswerParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static ai.metaheuristic.mhbp.Enums.OperationStatus.OK;

/**
 * @author Sergio Lissner
 * Date: 3/22/2023
 * Time: 2:39 AM
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class QuestionAndAnswerTxService {

    public final AnswerRepository answerRepository;

    @Transactional
    public void process(Session session, QuestionData.QuestionWithAnswerToAsk question, ProviderData.QuestionAndAnswer qaa) {
        Answer a = new Answer();
        a.questionCode = question.qCode();
        a.sessionId = session.id;
        a.kbId = question.kbId();
        a.apiInfo = "n/a";
        a.answeredOn = System.currentTimeMillis();
        if (qaa.status()==OK) {
            a.status = qaa.a()!=null && question.a().equals(qaa.a().strip()) ? Enums.AnswerStatus.normal.code : Enums.AnswerStatus.fail.code;
        }
        else {
            a.status = Enums.AnswerStatus.error.code;
        }
        AnswerParams ap = new AnswerParams();
        ap.raw = qaa.raw();
        a.updateParams(ap);

        answerRepository.save(a);
    }

}
