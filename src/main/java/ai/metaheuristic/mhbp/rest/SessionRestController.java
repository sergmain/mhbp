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

import ai.metaheuristic.mhbp.data.*;
import ai.metaheuristic.mhbp.session.SessionService;
import ai.metaheuristic.mhbp.sec.UserContextService;
import ai.metaheuristic.mhbp.session.SessionTxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * @author Sergio Lissner
 * Date: 3/26/2023
 * Time: 2:57 AM
 */
@RestController
@RequestMapping("/rest/v1/dispatcher/session")
@Slf4j
@RequiredArgsConstructor
public class SessionRestController {

    private final SessionService sessionService;
    private final SessionTxService sessionTxService;
    private final UserContextService userContextService;

    @GetMapping("/sessions")
    public SessionData.SessionStatuses sessions(Pageable pageable) {
        final SessionData.SessionStatuses statuses = sessionService.getStatuses(pageable);
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

    @PostMapping("/session-delete-commit")
//    @PreAuthorize("hasAnyRole('MASTER_ASSET_MANAGER', 'ADMIN', 'DATA')")
    public OperationStatusRest deleteCommit(Long sessionId, Authentication authentication) {
        RequestContext context = userContextService.getContext(authentication);
        return sessionTxService.deleteSessionById(sessionId, context);
    }

    @GetMapping("/session-errors/{sessionId}")
    public ErrorData.ErrorsResult errors(Pageable pageable, @PathVariable Long sessionId, Authentication authentication) {
        RequestContext context = userContextService.getContext(authentication);
        final ErrorData.ErrorsResult errors = sessionService.getErrors(pageable, sessionId, context);
        return errors;
    }

}
