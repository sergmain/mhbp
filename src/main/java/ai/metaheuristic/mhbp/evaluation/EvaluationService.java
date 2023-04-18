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
import ai.metaheuristic.mhbp.data.EvaluationData;
import ai.metaheuristic.mhbp.data.OperationStatusRest;
import ai.metaheuristic.mhbp.data.RequestContext;
import ai.metaheuristic.mhbp.kb.KbService;
import ai.metaheuristic.mhbp.repositories.EvaluationRepository;
import ai.metaheuristic.mhbp.utils.ControllerUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Sergio Lissner
 * Date: 4/17/2023
 * Time: 1:30 AM
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class EvaluationService {

    public final ApiService apiService;
    public final KbService kbService;
    public final EvaluationTxService evaluationTxService;
    public final EvaluationRepository evaluationRepository;

    public EvaluationData.Evaluations getEvaluations(Pageable pageable, RequestContext context) {
        pageable = ControllerUtils.fixPageSize(20, pageable);

        Page<Evaluation> evaluations = evaluationRepository.findAllByCompanyUniqueId(pageable, context.getCompanyId());
        List<EvaluationData.SimpleEvaluation > list = evaluations.stream().map(EvaluationData.SimpleEvaluation ::new).toList();
        var sorted = list.stream().sorted((o1, o2)->Long.compare(o2.evaluationId, o1.evaluationId)).collect(Collectors.toList());
        return new EvaluationData.Evaluations(new PageImpl<>(sorted, pageable, list.size()));
    }

    public EvaluationData.EvaluationUidsForCompany getEvaluationUidsForCompany(RequestContext context) {
        EvaluationData.EvaluationUidsForCompany r = new EvaluationData.EvaluationUidsForCompany();

        r.apis = apiService.getApisAllowedForCompany(context).stream()
                .map(o->new EvaluationData.ApiUid(o.id, o.code))
                .toList();

        r.kbs = kbService.getKbsAllowedForCompany(context).stream()
                .map(o->new EvaluationData.KbUid(o.id, o.code))
                .toList();

        return r;
    }

    public OperationStatusRest createEvaluation(String code, String apiId, String[] kbIds, long companyId, long accountId) {
        evaluationTxService.createEvaluation(code, apiId, kbIds, companyId, accountId);
        return OperationStatusRest.OPERATION_STATUS_OK;
    }
}
