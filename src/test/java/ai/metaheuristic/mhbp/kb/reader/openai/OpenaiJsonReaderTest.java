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

import ai.metaheuristic.mhbp.Enums;
import ai.metaheuristic.mhbp.questions.QuestionData;
import ai.metaheuristic.mhbp.utils.JsonUtils;
import ai.metaheuristic.mhbp.utils.S;
import ai.metaheuristic.mhbp.yaml.kb.KbParams;
import ai.metaheuristic.mhbp.yaml.kb.KbParamsUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Sergio Lissner
 * Date: 4/19/2023
 * Time: 11:52 PM
 */
public class OpenaiJsonReaderTest {

    @Test
    public void test_read() {

        String s = """
                TASK: Read the chess position provided in FEN-notation, then identify the best move to the board position below, in the format A. Your answer should only contain the letter of the correct move. Do not provide any further explanation.
                            
                White to move FEN: rn2k2r/pp2ppPp/2p5/6Q1/2q3b1/2N5/PPP2PPP/R1B1K1NR Possible Moves: A: Rg8, B: Qd4, C: Qxc3 Answer only with the letter of the beset move.""";

        String yaml = """
                version: 1
                kb:
                  code: openai
                  type: openai
                  git:
                    repo: https://github.com/openai/evals.git
                    branch: main
                    commit: origin
                    kbPaths:
                      - evals: evals/registry/evals
                        data: evals/registry/data
                """;
        KbParams kbParams = KbParamsUtils.UTILS.to(yaml);

        assertNotNull(kbParams.kb.git);
        assertEquals(1, kbParams.kb.git.kbPaths.size());

        KbParams.KbPath git = kbParams.kb.git.kbPaths.get(0);


        final String mhbpHomeEnv = System.getenv("MHBP_HOME");
        assertFalse(S.b(mhbpHomeEnv));

        Path mhbpHome = Path.of(mhbpHomeEnv);
        QuestionData.QuestionsWithAnswersAndStatus qas = OpenaiJsonReader.read(10L, Enums.KbFileFormat.openai, mhbpHome, kbParams.kb.git);

        String jsonl = qas.list.stream().map(SimpleQA::to).map(SimpleQA::toJson).collect(Collectors.joining("\n"));

        System.out.println("Total inputs: " + qas.list.size());
        System.out.println("Final size: " + jsonl.length());

        int i = 0;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SimpleQA {
        public String q;
        public String a;

        public static SimpleQA to(QuestionData.QuestionWithAnswerToAsk bean) {
            return new SimpleQA(bean.q(), bean.a());
        }

        @SneakyThrows
        public String toJson() {
            return JsonUtils.getMapper().writeValueAsString(this);
        }
    }

    @Test
    public void test_read_yaml() {
        String s = """
                balance-chemical-equation:
                  id: balance-chemical-equation.dev.v0
                  metrics: [accuracy]
                                
                balance-chemical-equation.dev.v0:
                  class: evals.elsuite.basic.match:Match
                  args:
                    samples_jsonl: balance_chemical_equation/samples.jsonl
                """;

        final String jsonlPath = OpenaiJsonReader.parseAndGetJsonlPath(s);
        System.out.println(jsonlPath);
        int i = 0;
    }

    @Test
    public void test_toOpenaiInput_1() throws JsonProcessingException {

        String json = """
                {
                  "input": [
                    {
                      "role": "system",
                      "content": "You are LawStudentGPT. Answer the following True/False question according to the ABA Model Rules of Professional Conduct."
                    },
                    {
                      "role": "user",
                      "content": "A lawyer with general experience not considered competent to handle a case involving a specialized field of law."
                    }
                  ],
                  "ideal": "False"
                }""";

        OpenaiInput openaiInput = OpenaiJsonReader.toOpenaiInput(json);

        assertEquals("system", openaiInput.input.get(0).role);
        assertEquals("You are LawStudentGPT. Answer the following True/False question according to the ABA Model Rules of Professional Conduct.", openaiInput.input.get(0).content);
        assertEquals("user", openaiInput.input.get(1).role);
        assertEquals("A lawyer with general experience not considered competent to handle a case involving a specialized field of law.", openaiInput.input.get(1).content);
        assertEquals("False", openaiInput.getIdeal());

        System.out.println(openaiInput);
    }

    @Test
    public void test_toOpenaiInput_2() throws JsonProcessingException {

        String json = """
                {
                  "input": [
                    {
                      "role": "system",
                      "content": "You are ChemistGPT, can help user balance chemical equation. User for example, if user's input is \\"C6H5COOH + O2 = CO2 + H2O\\", you will reply the balanced chemical equation: \\"2C6H5COOH + 15O2 = 14CO2 + 6H2O\\", without explanation. If you can't balance the equation, just reply \\"Unknown\\""
                    },
                    {
                      "role": "user",
                      "content": "Fe + Cl2 = FeCl3"
                    }
                  ],
                  "ideal": [
                    "2Fe + 3Cl_2 = 2FeCl_3"
                  ]
                }""";

        OpenaiInput openaiInput = OpenaiJsonReader.toOpenaiInput(json);

        assertEquals(2, openaiInput.input.size());
        assertEquals("system", openaiInput.input.get(0).role);
        assertEquals("You are ChemistGPT, can help user balance chemical equation. User for example, if user's input is \"C6H5COOH + O2 = CO2 + H2O\", you will reply the balanced chemical equation: \"2C6H5COOH + 15O2 = 14CO2 + 6H2O\", without explanation. If you can't balance the equation, just reply \"Unknown\"", openaiInput.input.get(0).content);
        assertEquals("user", openaiInput.input.get(1).role);
        assertEquals("Fe + Cl2 = FeCl3", openaiInput.input.get(1).content);
        assertEquals("2Fe + 3Cl_2 = 2FeCl_3", openaiInput.getIdeal());

        System.out.println(openaiInput);
    }

    @Test
    public void test_toOpenaiInput_3() throws JsonProcessingException {

        String json = """
               {
                 "input": [
                   {
                     "role": "system",
                     "content": "The text transformation rules are as follows: 1) if \\"a\\" character is encountered, replace it with \\"z\\". 2) if \\"e\\" character is encountered, replace it with \\"y\\". The rules are case-sensitive. Return the transformed text. Respond as concise as possible."
                   },
                   {
                     "role": "system",
                     "content": "How are you?",
                     "name": "example_user"
                   },
                   {
                     "role": "system",
                     "content": "How zry you?",
                     "name": "example_assistant"
                   },
                   {
                     "role": "user",
                     "content": "Hello! How are you?"
                   }
                 ],
                 "ideal": [
                   "Hyllo! How zry you?"
                 ]
               }""";

        OpenaiInput openaiInput = OpenaiJsonReader.toOpenaiInput(json);

        assertEquals(4, openaiInput.input.size());
        assertEquals("system", openaiInput.input.get(0).role);
        assertEquals("The text transformation rules are as follows: 1) if \"a\" character is encountered, replace it with \"z\". 2) if \"e\" character is encountered, replace it with \"y\". The rules are case-sensitive. Return the transformed text. Respond as concise as possible.", openaiInput.input.get(0).content);
        assertEquals("system", openaiInput.input.get(1).role);
        assertEquals("How are you?", openaiInput.input.get(1).content);
        assertEquals("system", openaiInput.input.get(2).role);
        assertEquals("How zry you?", openaiInput.input.get(2).content);
        assertEquals("user", openaiInput.input.get(3).role);
        assertEquals("Hello! How are you?", openaiInput.input.get(3).content);
        assertEquals("Hyllo! How zry you?", openaiInput.getIdeal());

        System.out.println(openaiInput);
    }

}
