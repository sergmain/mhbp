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

import ai.metaheuristic.mhbp.auth.AuthService;
import ai.metaheuristic.mhbp.data.AuthData;
import ai.metaheuristic.mhbp.data.OperationStatusRest;
import ai.metaheuristic.mhbp.data.RequestContext;
import ai.metaheuristic.mhbp.sec.UserContextService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * @author Sergio Lissner
 * Date: 4/13/2023
 * Time: 12:15 AM
 */
@RestController
@RequestMapping("/rest/v1/dispatcher/auth")
@Slf4j
@RequiredArgsConstructor
public class AuthRestController {

    private final AuthService authService;
    private final UserContextService userContextService;

    @GetMapping("/auths")
    public AuthData.Auths auths(Pageable pageable, Authentication authentication) {
        RequestContext context = userContextService.getContext(authentication);
        final AuthData.Auths auths = authService.getAuths(pageable, context);
        return auths;
    }

    @GetMapping("/auth/{authId}")
    public AuthData.Auth apis(@PathVariable @Nullable Long authId, Authentication authentication) {
        RequestContext context = userContextService.getContext(authentication);
        final AuthData.Auth api = authService.getAuth(authId, context);
        return api;
    }

    @PostMapping("/auth-add-commit")
//    @PreAuthorize("hasAnyRole('MASTER_ASSET_MANAGER', 'ADMIN', 'DATA')")
    public OperationStatusRest addFormCommit(
            @RequestParam(name = "code") String code,
            @RequestParam(name = "params") String params,
            Authentication authentication) {
        RequestContext context = userContextService.getContext(authentication);

        return authService.createAuth(code, params, context);
    }

    @PostMapping("/auth-delete-commit")
//    @PreAuthorize("hasAnyRole('MASTER_ASSET_MANAGER', 'ADMIN', 'DATA')")
    public OperationStatusRest deleteCommit(Long id, Authentication authentication) {
        RequestContext context = userContextService.getContext(authentication);
        return authService.deleteAuthById(id, context);
    }

}
