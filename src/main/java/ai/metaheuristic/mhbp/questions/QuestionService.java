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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
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
public class QuestionService {

    public static final List<QuestionData.QuestionToAsk> qqs = List.of(
            new QuestionData.QuestionToAsk("q1"),
            new QuestionData.QuestionToAsk("q2"),
            new QuestionData.QuestionToAsk("q3"),
            new QuestionData.QuestionToAsk("q4"),
            new QuestionData.QuestionToAsk("q5")
    );

    public List<QuestionData.QuestionToAsk> getQuestionToAsk() {
        return qqs;
    }

}
