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
import ai.metaheuristic.mhbp.events.EvaluateProviderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    private final ApplicationEventPublisher eventPublisher;

    @GetMapping("/scan/{providerCode}")
    public OperationStatusRest scan(@PathVariable String providerCode, Authentication authentication) {
        eventPublisher.publishEvent(new EvaluateProviderEvent(providerCode));

        return OperationStatusRest.OPERATION_STATUS_OK;
    }
}
