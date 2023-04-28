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

import ai.metaheuristic.mhbp.beans.Answer;
import ai.metaheuristic.mhbp.beans.Chapter;
import ai.metaheuristic.mhbp.beans.Session;
import ai.metaheuristic.mhbp.repositories.AnswerRepository;
import ai.metaheuristic.mhbp.yaml.answer.AnswerParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static ai.metaheuristic.mhbp.Enums.AnswerStatus;

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
    public void process(Session session, Chapter chapter, AnswerParams ap) {
        Answer a = new Answer();
        // not user which values to use for questionCode
        a.questionCode = "q code";

        a.sessionId = session.id;
        a.chapterId = chapter.id;
        a.answeredOn = System.currentTimeMillis();
        a.total = ap.total;
        a.failed = (int)ap.results.stream().filter(o-> o.s==AnswerStatus.fail).count();
        a.systemError = (int)ap.results.stream().filter(o-> o.s==AnswerStatus.error).count();

        a.updateParams(ap);

        answerRepository.save(a);
    }

}
