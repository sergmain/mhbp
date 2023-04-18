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

package ai.metaheuristic.mhbp.evaluation;

import ai.metaheuristic.mhbp.api.ApiService;
import ai.metaheuristic.mhbp.beans.Evaluation;
import ai.metaheuristic.mhbp.data.OperationStatusRest;
import ai.metaheuristic.mhbp.kb.KbService;
import ai.metaheuristic.mhbp.repositories.EvaluationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Sergio Lissner
 * Date: 4/17/2023
 * Time: 8:47 PM
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class EvaluationTxService {

    public final ApiService apiService;
    public final KbService kbService;
    public final EvaluationRepository evaluationRepository;


    @Transactional
    public OperationStatusRest createEvaluation(String code, String apiId, String[] kbIds, long companyId, long accountId) {
        Evaluation eval = new Evaluation();
        eval.companyId = companyId;
        eval.accountId = accountId;
        eval.createdOn = System.currentTimeMillis();
        eval.apiId = Long.parseLong(apiId);
        eval.kbIds = List.of(kbIds);
        eval.code = code;

        evaluationRepository.save(eval);

        return OperationStatusRest.OPERATION_STATUS_OK;
    }
}
