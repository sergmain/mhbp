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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Sergio Lissner
 * Date: 4/14/2023
 * Time: 5:32 PM
 */
public class ExecData {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString(exclude = {"console"})
    public static class SystemExecResult {
        public String functionCode;
        public boolean isOk;
        public int exitCode;
        public String console;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GitInfo {
        public String repo;
        // right now it'll be always as origin
//        public String remote;
        public String branch;
        public String commit;
    }
}
