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
import ai.metaheuristic.mhbp.beans.Kb;
import ai.metaheuristic.mhbp.data.RequestContext;
import ai.metaheuristic.mhbp.data.SimpleChapterUid;
import ai.metaheuristic.mhbp.repositories.ChapterRepository;
import ai.metaheuristic.mhbp.repositories.KbRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sergio Lissner
 * Date: 4/28/2023
 * Time: 12:27 PM
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ChapterService {

    public final KbRepository kbRepository;
    public final ChapterRepository chapterRepository;

    public List<Kb> getKbsAllowedForCompany(RequestContext context) {
        List<Kb> result = new ArrayList<>(50);
        List<Kb> kbs = kbRepository.findAllByCompanyUniqueId(Consts.ID_1);
        result.addAll(kbs);
        kbs = kbRepository.findAllByCompanyUniqueId(context.getCompanyId());
        result.addAll(kbs);
        return result;
    }

    public List<Long> getKbIdsAllowedForCompany(RequestContext context) {
        List<Long> result = new ArrayList<>(50);
        List<Long> kbs = kbRepository.findAllIdsByCompanyUniqueId(Consts.ID_1);
        result.addAll(kbs);
        kbs = kbRepository.findAllIdsByCompanyUniqueId(context.getCompanyId());
        result.addAll(kbs);
        return result;
    }

    public List<SimpleChapterUid> getChaptersAllowedForCompany(RequestContext context) {
        List<Long> kbIds = getKbIdsAllowedForCompany(context);
        List<SimpleChapterUid> result = chapterRepository.findAllByKbIds(kbIds);
        return result;
    }

}
