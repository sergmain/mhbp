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

package ai.metaheuristic.mhbp.data;

import ai.metaheuristic.mhbp.beans.Answer;
import ai.metaheuristic.mhbp.yaml.answer.AnswerParams;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.Collections;

/**
 * @author Sergio Lissner
 * Date: 4/19/2023
 * Time: 4:16 PM
 */
public class ErrorData {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SimpleError {
        public Long id;
        public Long sessionId;
        public String p;
        public String a;
        public String raw;

        public SimpleError(Answer answer) {
            this.id = answer.id;
            this.sessionId = answer.sessionId;
            AnswerParams answerParams = answer.getAnswerParams();
            this.p = answerParams.expected!=null ? answerParams.expected.prompt : "<null>";
            this.a = answerParams.expected!=null ? answerParams.expected.answer : "<null>";
            this.raw = answerParams.raw;
        }
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    @NoArgsConstructor
    public static class ErrorResult extends BaseDataClass {
        public SimpleError error;

        public ErrorResult(String errorMessage) {
            this.errorMessages = Collections.singletonList(errorMessage);
        }

        public ErrorResult(SimpleError error, String errorMessage) {
            this.error = error;
            this.errorMessages = Collections.singletonList(errorMessage);
        }

        public ErrorResult(SimpleError error) {
            this.error = error;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class ErrorsResult extends BaseDataClass {
        public Page<SimpleError> errors;

        public ErrorsResult(String errorMessage) {
            this.errorMessages = Collections.singletonList(errorMessage);
        }
    }
}
