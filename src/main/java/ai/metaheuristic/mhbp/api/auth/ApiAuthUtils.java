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

package ai.metaheuristic.mhbp.api.auth;

import ai.metaheuristic.mhbp.utils.versioning.BaseUtils;

import java.util.Map;

public class ApiAuthUtils {

    private static final ApiAuthUtilsV1 YAML_UTILS_V_1 = new ApiAuthUtilsV1();
    private static final ApiAuthUtilsV1 DEFAULT_UTILS = YAML_UTILS_V_1;

    public static final BaseUtils<ApiAuth> UTILS = new BaseUtils<>(
            Map.of(
                    1, YAML_UTILS_V_1
            ),
            DEFAULT_UTILS
    );
}
