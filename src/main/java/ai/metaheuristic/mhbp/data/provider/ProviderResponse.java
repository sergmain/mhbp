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

package ai.metaheuristic.mhbp.data.provider;

import ai.metaheuristic.mhbp.Enums;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;

/**
 * @author Sergio Lissner
 * Date: 3/5/2023
 * Time: 1:24 AM
 */
public class ProviderResponse {
    public String providerCode;
    public long requestId;
    public long sessionId;
    public String text;
    public LocalDateTime dateTime;
    public boolean safe;
    @Nullable
    public String modelId;

    public Enums.ResponseType type;

    @Nullable
    // will be inited in async manner
    public Long questionId;
}
