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

import ai.metaheuristic.mhbp.Enums;
import ai.metaheuristic.mhbp.Globals;
import ai.metaheuristic.mhbp.beans.Api;
import ai.metaheuristic.mhbp.beans.Scenario;
import ai.metaheuristic.mhbp.beans.ScenarioGroup;
import ai.metaheuristic.mhbp.data.OperationStatusRest;
import ai.metaheuristic.mhbp.data.RequestContext;
import ai.metaheuristic.mhbp.repositories.ApiRepository;
import ai.metaheuristic.mhbp.repositories.ScenarioGroupRepository;
import ai.metaheuristic.mhbp.repositories.ScenarioRepository;
import ai.metaheuristic.mhbp.utils.S;
import ai.metaheuristic.mhbp.yaml.scenario.ScenarioParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * @author Sergio Lissner
 * Date: 5/4/2023
 * Time: 8:01 PM
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ScenarioTxService {

    public final Globals globals;
    public final ScenarioGroupRepository scenarioGroupRepository;
    public final ScenarioRepository scenarioRepository;
    public final ApiRepository apiRepository;

    @Transactional
    public OperationStatusRest createScenarioGroup(String name, String description, RequestContext context) {
        ScenarioGroup s = new ScenarioGroup();
        s.name = name;
        s.description = description;
        s.companyId = context.getCompanyId();
        s.accountId = context.getAccountId();
        s.createdOn = System.currentTimeMillis();

        scenarioGroupRepository.save(s);

        return OperationStatusRest.OPERATION_STATUS_OK;
    }

    @Transactional
    public OperationStatusRest createScenario(String scenarioGroupId, String name, String description, String apiId, RequestContext context) {
        if (S.b(scenarioGroupId)) {
            return new OperationStatusRest(Enums.OperationStatus.ERROR,"229.040 scenarioGroupId is null");
        }
        if (S.b(apiId)) {
            return new OperationStatusRest(Enums.OperationStatus.ERROR,"229.080 apiId is null");
        }
        Scenario s = new Scenario();
        s.scenarioGroupId = Long.parseLong(scenarioGroupId);
        s.apiId = Long.parseLong(apiId);
        s.name = name;
        s.description = description;
        s.accountId = context.getAccountId();
        s.createdOn = System.currentTimeMillis();
        s.updateParams( new ScenarioParams());

        scenarioRepository.save(s);

        return OperationStatusRest.OPERATION_STATUS_OK;
    }

    public OperationStatusRest createScenarioStep(String scenarioGroupId, String scenarioId, String name, String prompt, String apiId, RequestContext context) {
        if (S.b(scenarioGroupId)) {
            return new OperationStatusRest(Enums.OperationStatus.ERROR,"229.120 scenarioGroupId is null");
        }
        if (S.b(scenarioId)) {
            return new OperationStatusRest(Enums.OperationStatus.ERROR,"229.160 scenarioId is null");
        }
        if (S.b(apiId)) {
            return new OperationStatusRest(Enums.OperationStatus.ERROR,"229.200 apiId is null");
        }
        Api api = apiRepository.findById(Long.parseLong(apiId)).orElse(null);
        if (api==null || api.companyId!=context.getCompanyId()) {
            return new OperationStatusRest(Enums.OperationStatus.ERROR,"229.220 apiId");
        }

        Scenario s = scenarioRepository.findById(Long.parseLong(scenarioId)).orElse(null);
        if (s==null) {
            return new OperationStatusRest(Enums.OperationStatus.ERROR,"229.240 Scenario # " + scenarioId+" wasn't found");
        }
        if (s.accountId!=context.getAccountId()) {
            return new OperationStatusRest(Enums.OperationStatus.ERROR,"229.280 accountId");
        }
        if (s.scenarioGroupId!=Long.parseLong(scenarioGroupId)) {
            return new OperationStatusRest(Enums.OperationStatus.ERROR,"229.320 scenarioGroupId");
        }

        ScenarioParams sp = s.getScenarioParams();
        sp.steps.add(new ScenarioParams.Step(UUID.randomUUID().toString(), name, prompt, null, api.id, api.code));
        s.updateParams(sp);

        scenarioRepository.save(s);

        return OperationStatusRest.OPERATION_STATUS_OK;
    }

    @Transactional
    public OperationStatusRest deleteScenarioById(Long scenarioId, RequestContext context) {
        if (scenarioId==null) {
            return new OperationStatusRest(Enums.OperationStatus.ERROR,
                    "229.240 scenarioId is null");
        }
        Scenario scenario = scenarioRepository.findById(scenarioId).orElse(null);
        if (scenario == null) {
            return new OperationStatusRest(Enums.OperationStatus.ERROR,
                    "229.280 Scenario wasn't found, scenarioId: " + scenarioId);
        }
        if (scenario.accountId!=context.getAccountId()) {
            return new OperationStatusRest(Enums.OperationStatus.ERROR, "229.320 scenarioId: " + scenarioId);
        }

        scenarioRepository.delete(scenario);
        return OperationStatusRest.OPERATION_STATUS_OK;
    }

    @Transactional
    public OperationStatusRest deleteScenarioGroupById(Long scenarioGroupId, RequestContext context) {
        if (scenarioGroupId==null) {
            return new OperationStatusRest(Enums.OperationStatus.ERROR,
                    "229.360 scenarioGroupId is null");
        }
        ScenarioGroup scenarioGroup = scenarioGroupRepository.findById(scenarioGroupId).orElse(null);
        if (scenarioGroup == null) {
            return new OperationStatusRest(Enums.OperationStatus.ERROR,
                    "229.400 Scenario wasn't scenarioGroupId, scenarioId: " + scenarioGroupId);
        }
        if (scenarioGroup.accountId!=context.getAccountId()) {
            return new OperationStatusRest(Enums.OperationStatus.ERROR, "239.440 scenarioGroupId: " + scenarioGroupId);
        }

        scenarioGroupRepository.delete(scenarioGroup);
        return OperationStatusRest.OPERATION_STATUS_OK;
    }

    @Transactional
    public OperationStatusRest deleteScenarioStep(Long scenarioId, String uuid, RequestContext context) {
        if (scenarioId==null) {
            return new OperationStatusRest(Enums.OperationStatus.ERROR,
                    "229.440 scenarioGroupId is null");
        }
        Scenario s = scenarioRepository.findById(scenarioId).orElse(null);
        if (s == null) {
            return new OperationStatusRest(Enums.OperationStatus.ERROR,
                    "229.480 Scenario wasn't scenarioGroupId, scenarioId: " + scenarioId);
        }
        if (s.accountId!=context.getAccountId()) {
            return new OperationStatusRest(Enums.OperationStatus.ERROR, "239.440 scenarioId: " + scenarioId);
        }

        ScenarioParams sp = s.getScenarioParams();
        sp.steps = sp.steps.stream().filter(o->!o.uuid.equals(uuid)).toList();
        s.updateParams(sp);

        scenarioRepository.save(s);
        return OperationStatusRest.OPERATION_STATUS_OK;
    }

    @Transactional
    public OperationStatusRest scenarioStepRearrange(Long scenarioId, String previousIndexStr, String currentIndexStr, RequestContext context) {
        if (scenarioId==null) {
            return new OperationStatusRest(Enums.OperationStatus.ERROR,
                    "229.480 scenarioGroupId is null");
        }
        Scenario s = scenarioRepository.findById(scenarioId).orElse(null);
        if (s == null) {
            return new OperationStatusRest(Enums.OperationStatus.ERROR,
                    "229.520 Scenario wasn't scenarioGroupId, scenarioId: " + scenarioId);
        }
        if (s.accountId!=context.getAccountId()) {
            return new OperationStatusRest(Enums.OperationStatus.ERROR, "239.560 scenarioId: " + scenarioId);
        }

        if (S.b(previousIndexStr) || S.b(currentIndexStr)) {
            return new OperationStatusRest(Enums.OperationStatus.ERROR, "239.565 indexes");
        }

        int previousIndex = Integer.parseInt(previousIndexStr);
        int currentIndex = Integer.parseInt(currentIndexStr);

        ScenarioParams sp = s.getScenarioParams();

        if (sp.steps.size()<=previousIndex || sp.steps.size()<= currentIndex) {
            return new OperationStatusRest(Enums.OperationStatus.ERROR, "239.570 indexes >" + sp.steps.size());
        }

        ScenarioParams.Step step = sp.steps.remove(previousIndex);
        sp.steps.add(currentIndex, step);
        s.updateParams(sp);

        scenarioRepository.save(s);
        return OperationStatusRest.OPERATION_STATUS_OK;
    }
}
