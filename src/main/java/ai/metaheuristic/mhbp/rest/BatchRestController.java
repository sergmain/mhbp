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

import ai.metaheuristic.mhbp.data.BatchData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Sergio Lissner
 * Date: 3/8/2023
 * Time: 6:36 PM
 */
@RestController
@RequestMapping("/rest/v1/dispatcher/batch")
@Slf4j
@CrossOrigin
@RequiredArgsConstructor
public class BatchRestController {
    // http://localhost:8080/rest/v1/dispatcher/batch/batch-exec-statuses

    @GetMapping("/batch-exec-statuses")
    @PreAuthorize("isAuthenticated()")
    public BatchData.ExecStatuses batchExecStatuses(Authentication authentication) {
        return new BatchData.ExecStatuses(List.of());
    }
}
