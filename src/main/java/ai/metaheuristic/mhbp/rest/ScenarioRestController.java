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

import ai.metaheuristic.mhbp.data.OperationStatusRest;
import ai.metaheuristic.mhbp.data.RequestContext;
import ai.metaheuristic.mhbp.data.ScenarioData;
import ai.metaheuristic.mhbp.scenario.ScenarioService;
import ai.metaheuristic.mhbp.scenario.ScenarioTxService;
import ai.metaheuristic.mhbp.sec.UserContextService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * @author Sergio Lissner
 * Date: 5/4/2023
 * Time: 7:06 PM
 */
@RestController
@RequestMapping("/rest/v1/dispatcher/scenario")
@Slf4j
@RequiredArgsConstructor
public class ScenarioRestController {

    private final ScenarioService scenarioService;
    private final ScenarioTxService scenarioTxService;
    private final UserContextService userContextService;

    @GetMapping("/scenario-groups")
    public ScenarioData.ScenarioGroupsResult scenarioGroups(Pageable pageable, Authentication authentication) {
        RequestContext context = userContextService.getContext(authentication);
        final ScenarioData.ScenarioGroupsResult result = scenarioService.getScenarioGroups(pageable, context);
        return result;
    }

    @GetMapping("/scenarios/{scenarioGroupId}")
    public ScenarioData.ScenariosResult scenarios(Pageable pageable, @PathVariable Long scenarioGroupId, Authentication authentication) {
        RequestContext context = userContextService.getContext(authentication);
        final ScenarioData.ScenariosResult result = scenarioService.getScenarios(pageable, scenarioGroupId, context);
        return result;
    }

    // /dispatcher/scenario/scenarios/3/scenario/1/steps:
    @GetMapping("/scenarios/{scenarioGroupId}/scenario/{scenarioId}/steps")
    public ScenarioData.SimpleScenarioSteps scenarios(@PathVariable Long scenarioGroupId, @PathVariable Long scenarioId, Authentication authentication) {
        RequestContext context = userContextService.getContext(authentication);
        final ScenarioData.SimpleScenarioSteps result = scenarioService.getScenarioSteps(scenarioGroupId, scenarioId, context);
        return result;
    }

    @GetMapping(value = "/scenario-add")
    public ScenarioData.ScenarioUidsForAccount scenarioAdd(Authentication authentication) {
        RequestContext context = userContextService.getContext(authentication);
        ScenarioData.ScenarioUidsForAccount result = scenarioService.getScenarioUidsForAccount(context);
        return result;
    }

    @GetMapping(value = "/scenario-step-add")
    public ScenarioData.ScenarioUidsForAccount scenarioStepAdd(Authentication authentication) {
        RequestContext context = userContextService.getContext(authentication);
        ScenarioData.ScenarioUidsForAccount result = scenarioService.getScenarioUidsForAccount(context);
        return result;
    }

    @PostMapping(value = "/scenario-step-rearrange")
    public OperationStatusRest scenarioStepRearrange(
            @RequestParam(name = "scenarioId") Long scenarioId,
            @RequestParam(name = "prev") String previousIndex,
            @RequestParam(name = "curr") String currentIndex,
            Authentication authentication) {
        RequestContext context = userContextService.getContext(authentication);
        if (Objects.equals(previousIndex, currentIndex)) {
            return OperationStatusRest.OPERATION_STATUS_OK;
        }
        OperationStatusRest result = scenarioTxService.scenarioStepRearrange(scenarioId, previousIndex, currentIndex, context);
        return result;
    }


    @PostMapping("/scenario-group-add-commit")
//    @PreAuthorize("hasAnyRole('MASTER_ASSET_MANAGER', 'ADMIN', 'DATA')")
    public OperationStatusRest addScenarioGroupFormCommit(
            @RequestParam(name = "name") String name,
            @RequestParam(name = "description") String description,
            Authentication authentication) {
        RequestContext context = userContextService.getContext(authentication);
        return scenarioTxService.createScenarioGroup(name, description, context);
    }

    @PostMapping("/scenario-add-commit")
//    @PreAuthorize("hasAnyRole('MASTER_ASSET_MANAGER', 'ADMIN', 'DATA')")
    public OperationStatusRest addScenarioFormCommit(
            @RequestParam(name = "scenarioGroupId") String scenarioGroupId,
            @RequestParam(name = "name") String name,
            @RequestParam(name = "description") String description,
            @RequestParam(name = "apiId") String apiId,
            Authentication authentication) {
        RequestContext context = userContextService.getContext(authentication);
        return scenarioTxService.createScenario(scenarioGroupId, name, description, apiId, context);
    }

    @PostMapping("/scenario-step-add-commit")
//    @PreAuthorize("hasAnyRole('MASTER_ASSET_MANAGER', 'ADMIN', 'DATA')")
    public OperationStatusRest addScenarioStepFormCommit(
            @RequestParam(name = "scenarioGroupId") String scenarioGroupId,
            @RequestParam(name = "scenarioId") String scenarioId,
            @RequestParam(name = "name") String name,
            @RequestParam(name = "prompt") String prompt,
            @RequestParam(name = "apiId") String apiId,
            Authentication authentication) {
        RequestContext context = userContextService.getContext(authentication);
        return scenarioTxService.createScenarioStep(scenarioGroupId, scenarioId, name, prompt, apiId, context);
    }

    @PostMapping("/scenario-group-delete-commit")
//    @PreAuthorize("hasAnyRole('MASTER_ASSET_MANAGER', 'ADMIN', 'DATA')")
    public OperationStatusRest scenarioGroupDeleteCommit(Long scenarioGroupId, Authentication authentication) {
        RequestContext context = userContextService.getContext(authentication);
        return scenarioTxService.deleteScenarioGroupById(scenarioGroupId, context);
    }

    @PostMapping("/scenario-delete-commit")
//    @PreAuthorize("hasAnyRole('MASTER_ASSET_MANAGER', 'ADMIN', 'DATA')")
    public OperationStatusRest scenarioDeleteCommit(Long scenarioId, Authentication authentication) {
        RequestContext context = userContextService.getContext(authentication);
        return scenarioTxService.deleteScenarioById(scenarioId, context);
    }

    // /dispatcher/scenario/scenario-step-delete-commit: 404 OK
    @PostMapping("/scenario-step-delete-commit")
//    @PreAuthorize("hasAnyRole('MASTER_ASSET_MANAGER', 'ADMIN', 'DATA')")
    public OperationStatusRest scenarioDeleteCommit(Long scenarioId, String uuid, Authentication authentication) {
        RequestContext context = userContextService.getContext(authentication);
        return scenarioTxService.deleteScenarioStep(scenarioId, uuid, context);
    }

    @PostMapping("/scenario-duplicate")
//    @PreAuthorize("hasAnyRole('MASTER_ASSET_MANAGER', 'ADMIN', 'DATA')")
    public OperationStatusRest duplicateScenario(
            @RequestParam(name = "scenarioGroupId") String scenarioGroupId,
            @RequestParam(name = "scenarioId") String scenarioId,
            @RequestParam(name = "name") String name,
            @RequestParam(name = "prompt") String prompt,
            @RequestParam(name = "apiId") String apiId,
            Authentication authentication) {
        RequestContext context = userContextService.getContext(authentication);
        return scenarioService.duplicateScenario(scenarioGroupId, scenarioId, name, prompt, apiId, context);
    }

    @PostMapping("/scenario-run")
//    @PreAuthorize("hasAnyRole('MASTER_ASSET_MANAGER', 'ADMIN', 'DATA')")
    public OperationStatusRest runScenario(
            @RequestParam(name = "scenarioGroupId") String scenarioGroupId,
            @RequestParam(name = "scenarioId") String scenarioId,
            @RequestParam(name = "name") String name,
            @RequestParam(name = "prompt") String prompt,
            @RequestParam(name = "apiId") String apiId,
            Authentication authentication) {
        RequestContext context = userContextService.getContext(authentication);
        return scenarioService.runScenario(scenarioGroupId, scenarioId, name, prompt, apiId, context);
    }

}
