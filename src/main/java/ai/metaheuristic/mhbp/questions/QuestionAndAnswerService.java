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

import ai.metaheuristic.mhbp.beans.Chapter;
import ai.metaheuristic.mhbp.beans.Session;
import ai.metaheuristic.mhbp.yaml.answer.AnswerParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author Sergio Lissner
 * Date: 3/19/2023
 * Time: 11:01 PM
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class QuestionAndAnswerService {

    private final QuestionAndAnswerTxService questionAndAnswerTxService;

    public Stream<QuestionData.PromptWithAnswerWithChapterId> getQuestionToAsk(List<String> chapterIds, int limit) {

        Stream<QuestionData.PromptWithAnswerWithChapterId> stream = chapterIds.stream()
                .map(questionAndAnswerTxService::getQuestionToAsk)
                .flatMap(Collection::stream);
        if (limit!=0) {
            stream = stream.limit(limit);
        }
        return stream;
    }

/*
    private static Stream<QuestionWithAnswerToAsk> getStreamOfPrompt(Chapter chapter) {
        return chapter.getChapterParams().prompts.stream().map(QuestionWithAnswerToAsk::fromPrompt);

        if (chapterParams.kb.file!=null) {
            return Stream.empty();
        }
        else if (chapterParams.kb.git!=null) {
            QuestionData.Chapters qas = OpenaiJsonReader.read(chapter.id, mhbpHome, chapterParams.kb.git);
            return qas.list.stream();
        }
        else if (chapterParams.getKb().inline!=null) {
            return chapterParams.getKb().inline.stream()
                    .map(o->new QuestionWithAnswerToAsk(chapter.id, chapterParams.getKb().type, o.p, o.a));
        }
        throw new IllegalStateException();
    }
*/

    public void process(Session session, Chapter chapter, AnswerParams ap) {
        questionAndAnswerTxService.process(session, chapter, ap);
    }
}
