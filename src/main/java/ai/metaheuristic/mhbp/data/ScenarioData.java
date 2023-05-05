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
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;

/**
 * @author Sergio Lissner
 * Date: 5/4/2023
 * Time: 7:08 PM
 */
public class ScenarioData {

    @RequiredArgsConstructor
    public static class ScenarioGroupsResult extends BaseDataClass {
        public final Slice<SimpleScenarioGroup> scenarios;
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
}

