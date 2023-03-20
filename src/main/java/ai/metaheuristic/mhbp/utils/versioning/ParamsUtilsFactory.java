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

import lombok.Data;
import org.springframework.lang.NonNull;

import java.util.Map;

/**
 * @author Sergio Lissner
 * Date: 3/5/2023
 * Time: 6:33 PM
 */
@Data
public class ParamsUtilsFactory {

    public @NonNull Map<Integer, AbstractParamsUtils> map;
    public @NonNull AbstractParamsUtils defYamlUtils;

    public void ParamsYamlUtilsFactory(@NonNull Map<Integer, AbstractParamsUtils> map, @NonNull AbstractParamsUtils defYamlUtils) {
        this.map = map;
        this.defYamlUtils = defYamlUtils;
    }

    public @NonNull AbstractParamsUtils getForVersion(int version) {
        AbstractParamsUtils yamlUtils = map.get(version);
        if (yamlUtils==null) {
            throw new IllegalArgumentException("Not supported version: " + version);
        }
        return yamlUtils;
    }


    public @NonNull AbstractParamsUtils getDefault() {
        return defYamlUtils;
    }
}
