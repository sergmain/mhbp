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

import ai.metaheuristic.mhbp.data.SessionData;
import ai.metaheuristic.mhbp.data.OperationStatusRest;
import ai.metaheuristic.mhbp.data.RequestContext;
import ai.metaheuristic.mhbp.session.SessionService;
import ai.metaheuristic.mhbp.sec.UserContextService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Sergio Lissner
 * Date: 4/15/2023
 * Time: 7:29 PM
 */
@RestController
@RequestMapping("/rest/v1/dispatcher/evaluations")
@Slf4j
@RequiredArgsConstructor
public class EvaluationsRestController {

    private final SessionService evaluationService;
    private final UserContextService userContextService;

    @GetMapping("/evaluations")
    public SessionData.SessionStatuses evaluations(Pageable pageable) {
        final SessionData.SessionStatuses statuses = evaluationService.getStatuses(pageable);
        return statuses;
    }

/*
    @PostMapping("/evaluation-add-commit")
//    @PreAuthorize("hasAnyRole('MASTER_ASSET_MANAGER', 'ADMIN', 'DATA')")
    public SourceCodeApiData.SourceCodeResult addFormCommit(@RequestParam(name = "source") String sourceCodeYamlAsStr, Authentication authentication) {
        RequestContext context = userContextService.getContext(authentication);
        return sourceCodeTopLevelService.createSourceCode(sourceCodeYamlAsStr, context.getCompanyId());
    }

    @PostMapping("/evaluation-edit-commit")
//    @PreAuthorize("hasAnyRole('MASTER_ASSET_MANAGER', 'ADMIN', 'DATA')")
    public SourceCodeApiData.SourceCodeResult editFormCommit(Long sourceCodeId, @RequestParam(name = "source") String sourceCodeYamlAsStr) {
        throw new IllegalStateException("Not supported any more");
    }
*/

    @PostMapping("/evaluations-delete-commit")
//    @PreAuthorize("hasAnyRole('MASTER_ASSET_MANAGER', 'ADMIN', 'DATA')")
    public OperationStatusRest deleteCommit(Long evaluationId, Authentication authentication) {
        RequestContext context = userContextService.getContext(authentication);
        return evaluationService.deleteSessionById(evaluationId, context);
    }


}
