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
import ai.metaheuristic.mhbp.utils.JsonUtils;
import ai.metaheuristic.mhbp.utils.NetUtils;
import ai.metaheuristic.mhbp.yaml.kb.KbParams;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.SneakyThrows;
import org.springframework.lang.Nullable;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ai.metaheuristic.mhbp.Enums.PromptResponseType.json;
import static ai.metaheuristic.mhbp.questions.QuestionData.QuestionsWithAnswersAndStatus;

/**
 * @author Sergio Lissner
 * Date: 4/19/2023
 * Time: 11:17 PM
 */
public class OpenaiJsonReader {


    public static final QuestionsWithAnswersAndStatus NOT_YET = new QuestionsWithAnswersAndStatus(List.of(), Enums.KbSourceInitStatus.not_yet);
    public static final String SAMPLES_JSONL = "samples_jsonl";
    public static final String MATCH = "evals.elsuite.basic.match:Match";

    @SneakyThrows
    public static QuestionsWithAnswersAndStatus read(long kbId, String qCode, Path mhbpHome, @Nullable KbParams.Git git) {
        if (git==null) {
            return NOT_YET;
        }
        Path gitPath = mhbpHome.resolve(Consts.GIT_PATH);
        String code = NetUtils.asCode(git.repo);
        Path p = gitPath.resolve(code);
        if (Files.notExists(p)) {
            return NOT_YET;
        }
        Path repo = p.resolve(Consts.REPO);
        if (Files.notExists(repo)) {
            return NOT_YET;
        }

        List<QuestionData.QuestionWithAnswerToAsk> list = new ArrayList<>(100000);
        for (KbParams.KbPath kbPath : git.kbPaths) {
            Path evals = repo.resolve(kbPath.evals);

            final List<String> paths = new ArrayList<>();
            Files.walkFileTree(evals, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    String yaml = Files.readString(file);
                    String jsonlPath = parseAndGetJsonlPath(yaml);
                    if (jsonlPath!=null) {
                        paths.add(jsonlPath);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });

            Path data = repo.resolve(kbPath.data);
            for (String path : paths) {
                Path jsonlPath = data.resolve(path);
                if (Files.notExists(jsonlPath)) {
                    System.out.println("\tNot exists: " + jsonlPath);
                    continue;
                }
                System.out.println("Exists: " + jsonlPath);
                List<String> jsonls = Files.readAllLines(jsonlPath);
                for (String jsonl : jsonls) {
                    OpenaiInput input = toOpenaiInput(jsonl);
                    StringBuilder sb = new StringBuilder();
                    for (OpenaiInput.Input in : input.input) {
                        sb.append(in.content).append('\n');
                    }
                    list.add(new QuestionData.QuestionWithAnswerToAsk(kbId, qCode, sb.toString(), input.getIdeal()));
                }
            }

        }
        return new QuestionsWithAnswersAndStatus(list, Enums.KbSourceInitStatus.ready);
    }

    @Nullable
    static String parseAndGetJsonlPath(String s) {
        Constructor c = new Constructor(Map.class);
        Yaml yaml = new Yaml(c);

        Map map = (Map) yaml.load(s);

        final Map.Entry match = isMatch(map);
//        System.out.println(match);
        final String jsonlPath = getJsonlPath(match);
        return jsonlPath;
    }

    @Nullable
    public static Map.Entry<Object, Object> isMatch(Map<Object, Object> map) {
        for (Map.Entry<Object, Object> entry : map.entrySet()) {
            if (entry.getValue() instanceof Map mmap) {
                if (MATCH.equals(mmap.get("class"))) {
                    return entry;
                }
            }
        }
        return null;
    }

    @Nullable
    public static String getJsonlPath(@Nullable Map.Entry match) {
        if (match==null) {
            return null;
        }
        if (!(match.getValue() instanceof Map mmap)) {
            return null;
        }

        Object o = mmap.get("args");

        if (!(o instanceof Map argsMap)) {
            return null;
        }

        final Object o1 = argsMap.get(SAMPLES_JSONL);
        return o1 instanceof String ? (String)o1 : null;
    }

    static OpenaiInput toOpenaiInput(String json) throws JsonProcessingException {
        try {
            return JsonUtils.getMapper().readValue(json, OpenaiInput.class);
        }
        catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
