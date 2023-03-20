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

package ai.metaheuristic.mhbp.utils.versioning;

import ai.metaheuristic.mhbp.data.BaseParams;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.yaml.snakeyaml.Yaml;

/**
 * @author Sergio Lissner
 * Date: 3/5/2023
 * Time: 6:25 PM
 */
abstract public class AbstractParamsUtils
        <CurrT extends BaseParams, NextT extends BaseParams, NextU, PrevT, PrevU, CurrForDownT> {

    public abstract @NonNull Yaml getYaml();

    public abstract @NonNull NextT upgradeTo(@NonNull CurrT yaml);

    public abstract @NonNull PrevT downgradeTo(@NonNull CurrForDownT yaml);

    public abstract @Nullable NextU nextUtil();

    public abstract @Nullable PrevU prevUtil();

    public abstract String toString(CurrT yaml);

    public abstract @NonNull CurrT to(String s);

    public abstract int getVersion();
}
