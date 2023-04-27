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

package ai.metaheuristic.mhbp.chapter;

import ai.metaheuristic.mhbp.Consts;
import ai.metaheuristic.mhbp.beans.Chapter;
import ai.metaheuristic.mhbp.repositories.ChapterRepository;
import ai.metaheuristic.mhbp.yaml.chapter.ChapterParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Sergio Lissner
 * Date: 4/27/2023
 * Time: 3:08 AM
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ChapterTxService {

    public final ChapterRepository chapterRepository;

    @Transactional
    public void saveSystemChapter(String kbCode, ChapterParams chapterParams) {
        Chapter chapter = new Chapter();
        chapter.code = kbCode;
        chapter.createdOn = System.currentTimeMillis();
        chapter.disabled = false;
        chapter.companyId = Consts.ID_1;
        // for companyId==1L it doesn't matter which accountId will be
        chapter.accountId = 0;
        chapter.updateParams(chapterParams);

        chapterRepository.save(chapter);
    }


}
