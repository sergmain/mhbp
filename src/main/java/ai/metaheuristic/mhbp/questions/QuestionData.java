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
import ai.metaheuristic.mhbp.yaml.chapter.ChapterParams;
import ai.metaheuristic.mhbp.yaml.part.PartParams;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sergio Lissner
 * Date: 3/19/2023
 * Time: 11:01 PM
 */
public class QuestionData {

    public record PromptWithAnswerWithChapterId(long chapterId, long partId, QuestionWithAnswerToAsk prompt) {
        public static PromptWithAnswerWithChapterId fromPrompt(long chapterId, long partId, PartParams.Prompt prompt) {
            return new PromptWithAnswerWithChapterId(chapterId, partId, new QuestionWithAnswerToAsk(prompt.p, prompt.a));
        }
    }

    public record QuestionWithAnswerToAsk(String q, String a) {
        public PartParams.Prompt toPrompt() {
            return new PartParams.Prompt(q, a);
        }
    }

    public record ChapterWithPrompts(String chapterCode, List<QuestionWithAnswerToAsk> list) {}

    @NoArgsConstructor
    @AllArgsConstructor
    public static class Chapters {
        public List<ChapterWithPrompts> chapters = new ArrayList<>();
        public long kbId;
        public Enums.KbSourceInitStatus initStatus;

        public Chapters(Enums.KbSourceInitStatus initStatus) {
            this.initStatus = initStatus;
        }

        public Chapters(long kbId) {
            this.kbId = kbId;
        }
    }
}
