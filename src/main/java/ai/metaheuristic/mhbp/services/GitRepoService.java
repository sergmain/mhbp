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

package ai.metaheuristic.mhbp.services;

import ai.metaheuristic.mhbp.Globals;
import ai.metaheuristic.mhbp.utils.JsonUtils;
import ai.metaheuristic.mhbp.utils.NetUtils;
import ai.metaheuristic.mhbp.utils.SystemProcessLauncher;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Sergio Lissner
 * Date: 4/14/2023
 * Time: 10:00 PM
 */
@Service
@RequiredArgsConstructor
public class GitRepoService {

    @Value("${MHBP_HOME}/git")
    public Path gitPath;

    public final Globals globals;
    public final GitSourcingService gitSourcingService;

    @SneakyThrows
    public String initRepo() {

        String s = "";
        for (Globals.Kb kb : globals.kb) {
            if (!kb.disabled && kb.git!=null) {
                Globals.Git git = kb.git;
                String code = NetUtils.asCode(git.repo);
                Path p = gitPath.resolve(code);
                if (Files.notExists(p)) {
                    Files.createDirectories(p);
                }
                SystemProcessLauncher.ExecResult execResult = gitSourcingService.prepareFunction(p.toFile(), git);
                final String asString = JsonUtils.getMapper().writeValueAsString(execResult);
                s += ("\n" + asString);
                System.out.println(asString);
            }
            if (kb.file!=null) {

            }
        }

        int i=0;
        return s;
    }
}
