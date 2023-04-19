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

package ai.metaheuristic.mhbp.rest;

import ai.metaheuristic.mhbp.data.EvaluationData;
import ai.metaheuristic.mhbp.data.OperationStatusRest;
import ai.metaheuristic.mhbp.data.RequestContext;
import ai.metaheuristic.mhbp.evaluation.EvaluationService;
import ai.metaheuristic.mhbp.sec.UserContextService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * @author Sergio Lissner
 * Date: 4/15/2023
 * Time: 7:29 PM
 */
@RestController
@RequestMapping("/rest/v1/dispatcher/evaluation")
@Slf4j
@RequiredArgsConstructor
public class EvaluationsRestController {

    private final EvaluationService evaluationService;
    private final UserContextService userContextService;

    @GetMapping("/evaluate/{apiId}")
    public OperationStatusRest evaluate(@PathVariable @Nullable Long apiId, Authentication authentication) {
        RequestContext context = userContextService.getContext(authentication);
        return evaluationService.evaluate(apiId, context, 0);
    }

    @PostMapping("/run-evaluation")
    public OperationStatusRest runEvaluation(Long id, Authentication authentication) {
        RequestContext context = userContextService.getContext(authentication);
        return evaluationService.evaluate(id, context, 0);
    }

    @PostMapping("/run-test-evaluation")
    public OperationStatusRest runTestEvaluation(Long id, Authentication authentication) {
        RequestContext context = userContextService.getContext(authentication);
        return evaluationService.evaluate(id, context, 1);
    }

    @GetMapping("/evaluations")
    public EvaluationData.Evaluations evaluations(Pageable pageable, Authentication authentication) {
        RequestContext context = userContextService.getContext(authentication);
        final EvaluationData.Evaluations evaluations = evaluationService.getEvaluations(pageable, context);
        return evaluations;
    }

    @GetMapping(value = "/evaluation-add")
    public EvaluationData.EvaluationUidsForCompany batchAdd(Authentication authentication) {
        RequestContext context = userContextService.getContext(authentication);
        EvaluationData.EvaluationUidsForCompany result = evaluationService.getEvaluationUidsForCompany(context);
        return result;
    }


    @PostMapping("/evaluation-add-commit")
//    @PreAuthorize("hasAnyRole('MASTER_ASSET_MANAGER', 'ADMIN', 'DATA')")
    // apiId, kbIds
    public OperationStatusRest addFormCommit(
            @RequestParam(name = "code") String code,
            @RequestParam(name = "apiId") String apiId,
            @RequestParam(name = "kbIds") String[] kbIds, Authentication authentication) {
        RequestContext context = userContextService.getContext(authentication);
        return evaluationService.createEvaluation(code, apiId, kbIds, context.getCompanyId(), context.getAccountId());
    }

/*
    @PostMapping("/evaluation-edit-commit")
//    @PreAuthorize("hasAnyRole('MASTER_ASSET_MANAGER', 'ADMIN', 'DATA')")
    public SourceCodeApiData.SourceCodeResult editFormCommit(Long sourceCodeId, @RequestParam(name = "source") String sourceCodeYamlAsStr) {
        throw new IllegalStateException("Not supported any more");
    }
*/

    @PostMapping("/evaluation-delete-commit")
//    @PreAuthorize("hasAnyRole('MASTER_ASSET_MANAGER', 'ADMIN', 'DATA')")
    public OperationStatusRest deleteCommit(Long evaluationId, Authentication authentication) {
        RequestContext context = userContextService.getContext(authentication);
        return evaluationService.deleteEvaluationById(evaluationId, context);
    }


}
