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
import ai.metaheuristic.mhbp.data.ApiData;
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

    @GetMapping("/apis")
    public ApiData.Apis apis(Pageable pageable, Authentication authentication) {
        RequestContext context = userContextService.getContext(authentication);
        final AuthData.Auths apis = authService.getAuths(pageable, context);
        return apis;
    }

    @GetMapping("/api/{apiId}")
    public ApiData.Api apis(@PathVariable @Nullable Long apiId, Authentication authentication) {
        RequestContext context = userContextService.getContext(authentication);
        final ApiData.Api api = authService.getApi(apiId, context);
        return api;
    }

    @PostMapping("/api-add-commit")
//    @PreAuthorize("hasAnyRole('MASTER_ASSET_MANAGER', 'ADMIN', 'DATA')")
    public OperationStatusRest addFormCommit(
            @RequestParam(name = "name") String name,
            @RequestParam(name = "code") String code,
            @RequestParam(name = "params") String params,
            @RequestParam(name = "scheme") String scheme,
            Authentication authentication) {
        RequestContext context = userContextService.getContext(authentication);

        return authService.createApi(name, code, params, scheme, context);
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

    @PostMapping("/api-delete-commit")
//    @PreAuthorize("hasAnyRole('MASTER_ASSET_MANAGER', 'ADMIN', 'DATA')")
    public OperationStatusRest deleteCommit(Long id, Authentication authentication) {
        RequestContext context = userContextService.getContext(authentication);
        return authService.deleteApiById(id, context);
    }

}
