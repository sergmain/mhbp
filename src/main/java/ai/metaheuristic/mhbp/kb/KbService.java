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

import ai.metaheuristic.mhbp.Globals;
import ai.metaheuristic.mhbp.beans.Kb;
import ai.metaheuristic.mhbp.yaml.kb.KbParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

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

    @PostConstruct
    public void inti() {

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
        t.kb.type = v1.type;
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
}
