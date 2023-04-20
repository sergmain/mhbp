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

package ai.metaheuristic.mhbp.kb.reader.openai;

import ai.metaheuristic.mhbp.Consts;
import ai.metaheuristic.mhbp.Enums;
import ai.metaheuristic.mhbp.questions.QuestionData;
import ai.metaheuristic.mhbp.utils.NetUtils;
import ai.metaheuristic.mhbp.yaml.kb.KbParams;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static ai.metaheuristic.mhbp.questions.QuestionData.QuestionsWithAnswersAndStatus;

/**
 * @author Sergio Lissner
 * Date: 4/19/2023
 * Time: 11:17 PM
 */
public class OpenaiJsonReader {


    public static final QuestionsWithAnswersAndStatus NOT_YET = new QuestionsWithAnswersAndStatus(List.of(), Enums.KbSourceInitStatus.not_yet);

    public static QuestionsWithAnswersAndStatus read(Path mhbpHome, KbParams.Git git) {
        Path gitPath = mhbpHome.resolve(Consts.GIT_PATH);
        String code = NetUtils.asCode(git.repo);
        Path p = gitPath.resolve(code);
        if (Files.notExists(p)) {
            return NOT_YET;
        }

        List<QuestionData.QuestionWithAnswerToAsk> list = new ArrayList<>(100000);
        for (KbParams.KbPath kbPath : git.kbPaths) {
            Path evals = p.resolve(kbPath.evals);
            Path data = p.resolve(kbPath.data);
        }
        return new QuestionsWithAnswersAndStatus(list, Enums.KbSourceInitStatus.ready);
    }
}
