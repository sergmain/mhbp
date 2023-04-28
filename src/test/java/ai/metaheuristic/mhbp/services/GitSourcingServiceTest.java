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

import ai.metaheuristic.mhbp.Consts;
import ai.metaheuristic.mhbp.Enums;
import ai.metaheuristic.mhbp.data.KbData;
import ai.metaheuristic.mhbp.kb.reader.openai.OpenaiJsonReader;
import ai.metaheuristic.mhbp.questions.QuestionData;
import ai.metaheuristic.mhbp.utils.SystemProcessLauncher;
import ai.metaheuristic.mhbp.yaml.kb.KbParams;
import ai.metaheuristic.mhbp.yaml.kb.KbParamsUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import static ai.metaheuristic.mhbp.services.GitSourcingService.*;
import static ai.metaheuristic.mhbp.services.GitSourcingService.getGitStatus;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Sergio Lissner
 * Date: 4/28/2023
 * Time: 12:33 AM
 */
public class GitSourcingServiceTest {

    public static final String repo = "https://github.com/sergmain/mhbp.git";

    @Test
    public void test_(@TempDir Path temp) throws IOException {
        String yaml = IOUtils.resourceToString("/kb/openai-format-math/kb-openai-format-math.yaml", StandardCharsets.UTF_8);

        KbParams kbParams = KbParamsUtils.UTILS.to(yaml);
        assertNotNull(kbParams.kb.git);

        GitStatusInfo statusInfo =  getGitStatus(new GitContext(30L, 100));
        assertEquals(Enums.GitStatus.installed, statusInfo.status);

        KbData.KbGit git = new KbParams.Git(kbParams.kb.git.repo, kbParams.kb.git.branch, kbParams.kb.git.commit);
        Path gitPath = temp.resolve("git");
        GitContext gitContext = new GitContext(60L, 100);

        SystemProcessLauncher.ExecResult result = GitRepoService.initGitRepo(git, gitPath, gitContext);
        assertNotNull(result.systemExecResult);
        assertTrue(result.systemExecResult.isOk);
        assertNotNull(result.repoDir);
        assertTrue(result.repoDir.exists());

        File f = new File(result.repoDir, "pom.xml");
        assertTrue(f.exists());
        assertTrue(f.isFile());

        QuestionData.Chapters chapters = OpenaiJsonReader.read(10L, result.repoDir.toPath(), kbParams.kb.git);

        assertEquals(1, chapters.chapters.size());
        assertEquals("math/simple-math.jsonl", chapters.chapters.get(0).chapterCode());
        assertEquals(2, chapters.chapters.get(0).list().size());
        assertEquals("answer 2+2 with digits only", chapters.chapters.get(0).list().get(0).q());
        assertEquals("4", chapters.chapters.get(0).list().get(0).a());
        assertEquals("answer square root of 9 with only digits", chapters.chapters.get(0).list().get(1).q());
        assertEquals("3", chapters.chapters.get(0).list().get(1).a());

        int i=0;
    }
}
