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

import ai.metaheuristic.mhbp.data.KbData;
import ai.metaheuristic.mhbp.data.OperationStatusRest;
import ai.metaheuristic.mhbp.data.RequestContext;
import ai.metaheuristic.mhbp.kb.KbService;
import ai.metaheuristic.mhbp.kb.KbTxService;
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
 * Time: 3:31 PM
 */
@RestController
@RequestMapping("/rest/v1/dispatcher/kb")
@Slf4j
@RequiredArgsConstructor
public class KbRestController {

    private final KbService kbService;
    private final KbTxService kbTxService;
    private final UserContextService userContextService;

    @GetMapping("/kbs")
    public KbData.Kbs auths(Pageable pageable, Authentication authentication) {
        RequestContext context = userContextService.getContext(authentication);
        final KbData.Kbs kbs = kbService.getKbs(pageable, context);
        return kbs;
    }

    @GetMapping("/kb/{kbId}")
    public KbData.Kb apis(@PathVariable @Nullable Long kbId, Authentication authentication) {
        RequestContext context = userContextService.getContext(authentication);
        final KbData.Kb api = kbService.getKb(kbId, context);
        return api;
    }

    @PostMapping("/kb-add-commit")
//    @PreAuthorize("hasAnyRole('MASTER_ASSET_MANAGER', 'ADMIN', 'DATA')")
    public OperationStatusRest addFormCommit(
            @RequestParam(name = "code") String code,
            @RequestParam(name = "params") String params,
            Authentication authentication) {
        RequestContext context = userContextService.getContext(authentication);

        return kbTxService.createKb(code, params, context);
    }

    @PostMapping("/kb-delete-commit")
//    @PreAuthorize("hasAnyRole('MASTER_ASSET_MANAGER', 'ADMIN', 'DATA')")
    public OperationStatusRest deleteCommit(Long id, Authentication authentication) {
        RequestContext context = userContextService.getContext(authentication);
        return kbTxService.deleteKbById(id, context);
    }

    @PostMapping("/kb-init")
//    @PreAuthorize("hasAnyRole('MASTER_ASSET_MANAGER', 'ADMIN', 'DATA')")
    public OperationStatusRest initKb(Long id, Authentication authentication) {
        RequestContext context = userContextService.getContext(authentication);
        return kbService.initKb(id, context);
    }

}
