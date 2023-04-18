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

import ai.metaheuristic.mhbp.Enums;
import ai.metaheuristic.mhbp.api.ApiService;
import ai.metaheuristic.mhbp.beans.Api;
import ai.metaheuristic.mhbp.beans.Evaluation;
import ai.metaheuristic.mhbp.beans.Kb;
import ai.metaheuristic.mhbp.data.EvaluationData;
import ai.metaheuristic.mhbp.data.OperationStatusRest;
import ai.metaheuristic.mhbp.data.RequestContext;
import ai.metaheuristic.mhbp.events.EvaluateProviderEvent;
import ai.metaheuristic.mhbp.kb.KbService;
import ai.metaheuristic.mhbp.repositories.ApiRepository;
import ai.metaheuristic.mhbp.repositories.EvaluationRepository;
import ai.metaheuristic.mhbp.repositories.KbRepository;
import ai.metaheuristic.mhbp.utils.ControllerUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
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
    public final ApplicationEventPublisher eventPublisher;
    public final ApiRepository apiRepository;
    public final KbRepository kbRepository;

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

    public OperationStatusRest evaluate(@Nullable Long evaluationId, RequestContext context, int limit) {
        if (evaluationId==null) {
            return OperationStatusRest.OPERATION_STATUS_OK;
        }
        Evaluation evaluation = evaluationRepository.findById(evaluationId).orElse(null);
        if (evaluation == null) {
            return new OperationStatusRest(Enums.OperationStatus.ERROR,
                    "#565.150 Evaluation wasn't found, evaluationId: " + evaluationId, null);
        }
        if (evaluation.companyId!=context.getCompanyId()) {
            return new OperationStatusRest(Enums.OperationStatus.ERROR, "#565.200 evaluationId: " + evaluationId);
        }
        Api api = apiRepository.findById(evaluation.apiId).orElse(null);
        if (api==null || api.companyId!=context.getCompanyId()) {
            return new OperationStatusRest(Enums.OperationStatus.ERROR, "#565.220 Reference to API is broken, evaluationId: " + evaluationId);
        }
        if (evaluation.kbIds.isEmpty()) {
            return new OperationStatusRest(Enums.OperationStatus.ERROR, "#565.240 Reference to KB is empty, evaluationId: " + evaluationId);
        }
        for (String kbIdStr : evaluation.kbIds) {
            long kbId = Long.parseLong(kbIdStr);
            Kb kb = kbRepository.findById(kbId).orElse(null);
            if (kb==null || kb.companyId!=context.getCompanyId()) {
                return new OperationStatusRest(Enums.OperationStatus.ERROR, "#565.260 Reference to KB is broken, evaluationId: " + evaluationId);
            }
        }

        eventPublisher.publishEvent(new EvaluateProviderEvent(evaluationId, limit, context.getAccountId()));
        return OperationStatusRest.OPERATION_STATUS_OK;
    }

}
