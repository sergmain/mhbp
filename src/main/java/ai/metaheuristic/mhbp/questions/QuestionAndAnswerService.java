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

import ai.metaheuristic.mhbp.beans.Kb;
import ai.metaheuristic.mhbp.beans.Session;
import ai.metaheuristic.mhbp.kb.reader.openai.OpenaiJsonReader;
import ai.metaheuristic.mhbp.provider.ProviderData;
import ai.metaheuristic.mhbp.repositories.AnswerRepository;
import ai.metaheuristic.mhbp.repositories.KbRepository;
import ai.metaheuristic.mhbp.yaml.kb.KbParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static ai.metaheuristic.mhbp.questions.QuestionData.QuestionWithAnswerToAsk;

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
    public final KbRepository kbRepository;

    public Stream<QuestionWithAnswerToAsk> getQuestionToAsk(List<String> kbIds, int limit) {

        Stream<QuestionWithAnswerToAsk> stream = kbIds.stream().map(Long::valueOf)
                .map(id->kbRepository.findById(id).orElse(null))
                .filter(Objects::nonNull)
                .flatMap(QuestionAndAnswerService::getStreamOfPrompt);

        if (limit!=0) {
            stream = stream.limit(limit);
        }
        return stream;
    }

    private static Stream<QuestionWithAnswerToAsk> getStreamOfPrompt(Kb kb) {
        KbParams kbParams = kb.getKbParams();

        if (kbParams.kb.file!=null) {
            return Stream.empty();
        }
        else if (kbParams.kb.git!=null) {
            Path mhbpHome = Path.of(System.getenv("MHBP_HOME"));
            QuestionData.QuestionsWithAnswersAndStatus qas = OpenaiJsonReader.read(kb.id, kbParams.kb.type, mhbpHome, kbParams.kb.git);
            return qas.list.stream();
        }
        else if (kbParams.getKb().inline!=null) {
            return kbParams.getKb().inline.stream()
                    .map(o->new QuestionWithAnswerToAsk(kb.id, kbParams.getKb().type, o.p, o.a));
        }
        throw new IllegalStateException();
    }

    public void process(Session session, QuestionWithAnswerToAsk question, ProviderData.QuestionAndAnswer qaa) {
        questionAndAnswerTxService.process(session, question, qaa);
    }
}
