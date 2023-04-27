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

package ai.metaheuristic.mhbp.kb;

import ai.metaheuristic.mhbp.Consts;
import ai.metaheuristic.mhbp.Enums;
import ai.metaheuristic.mhbp.Globals;
import ai.metaheuristic.mhbp.beans.Chapter;
import ai.metaheuristic.mhbp.beans.Kb;
import ai.metaheuristic.mhbp.chapter.ChapterTxService;
import ai.metaheuristic.mhbp.data.KbData;
import ai.metaheuristic.mhbp.data.OperationStatusRest;
import ai.metaheuristic.mhbp.data.RequestContext;
import ai.metaheuristic.mhbp.events.InitKbEvent;
import ai.metaheuristic.mhbp.kb.reader.openai.OpenaiJsonReader;
import ai.metaheuristic.mhbp.questions.QuestionData;
import ai.metaheuristic.mhbp.repositories.ChapterRepository;
import ai.metaheuristic.mhbp.repositories.KbRepository;
import ai.metaheuristic.mhbp.services.GitRepoService;
import ai.metaheuristic.mhbp.utils.ControllerUtils;
import ai.metaheuristic.mhbp.utils.JsonUtils;
import ai.metaheuristic.mhbp.utils.SystemProcessLauncher;
import ai.metaheuristic.mhbp.yaml.chapter.ChapterParams;
import ai.metaheuristic.mhbp.yaml.kb.KbParams;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ai.metaheuristic.mhbp.questions.QuestionData.*;

/**
 * @author Sergio Lissner
 * Date: 4/15/2023
 * Time: 2:34 PM
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class KbService {

    public final Globals globals;
    public final KbTxService kbTxService;
    public final KbRepository kbRepository;
    public final GitRepoService gitRepoService;
    public final ApplicationEventPublisher eventPublisher;
    public final ChapterTxService chapterTxService;
    public final ChapterRepository chapterRepository;

    @PostConstruct
    public void postConstruct() {
        List<Kb> kbs = kbTxService.findSystemKbs();
        for (Globals.Kb globalKb : globals.kb) {
            boolean create = true;
            for (Kb kb : kbs) {
                if (kb.code.equals(globalKb.code)) {
                    create = false;
                    if (kb.disabled!=globalKb.disabled) {
                        kbTxService.updateDisabled(kb.id, globalKb.disabled);
                    }
                    break;
                }
            }
            if (create) {
                KbParams kbParams = toKbParams(globalKb);
                kbTxService.saveSystemKb(kbParams);
            }
        }

        int i=0;
    }

    public static KbParams toKbParams(@NonNull Globals.Kb v1) {
        KbParams t = new KbParams();
        t.disabled = v1.disabled;
        t.kb.code = v1.code;
        t.kb.type = Enums.KbFileFormat.valueOf(v1.type);
        t.kb.git = toGit(v1.git);
        t.kb.file = toFile(v1.file);

        t.checkIntegrity();
        return t;
    }

    @Nullable
    private static KbParams.File toFile(@Nullable Globals.File v1) {
        if (v1==null) {
            return null;
        }
        KbParams.File f = new KbParams.File(v1.url);
        return f;
    }

    @Nullable
    private static KbParams.Git toGit(@Nullable Globals.Git v1) {
        if (v1==null) {
            return null;
        }
        KbParams.Git g = new KbParams.Git(v1.repo, v1.branch, v1.commit);
        v1.kbPaths.stream().map(KbService::toKbPath).collect(Collectors.toCollection(()->g.kbPaths));
        return g;
    }

    @Nullable
    public static KbParams.KbPath toKbPath(Globals.KbPath v1) {
        KbParams.KbPath ta = new KbParams.KbPath(v1.evals, v1.data);
        return ta;
    }

    public KbData.Kbs getKbs(Pageable pageable, RequestContext context) {
        pageable = ControllerUtils.fixPageSize(20, pageable);

        List<KbData.SimpleKb> simpleKbs = new ArrayList<>(50);
        Page<Kb> kbs = kbRepository.findAllByCompanyUniqueId(pageable, Consts.ID_1);
        kbs.stream().map(KbData.SimpleKb::new).collect(Collectors.toCollection(()->simpleKbs));

        kbs = kbRepository.findAllByCompanyUniqueId(pageable, context.getCompanyId());
        kbs.stream().map(KbData.SimpleKb::editableSimpleKb).collect(Collectors.toCollection(()->simpleKbs));

        var sorted = simpleKbs.stream().sorted((o1, o2)->Long.compare(o2.id, o1.id)).collect(Collectors.toList());
        return new KbData.Kbs(new PageImpl<>(sorted, pageable, simpleKbs.size()));
    }

    public List<Kb> getKbsAllowedForCompany(RequestContext context) {
        List<Kb> result = new ArrayList<>(50);
        List<Kb> kbs = kbRepository.findAllByCompanyUniqueId(Consts.ID_1);
        result.addAll(kbs);
        kbs = kbRepository.findAllByCompanyUniqueId(context.getCompanyId());
        result.addAll(kbs);
        return result;
    }

    public KbData.Kb getKb(@Nullable Long kbId, RequestContext context) {
        if (kbId==null) {
            return new KbData.Kb("Not found");
        }
        Kb kb = kbRepository.findById(kbId).orElse(null);
        if (kb == null) {
            return new KbData.Kb("Not found");
        }
        if (kb.companyId!=context.getCompanyId()) {
            return new KbData.Kb("Illegal access");
        }
        return new KbData.Kb(new KbData.SimpleKb(kb));
    }

    public OperationStatusRest initKb(Long kbId, RequestContext context) {
        if (kbId==null) {
            return OperationStatusRest.OPERATION_STATUS_OK;
        }
        Kb kb = kbRepository.findById(kbId).orElse(null);
        if (kb == null) {
            return new OperationStatusRest(Enums.OperationStatus.ERROR,
                    "#565.250 KB wasn't found, kbId: " + kbId, null);
        }
        if (kb.companyId!=context.getCompanyId()) {
            return new OperationStatusRest(Enums.OperationStatus.ERROR, "#565.500 Access denied, kbId: " + kbId);
        }
        eventPublisher.publishEvent(new InitKbEvent(kbId));
        return OperationStatusRest.OPERATION_STATUS_OK;
    }

    @SneakyThrows
    public void processInitKbEvent(InitKbEvent event) {
        Kb kb = kbRepository.findById(event.kbId()).orElse(null);
//        if (kb == null || kb.status==Enums.KbStatus.ready.code) {
        if (kb == null) {
            return;
        }
        KbParams kbParams = kb.getKbParams();
        if (kbParams.kb.git!=null) {
            final SystemProcessLauncher.ExecResult execResult = gitRepoService.initGitRepo(kbParams.kb.git);
            if (execResult.ok) {
                if (execResult.repoDir==null) {
                    throw new IllegalStateException();
                }
                final String asString = JsonUtils.getMapper().writeValueAsString(execResult);
                loadPromptFromGit(kb, execResult.repoDir, kbParams.kb.git);
            }
        }
        if (kbParams.kb.file!=null) {

        }

        kbTxService.markAsReady(event.kbId());

    }

    public void loadPromptFromGit(Kb kb, File repoDir, KbParams.Git git) {
        Chapters chapters = OpenaiJsonReader.read(kb.id, repoDir.toPath(), git);
        log.info("Status of loading kb: " + chapters.initStatus);
        if (chapters.initStatus!=Enums.KbSourceInitStatus.ready) {
            return;
        }

        for (ChapterWithPrompts chapter : chapters.chapters) {
            ChapterParams params = new ChapterParams();
            chapter.list().stream().map(QuestionWithAnswerToAsk::toPrompt).collect(Collectors.toCollection(()->params.prompts));

            Chapter c = chapterRepository.findByKbIdAndCode();

        }


    }


}
