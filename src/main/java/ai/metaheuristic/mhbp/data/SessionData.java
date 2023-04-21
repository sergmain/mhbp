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

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * @author Sergio Lissner
 * Date: 3/26/2023
 * Time: 3:00 AM
 */
public class SessionData {

    public record SessionStatus(
            long sessionId, long startedOn, @Nullable Long finishedOn, String sessionStatus,
            @Nullable String safe,
            float normalPercent, float failPercent, float errorPercent,
            String providerCode, String apiInfo, long evaluationId, String kbs) {
    }

    @RequiredArgsConstructor
    public static class SessionStatuses extends BaseDataClass {
        public final Slice<SessionStatus> sessions;
    }
}
