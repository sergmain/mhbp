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

package ai.metaheuristic.mhbp.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * @author Sergio Lissner
 * Date: 3/19/2023
 * Time: 3:06 PM
 */
@RestController
@RequestMapping("/rest/v1/provider/simple/stub")
@Slf4j
//@CrossOrigin
@RequiredArgsConstructor
public class StubProviderController {

    public record SimpleStubAnswer(int topProb, String code, String txt) {}

    public static Map<String, List<SimpleStubAnswer>> answers = Map.of(
            "q1", List.of(
                    new SimpleStubAnswer(90, "q1", "Bad question #1"),
                    new SimpleStubAnswer(100, "q1", "Good question #1")),

            "q2", List.of(new SimpleStubAnswer(100, "q2", "Good question #2")),
            "q3", List.of(new SimpleStubAnswer(100, "q3", "Good question #3")),
            "q4", List.of(new SimpleStubAnswer(100, "q4", "Bad question #4"))
    );

    List<SimpleStubAnswer> defAnswer = List.of(new SimpleStubAnswer(100, "", "Unknown context of question #5"));

    public static final Random r = new Random();

    @RequestMapping(method={GET, POST}, value = "/question")
    //@PreAuthorize("hasAnyRole('MAIN_ADMIN')")
    public String question(@RequestParam(name = "q") String question){
        int rInt = r.nextInt(100);
        return answers.getOrDefault(question, defAnswer).stream()
                .filter(o-> o.topProb >= rInt)
                .findFirst()
                .map(o->o.txt)
                .orElseThrow(IllegalStateException::new);
    }

}