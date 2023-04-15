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
import ai.metaheuristic.mhbp.beans.Kb;
import ai.metaheuristic.mhbp.repositories.KbRepository;
import ai.metaheuristic.mhbp.yaml.kb.KbParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.transaction.annotation.Propagation.*;

/**
 * @author Sergio Lissner
 * Date: 4/15/2023
 * Time: 2:34 PM
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class KbTxService {

    public final KbRepository kbRepository;

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
}
