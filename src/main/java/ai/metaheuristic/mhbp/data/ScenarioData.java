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

import ai.metaheuristic.mhbp.beans.ScenarioGroup;
import ai.metaheuristic.mhbp.yaml.scenario.ScenarioParams;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;

import java.util.List;

/**
 * @author Sergio Lissner
 * Date: 5/4/2023
 * Time: 7:08 PM
 */
public class ScenarioData {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ApiUid {
        public Long id;
        public String uid;
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ScenarioUidsForAccount extends BaseDataClass {
        public List<ApiUid> apis;
    }

    @RequiredArgsConstructor
    public static class ScenarioGroupsResult extends BaseDataClass {
        public final Slice<SimpleScenarioGroup> scenarioGroups;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class ScenariosResult extends BaseDataClass {
        public Page<SimpleScenario> scenarios;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SimpleScenarioGroup {
        public long scenarioGroupId;
        public long createdOn;
        public String name;
        public String description;

        public SimpleScenarioGroup(ScenarioGroup sg) {
            this.scenarioGroupId = sg.id;
            this.createdOn = sg.createdOn;
            this.name = sg.name;
            this.description = sg.description;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SimpleScenarioStep {
        public long scenarioGroupId;
        public String uuid;
        public long apiId;
        public String apiCode;
        public String name;
        public String prompt;
        public String answer;

        public SimpleScenarioStep(Long scenarioGroupId, ScenarioData.ApiUid apiUid, ScenarioParams.Step step) {
            this.scenarioGroupId = scenarioGroupId;
            this.uuid = step.uuid;
            this.apiId = apiUid.id;
            this.apiCode = apiUid.uid;
            this.name = step.name;
            this.prompt = step.p;
            this.answer = step.a;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class SimpleScenarioSteps extends BaseDataClass {
        public List<SimpleScenarioStep> steps;
    }

}

