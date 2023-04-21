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

package ai.metaheuristic.mhbp;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sergio Lissner
 * Date: 3/4/2023
 * Time: 3:29 PM
 */
@ConfigurationProperties("mhbp")
@Getter
@Setter
@Slf4j
@RequiredArgsConstructor
public class Globals {

    @Getter
    @Setter
    public static class Threads {
        public int scheduler = 10;
        public int event =  10;
        public int queryApi =  2;
    }

    @Getter
    @Setter
    public static class RowsLimit {
        public int defaultLimit = 20;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KbPath {
        public String evals;
        public String data;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Git {
        public String repo;
        public String branch;
        public String commit;
        public List<KbPath> kbPaths = new ArrayList<>();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class File {
        public String url;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Kb {
        public String code;
        public String type;
        public boolean disabled = false;
        public Git git;
        public File file;
    }

    public final Threads threads = new Threads();
    public RowsLimit rowsLimit = new RowsLimit();

    public final List<String> corsAllowedOrigins = new ArrayList<>(List.of("*"));

    public String mainUsername;
    public String mainPassword;
    public int consoleOutputMaxLines = 1000;

    public boolean sslRequired = true;
    public boolean testing = false;

    public Kb[] kb;

    public void setCorsAllowedOrigins(List<String> corsAllowedOrigins) {
        if (corsAllowedOrigins.isEmpty()) {
            return;
        }
        this.corsAllowedOrigins.clear();
        this.corsAllowedOrigins.addAll(corsAllowedOrigins);
    }
}
