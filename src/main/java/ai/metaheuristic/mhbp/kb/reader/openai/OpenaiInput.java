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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sergio Lissner
 * Date: 4/20/2023
 * Time: 4:42 PM
 */
public class OpenaiInput {

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

    public static class Input {
        public String role;
        public String content;
        public String name;
    }

    public Object ideal;
    public final List<Input> input = new ArrayList<>();

    public String getIdeal() {
        if (ideal instanceof String s) {
            return s;
        }
        else if (ideal instanceof List l) {
            final Object o = l.get(0);
            if (o instanceof String s1) {
                return s1;
            }
            throw new RuntimeException("Actual class: " + o.getClass().getName());
        }
        throw new RuntimeException("Not supported class: " + ideal.getClass().getName());
    }
}
