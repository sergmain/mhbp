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

import ai.metaheuristic.mhbp.Globals;
import ai.metaheuristic.mhbp.beans.ScenarioGroup;
import ai.metaheuristic.mhbp.data.OperationStatusRest;
import ai.metaheuristic.mhbp.data.RequestContext;
import ai.metaheuristic.mhbp.repositories.ScenarioGroupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public OperationStatusRest createScenario(String name, String description, String apiId, RequestContext context) {
        ScenarioGroup s = new ScenarioGroup();
        s.name = name;
        s.description = description;
        s.companyId = context.getCompanyId();
        s.accountId = context.getAccountId();
        s.createdOn = System.currentTimeMillis();

        scenarioGroupRepository.save(s);

        return OperationStatusRest.OPERATION_STATUS_OK;
    }
}
