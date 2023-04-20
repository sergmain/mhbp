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

import ai.metaheuristic.mhbp.yaml.kb.KbParams;
import ai.metaheuristic.mhbp.yaml.kb.KbParamsUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Sergio Lissner
 * Date: 4/19/2023
 * Time: 11:52 PM
 */
public class OpenaiJsonReaderTest {

    @Test
    public void test_read() throws IOException {

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


        Path mhbpHome = Path.of(System.getenv("MHBP_HOME"));
        var qas = OpenaiJsonReader.read(mhbpHome, kbParams.kb.git);

        int i=0;
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
        int i=0;
    }

}
