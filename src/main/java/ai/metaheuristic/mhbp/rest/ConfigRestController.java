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

import ai.metaheuristic.mhbp.services.GitRepoService;
import ai.metaheuristic.mhbp.services.GitSourcingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;

/**
 * @author Sergio Lissner
 * Date: 4/14/2023
 * Time: 6:13 PM
 */
@RestController
@RequestMapping("/rest/v1/dispatcher/config")
@Slf4j
//@Profile("!stub-data")
//@CrossOrigin
@RequiredArgsConstructor
public class ConfigRestController {

    public final GitSourcingService gitSourcingService;
    public final GitRepoService gitRepoService;

    @PreAuthorize("hasAnyRole('MAIN_ADMIN', 'MAIN_OPERATOR', 'MAIN_SUPPORT', 'MANAGER')")
    @GetMapping("/info")
    public String info() {
        return ""+gitSourcingService.getGitStatus();
    }

    @PreAuthorize("hasAnyRole('MAIN_ADMIN', 'MAIN_OPERATOR', 'MAIN_SUPPORT', 'MANAGER')")
    @GetMapping("/init-kb")
    public String initKb() {
        return gitRepoService.initRepo();
    }
}
