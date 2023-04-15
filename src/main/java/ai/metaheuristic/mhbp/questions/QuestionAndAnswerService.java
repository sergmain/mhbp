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

import ai.metaheuristic.mhbp.beans.Session;
import ai.metaheuristic.mhbp.provider.ProviderData;
import ai.metaheuristic.mhbp.repositories.AnswerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Sergio Lissner
 * Date: 3/19/2023
 * Time: 11:01 PM
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class QuestionAndAnswerService {

    public final QuestionAndAnswerTxService questionAndAnswerTxService;
    public final AnswerRepository answerRepository;

    public static final List<QuestionData.QuestionWithAnswerToAsk> qqs = List.of(
            new QuestionData.QuestionWithAnswerToAsk("1","answer 2+2 with digits only", "4"),
            new QuestionData.QuestionWithAnswerToAsk("2","q2", "Good"),
            new QuestionData.QuestionWithAnswerToAsk("3","q3", "Good"),
            new QuestionData.QuestionWithAnswerToAsk("4","q4", "Bad"),
            new QuestionData.QuestionWithAnswerToAsk("5","q5", "Good")
    );

    public List<QuestionData.QuestionWithAnswerToAsk> getQuestionToAsk(String providerCode, int limit) {
        if (limit!=0) {
            return getFirstQuestionWithAnswerToAsks();
        }
        return qqs;
    }

    public static List<QuestionData.QuestionWithAnswerToAsk> getFirstQuestionWithAnswerToAsks() {
        return qqs.subList(0, 1);
    }

    public void process(Session session, QuestionData.QuestionWithAnswerToAsk question, ProviderData.QuestionAndAnswer qaa) {
        questionAndAnswerTxService.process(session, question, qaa);
    }
}
