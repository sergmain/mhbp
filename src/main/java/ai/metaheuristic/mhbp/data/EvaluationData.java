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

import lombok.*;
import org.springframework.data.domain.Slice;

import java.util.List;

/**
 * @author Sergio Lissner
 * Date: 4/17/2023
 * Time: 1:26 AM
 */
public class EvaluationData {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ApiUid {
        public Long id;
        public String uid;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class KbUid {
        public Long id;
        public String uid;
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    @AllArgsConstructor
    @NoArgsConstructor
    public static class EvaluationUidsForCompany extends BaseDataClass {
        public List<ApiUid> apis;
        public List<KbUid> kbs;
    }

    public static class SimpleEvaluation {
        public long evaluationId;
        public long createdOn;
        public String code;

        public SimpleEvaluation(ai.metaheuristic.mhbp.beans.Evaluation eval) {
            this.evaluationId = eval.id;
            this.code = eval.code;
            this.createdOn = eval.createdOn;
        }
    }

    @RequiredArgsConstructor
    public static class Evaluations extends BaseDataClass {
        public final Slice<SimpleEvaluation> evaluations;
    }

}
