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

package ai.metaheuristic.mhbp.data;

import ai.metaheuristic.mhbp.Enums;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;

import java.util.Collections;

/**
 * @author Sergio Lissner
 * Date: 4/16/2023
 * Time: 12:29 AM
 */
public class KbData {

    public interface KbGit {
        String getRepo();
        String getBranch();
        String getCommit();
    }

    public static class SimpleKb {
        public long id;
        public String code;
        public String params;
        public boolean editable;
        public String status;

        public SimpleKb(ai.metaheuristic.mhbp.beans.Kb kb) {
            this.id = kb.id;
            this.code = kb.code;
            this.status = Enums.KbStatus.to(kb.status).toString();
            this.params = kb.getParams();
        }

        public static SimpleKb editableSimpleKb(ai.metaheuristic.mhbp.beans.Kb kb) {
            SimpleKb simpleKb = new SimpleKb(kb);
            simpleKb.editable = true;
            return simpleKb;
        }
    }

    @RequiredArgsConstructor
    public static class Kbs extends BaseDataClass {
        public final Slice<SimpleKb> kbs;
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    @NoArgsConstructor
    public static class Kb extends BaseDataClass {
        public SimpleKb kb;

        public Kb(String errorMessage) {
            this.errorMessages = Collections.singletonList(errorMessage);
        }

        public Kb(SimpleKb kb, String errorMessage) {
            this.kb = kb;
            this.errorMessages = Collections.singletonList(errorMessage);
        }

        public Kb(SimpleKb kb) {
            this.kb = kb;
        }
    }

}
