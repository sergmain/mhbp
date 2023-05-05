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

package ai.metaheuristic.mhbp.repositories;

import ai.metaheuristic.mhbp.beans.Scenario;
import ai.metaheuristic.mhbp.beans.ScenarioGroup;
import ai.metaheuristic.mhbp.data.SimpleScenario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Sergio Lissner
 * Date: 3/5/2023
 * Time: 1:02 AM
 */
@Repository
@Transactional
public interface ScenarioRepository extends CrudRepository<Scenario, Long> {

    @Transactional(readOnly = true)
    @Query(value= "select new ai.metaheuristic.mhbp.data.SimpleScenario(s.id, a.id, a.code, s.createdOn, s.name, s.description) " +
                  " from Scenario s, Api a where s.apiId=a.id and s.scenarioGroupId=:scenarioGroupId and s.accountId=:accountId")
    Page<SimpleScenario> findAllByScenarioGroupId(Pageable pageable, Long scenarioGroupId, long accountId);

}
