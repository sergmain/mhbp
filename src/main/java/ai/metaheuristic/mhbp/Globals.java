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

    public static class Threads {
        @Getter @Setter
        private int scheduler = 10;
        @Getter @Setter
        private int event =  10;
    }

    @Getter
    @Setter
    public static class RowsLimit {
        public int defaultLimit = 20;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Git {
        public String repo;
        public String branch;
        public String commit;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Kb {
        public Git[] gits;
    }

    public Threads threads;
    public RowsLimit rowsLimit;

    public final List<String> corsAllowedOrigins = new ArrayList<>(List.of("*"));

    public String mainUsername;
    public String mainPassword;
    public int consoleOutputMaxLines = 1000;

    public boolean sslRequired = true;
    public boolean testing = false;

    public Kb kb;

    public void setCorsAllowedOrigins(List<String> corsAllowedOrigins) {
        if (corsAllowedOrigins.isEmpty()) {
            return;
        }
        this.corsAllowedOrigins.clear();
        this.corsAllowedOrigins.addAll(corsAllowedOrigins);
    }
}
