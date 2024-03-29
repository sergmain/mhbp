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
import ai.metaheuristic.mhbp.Globals;
import ai.metaheuristic.mhbp.data.ExecData;
import ai.metaheuristic.mhbp.data.KbData;
import ai.metaheuristic.mhbp.utils.asset.AssetFile;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.file.PathUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

import static ai.metaheuristic.mhbp.utils.SystemProcessLauncher.ExecResult;
import static ai.metaheuristic.mhbp.utils.SystemProcessLauncher.execCmd;

/**
 * @author Sergio Lissner
 * Date: 4/14/2023
 * Time: 5:42 PM
 */
@Service
@Slf4j
public class GitSourcingService {

    private static final List<String> GIT_VERSION_CMD = List.of("git", "--version");
    private static final String GIT_VERSION_PREFIX = "git version";
    private static final String GIT_PREFIX = "git";

    private final Globals globals;

    public final GitStatusInfo gitStatusInfo;

    public record GitContext(long timeout, int consoleOutputMaxLines) {
        public GitContext withTimeout(long newTimeout) {
            return new GitContext(newTimeout, this.consoleOutputMaxLines);}
    }

    public GitSourcingService(Globals globals) {
        this.globals = globals;
        this.gitStatusInfo = getGitStatus();
    }

    // !!! DO NOT CHANGE THIS CLASS !!!
    // If you need to, then copy it to ai.metaheuristic.ai.yaml.communication.processor.ProcessorCommParamsYaml
    // before any changing
    @Data
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(of={"status", "version", "error"})
    public static class GitStatusInfo {
        public Enums.GitStatus status;
        public String version;
        public String error;

        public GitStatusInfo(Enums.GitStatus status) {
            this.status = status;
        }
    }

    public GitStatusInfo getGitStatus() {
        return getGitStatus(new GitContext(30L, globals.max.consoleOutputLines));
    }

    public static GitStatusInfo getGitStatus(GitContext gitContext) {
        ExecResult result = execGitCmd(GIT_VERSION_CMD, gitContext);
        if (!result.ok) {
            log.warn("#027.010 Error of getting git status");
            log.warn("\tresult.ok: {}",  result.ok);
            log.warn("\tresult.error: {}",  result.error);
            log.warn("\tresult.functionDir: {}", result.repoDir!=null ? result.repoDir.getPath() : null);
            log.warn("\tresult.systemExecResult: {}",  result.systemExecResult);
            return new GitStatusInfo(Enums.GitStatus.error, null, "#027.010 Error: " + result.error);
        }

        // at this point result.systemExecResult must be not null, it can be null only if result.ok==false, but see above
        if (result.systemExecResult.exitCode!=0) {
            return new GitStatusInfo(
                    Enums.GitStatus.not_found, null,
                    "#027.013 Console: " + result.systemExecResult.console);
        }
        return new GitStatusInfo(Enums.GitStatus.installed, getGitVersion(result.systemExecResult.console.toLowerCase()), null);
    }

    public static ExecResult execGitCmd(List<String> gitVersionCmd, GitContext gitContext) {
        return execCmd(gitVersionCmd, gitContext.timeout, gitContext.consoleOutputMaxLines);
    }

    private static AssetFile prepareFunctionDir(final File resourceDir, String functionCode) {
        final AssetFile assetFile = new AssetFile();
        final File trgDir = new File(resourceDir, Enums.DataType.function.toString());
        log.info("Target dir: {}, exist: {}", trgDir.getAbsolutePath(), trgDir.exists() );
        if (!trgDir.exists() && !trgDir.mkdirs()) {
            assetFile.isError = true;
            log.error("#027.030 Can't create function dir: {}", trgDir.getAbsolutePath());
            return assetFile;
        }
        final String resId = functionCode.replace(':', '_');
        final File resDir = new File(trgDir, resId);
        log.info("Resource dir: {}, exist: {}", resDir.getAbsolutePath(), resDir.exists() );
        if (!resDir.exists() && !resDir.mkdirs()) {
            assetFile.isError = true;
            log.error("#027.040 Can't create resource dir: {}", resDir.getAbsolutePath());
            return assetFile;
        }
        assetFile.file = resDir;
        return assetFile;
    }

    @SneakyThrows
    public static ExecResult prepareRepo(final File resourceDir, KbData.KbGit git, GitContext gitContext) {

        File functionDir = resourceDir;
        File repoDir = new File(functionDir, Consts.REPO);
        log.info("#027.070 Target dir: {}, exist: {}", repoDir.getAbsolutePath(), repoDir.exists() );

        if (repoDir.exists() && PathUtils.isEmpty(repoDir.toPath())) {
            Files.delete(repoDir.toPath());
        }

        if (!repoDir.exists()) {
            ExecResult result = execClone(functionDir, git, gitContext);
            log.info("#027.080 Result of cloning repo: {}", result.toString());
            if (!result.ok || !result.systemExecResult.isOk()) {
                result = tryToRepairRepo(functionDir, git, gitContext);
                log.info("#027.090 Result of repairing of repo: {}", result.toString());
                return result;
            }
        }
        ExecResult result = execRevParse(repoDir, gitContext);
        log.info("#027.100 Result of execRevParse: {}", result.toString());
        if (!result.ok) {
            return result;
        }
        if (!result.systemExecResult.isOk) {
            return new ExecResult(null,false, result.systemExecResult.console);
        }
        if (!"true".equals(result.systemExecResult.console.strip())) {
            result = tryToRepairRepo(repoDir, git, gitContext);
            log.info("#027.110 Result of tryToRepairRepo: {}", result.toString());
            if (!result.ok) {
                return result;
            }
            if (!result.systemExecResult.isOk) {
                return new ExecResult(null,false, result.systemExecResult.console);
            }
        }

        result = execResetHardHead(repoDir, gitContext);
        log.info("#027.120 Result of execResetHardHead: {}", result.toString());
        if (!result.ok) {
            return result;
        }
        if (!result.systemExecResult.isOk) {
            return new ExecResult(null,false, result.systemExecResult.console);
        }

        result = execCleanDF(repoDir, gitContext);
        log.info("#027.130 Result of execCleanDF: {}", result.toString());
        if (!result.ok) {
            return result;
        }
        if (!result.systemExecResult.isOk) {
            return new ExecResult(null,false, result.systemExecResult.console);
        }

        result = execPullOrigin(repoDir, git, gitContext);
        log.info("#027.140 Result of execPullOrigin: {}", result.toString());
        if (!result.ok) {
            return result;
        }
        if (!result.systemExecResult.isOk) {
            return new ExecResult(null,false, result.systemExecResult.console);
        }

        result = execCheckoutRevision(repoDir, git, gitContext);
        log.info("#027.150 Result of execCheckoutRevision: {}", result.toString());
        if (!result.ok) {
            return result;
        }
        if (!result.systemExecResult.isOk) {
            return new ExecResult(null,false, result.systemExecResult.console);
        }
        log.info("#027.160 repoDir: {}, exist: {}", repoDir.getAbsolutePath(), repoDir.exists());

        return new ExecResult(repoDir, new ExecData.SystemExecResult(null, true, 0, "" ), true, null);
    }

    public static ExecResult tryToRepairRepo(File functionDir, KbData.KbGit git, GitContext gitContext) {
        File repoDir = new File(functionDir, Consts.REPO);
        ExecResult result;
        FileUtils.deleteQuietly(repoDir);
        if (repoDir.exists()) {
            return new ExecResult(null,
                    false,
                    "#027.170 can't prepare repo dir for function: " + repoDir.getAbsolutePath());
        }
        result = execClone(functionDir, git, gitContext);
        return result;
    }

    private static ExecResult execFileSystemCheck(File repoDir, Globals.Git git, GitContext gitContext) {
//git>git fsck --full
//Checking object directories: 100% (256/256), done.
//Checking objects: 100% (10432/10432), done.
//error: bad signature 0x00000000
//fatal: index file corrupt

        // git fsck --full
        ExecResult result = execCommonCmd(List.of("git", "-C", repoDir.getAbsolutePath(), "checkout", git.commit), gitContext.withTimeout(0L));
        return result;
    }

    private static ExecResult execCheckoutRevision(File repoDir, KbData.KbGit git, GitContext gitContext) {
        // git checkout sha1
        ExecResult result = execCommonCmd(List.of("git", "-C", repoDir.getAbsolutePath(), "checkout", git.getCommit()), gitContext.withTimeout(0L));
        return result;
    }

    private static ExecResult execPullOrigin(File repoDir, KbData.KbGit git, GitContext gitContext) {
        // pull origin master
        ExecResult result = execCommonCmd(List.of("git", "-C", repoDir.getAbsolutePath(), "pull", "origin", git.getBranch()), gitContext.withTimeout(0L));
        return result;
    }

    private static ExecResult execCleanDF(File repoDir, GitContext gitContext) {
        // git clean -df
        ExecResult result = execCommonCmd(List.of("git", "-C", repoDir.getAbsolutePath(), "clean", "-df"), gitContext.withTimeout(120L));
        return result;
    }

    private static ExecResult execRevParse(File repoDir, GitContext gitContext) {
        // git rev-parse --is-inside-work-tree
        ExecResult result = execCommonCmd(List.of("git", "-C", repoDir.getAbsolutePath(), "rev-parse", "--is-inside-work-tree"), gitContext.withTimeout(60L));
        return result;
    }

    // TODO 2019-05-11 add this before checkout for new changes
    private static ExecResult execResetHardHead(File repoDir, GitContext gitContext) {
        // git reset --hard HEAD
        ExecResult result = execCommonCmd(List.of("git", "-C", repoDir.getAbsolutePath(), "reset", "--hard", "HEAD"), gitContext.withTimeout(120L));
        return result;
    }

    private static ExecResult execCommonCmd(List<String> cmd, GitContext gitContext) {
        log.info("exec {}", cmd);
        return execGitCmd(cmd, gitContext);
    }

    private static ExecResult execClone(File repoDir, KbData.KbGit git, GitContext gitContext) {
        // git -C <path> clone <git-repo-url> repo
        String gitUrl = git.getRepo();
        List<String> cmd = List.of("git", "-C", repoDir.getAbsolutePath(), "clone", gitUrl, Consts.REPO);
        log.info("exec {}", cmd);
        ExecResult result = execGitCmd(cmd, gitContext.withTimeout(0L));
        return result;
    }

    private static String getGitVersion(String s) {
        if (s.startsWith(GIT_VERSION_PREFIX)) {
            return s.substring(GIT_VERSION_PREFIX.length()).strip();
        }
        if (s.startsWith(GIT_PREFIX)) {
            return s.substring(GIT_PREFIX.length()).strip();
        }
        return StringUtils.substring(s, 0, 100);
    }
}
