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
import ai.metaheuristic.mhbp.data.OperationStatusRest;
import ai.metaheuristic.mhbp.data.RequestContext;
import ai.metaheuristic.mhbp.questions.QuestionData;
import ai.metaheuristic.mhbp.repositories.ChapterRepository;
import ai.metaheuristic.mhbp.repositories.KbRepository;
import ai.metaheuristic.mhbp.yaml.chapter.ChapterParams;
import ai.metaheuristic.mhbp.yaml.kb.KbParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.transaction.annotation.Propagation.SUPPORTS;

/**
 * @author Sergio Lissner
 * Date: 4/15/2023
 * Time: 2:34 PM
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class KbTxService {

    public final Globals globals;
    public final KbRepository kbRepository;
    private final ChapterRepository chapterRepository;

/*
    private Path gitPath;

    @PostConstruct
    public void postConstruct() {
        gitPath = globals.getHome().resolve("git");
    }
*/

    @Transactional(propagation= SUPPORTS)
    public List<Kb> findSystemKbs() {
        return kbRepository.findAllByCompanyUniqueId(Consts.ID_1);
    }

    @Transactional
    public void saveSystemKb(KbParams kbParams) {
        Kb kb = new Kb();
        kb.code = kbParams.kb.code;
        kb.createdOn = System.currentTimeMillis();
        kb.disabled = kbParams.disabled;
        kb.companyId = Consts.ID_1;
        // for companyId==1L it doesn't matter which accountId will be
        kb.accountId = 0;
        kb.updateParams(kbParams);

        kbRepository.save(kb);
    }

    @Transactional
    public void updateDisabled(Long kbId, boolean disabled) {
        Kb kb = kbRepository.findById(kbId).orElse(null);
        if (kb==null) {
            return;
        }
        kb.disabled = disabled;
        kbRepository.save(kb);
    }

    @Transactional
    public OperationStatusRest deleteKbById(Long kbId, RequestContext context) {
        if (kbId==null) {
            return OperationStatusRest.OPERATION_STATUS_OK;
        }
        Kb kb = kbRepository.findById(kbId).orElse(null);
        if (kb == null) {
            return new OperationStatusRest(Enums.OperationStatus.ERROR,
                    "209.040 KB wasn't found, kbId: " + kbId, null);
        }
        if (kb.companyId!=context.getCompanyId()) {
            return new OperationStatusRest(Enums.OperationStatus.ERROR, "209.080 kbId: " + kbId);
        }

        kbRepository.delete(kb);
        return OperationStatusRest.OPERATION_STATUS_OK;
    }

    @Transactional
    public OperationStatusRest createKb(String code, String params, RequestContext context) {
        Kb kb = new Kb();
        kb.code = code;
        kb.status = Enums.KbStatus.none.code;
        kb.setParams(params);
        kb.companyId = context.getCompanyId();
        kb.accountId = context.getAccountId();
        kb.createdOn = System.currentTimeMillis();

        kbRepository.save(kb);

        return OperationStatusRest.OPERATION_STATUS_OK;
    }

    @Transactional
    public void markAsReady(long kbId) {
        Kb kb = kbRepository.findById(kbId).orElse(null);
        if (kb == null) {
            return;
        }
        if (kb.status==Enums.KbStatus.ready.code) {
            return;
        }
        kb.status = Enums.KbStatus.ready.code;
        kbRepository.save(kb);
    }

    @Transactional
    public void storePrompts(QuestionData.ChapterWithPrompts chapter, ChapterParams params, long kbId, long companyId, long accountId) {
        Chapter c = chapterRepository.findByKbIdAndCode(kbId, chapter.chapterCode());
        if (c==null) {
            c = new Chapter();
            c.kbId = kbId;
            c.code = chapter.chapterCode();
            c.companyId = companyId;
            c.accountId = accountId;
            c.createdOn = System.currentTimeMillis();
            c.disabled = false;
            // for what is this status?
            c.status = 0;
        }
        c.promptCount = params.prompts.size();
        c.updateParams(params);

        chapterRepository.save(c);
    }

}
