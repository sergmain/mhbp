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

import ai.metaheuristic.mhbp.api.ApiService;
import ai.metaheuristic.mhbp.data.ApiData;
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
 * Date: 3/19/2023
 * Time: 10:38 PM
 */
@RestController
@RequestMapping("/rest/v1/dispatcher/api")
@Slf4j
@RequiredArgsConstructor
public class ApiRestController {

    private final ApiService apiService;
    private final UserContextService userContextService;

    @GetMapping("/evaluate/{apiId}")
    public OperationStatusRest evaluate(@PathVariable @Nullable Long apiId, Authentication authentication) {
        RequestContext context = userContextService.getContext(authentication);
        return apiService.evaluate(apiId, context);
    }

    @PostMapping("/run-evaluation")
    public OperationStatusRest runEvaluation(Long id, Authentication authentication) {
        RequestContext context = userContextService.getContext(authentication);
        return apiService.evaluate(id, context);
    }

    @GetMapping("/apis")
    public ApiData.Apis apis(Pageable pageable, Authentication authentication) {
        RequestContext context = userContextService.getContext(authentication);
        final ApiData.Apis apis = apiService.getApis(pageable, context);
        return apis;
    }

    @GetMapping("/api/{apiId}")
    public ApiData.Api apis(@PathVariable @Nullable Long apiId, Authentication authentication) {
        RequestContext context = userContextService.getContext(authentication);
        final ApiData.Api api = apiService.getApi(apiId, context);
        return api;
    }

    @PostMapping("/api-add-commit")
//    @PreAuthorize("hasAnyRole('MASTER_ASSET_MANAGER', 'ADMIN', 'DATA')")
    public OperationStatusRest addFormCommit(
            @RequestParam(name = "name") String name,
            @RequestParam(name = "code") String code,
            @RequestParam(name = "scheme") String scheme,
            Authentication authentication) {
        RequestContext context = userContextService.getContext(authentication);

        return apiService.createApi(name, code, scheme, context);
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
        return apiService.deleteApiById(id, context);
    }

}
