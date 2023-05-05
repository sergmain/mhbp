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

package ai.metaheuristic.mhbp.scenario;

import ai.metaheuristic.mhbp.api.ApiService;
import ai.metaheuristic.mhbp.beans.Scenario;
import ai.metaheuristic.mhbp.beans.ScenarioGroup;
import ai.metaheuristic.mhbp.data.RequestContext;
import ai.metaheuristic.mhbp.data.ScenarioData;
import ai.metaheuristic.mhbp.data.SimpleScenario;
import ai.metaheuristic.mhbp.repositories.ApiRepository;
import ai.metaheuristic.mhbp.repositories.ScenarioGroupRepository;
import ai.metaheuristic.mhbp.repositories.ScenarioRepository;
import ai.metaheuristic.mhbp.utils.ControllerUtils;
import ai.metaheuristic.mhbp.yaml.scenario.ScenarioParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Sergio Lissner
 * Date: 5/4/2023
 * Time: 7:07 PM
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ScenarioService {

    private final ApiService apiService;
    private final ApiRepository apiRepository;
    private final ScenarioGroupRepository scenarioGroupRepository;
    private final ScenarioRepository scenarioRepository;

    public ScenarioData.ScenarioGroupsResult getScenarioGroups(Pageable pageable, RequestContext context) {
        pageable = ControllerUtils.fixPageSize(10, pageable);

        Page<ScenarioGroup> scenarioGroups = scenarioGroupRepository.findAllByAccountId(pageable, context.getAccountId());
        List<ScenarioData.SimpleScenarioGroup> list = scenarioGroups.stream().map(ScenarioData.SimpleScenarioGroup::new).toList();
        var sorted = list.stream().sorted((o1, o2)->Long.compare(o2.scenarioGroupId, o1.scenarioGroupId)).collect(Collectors.toList());
        return new ScenarioData.ScenarioGroupsResult(new PageImpl<>(sorted, pageable, list.size()));
    }

    public ScenarioData.ScenariosResult getScenarios(Pageable pageable, Long scenarioGroupId, RequestContext context) {
        if (scenarioGroupId==null) {
            return new ScenarioData.ScenariosResult(Page.empty(pageable));
        }
        pageable = ControllerUtils.fixPageSize(10, pageable);
        Page<SimpleScenario> scenarios = scenarioRepository.findAllByScenarioGroupId(pageable, scenarioGroupId, context.getAccountId());

        return new ScenarioData.ScenariosResult(scenarios);
    }

    public ScenarioData.ScenarioUidsForAccount getScenarioUidsForAccount(RequestContext context) {
        ScenarioData.ScenarioUidsForAccount r = new ScenarioData.ScenarioUidsForAccount();
        r.apis = apiService.getApisAllowedForCompany(context).stream()
                .map(o->new ScenarioData.ApiUid(o.id, o.code))
                .toList();
        return r;
    }

    public ScenarioData.SimpleScenarioSteps getScenarioSteps(Long scenarioGroupId, Long scenarioId, RequestContext context) {
        if (scenarioGroupId==null || scenarioId==null) {
            return new ScenarioData.SimpleScenarioSteps(List.of());
        }
        Scenario s = scenarioRepository.findById(scenarioId).orElse(null);
        if (s==null || s.scenarioGroupId!=scenarioGroupId || s.accountId!=context.getAccountId()) {
            return new ScenarioData.SimpleScenarioSteps(List.of());
        }
        ScenarioParams scenarioParams = s.getScenarioParams();

        Map<Long, ScenarioData.ApiUid> apis = new HashMap<>();
        List<ScenarioData.SimpleScenarioStep> steps = scenarioParams.steps.stream()
                .map(o-> {
                    ScenarioData.ApiUid apiUid = apis.computeIfAbsent(o.apiId,
                            (apiId)-> apiRepository.findById(apiId)
                                    .map(api->new ScenarioData.ApiUid(api.id, api.code))
                                    .orElse(new ScenarioData.ApiUid(0L, "<broken API Id>")));
                    return new ScenarioData.SimpleScenarioStep(scenarioGroupId, apiUid, o);
                })
                .toList();

        return new ScenarioData.SimpleScenarioSteps(steps);
    }
}
