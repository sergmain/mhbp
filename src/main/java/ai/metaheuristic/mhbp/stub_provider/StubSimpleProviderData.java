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

package ai.metaheuristic.mhbp.stub_provider;

import java.time.LocalDateTime;

/**
 * @author Sergio Lissner
 * Date: 3/19/2023
 * Time: 3:18 PM
 */
public class StubSimpleProviderData {
    public class QuestionToProvider {
        public long id;
        public long policyId;
        public String question;
    }

    public class AnswerFromProvider {
        public String guid;
        public String text;
        public String modelId;
        public LocalDateTime dateTime;

    }
}
