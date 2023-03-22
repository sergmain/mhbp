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

package ai.metaheuristic.mhbp.api.params;

import ai.metaheuristic.mhbp.utils.YamlUtils;
import ai.metaheuristic.mhbp.utils.versioning.AbstractParamsUtils;
import org.springframework.lang.NonNull;
import org.yaml.snakeyaml.Yaml;

public class ApiParamsUtilsV1 extends
        AbstractParamsUtils<ApiParamsV1, ApiParams, Void, Void, Void, Void> {

    @Override
    public int getVersion() {
        return 1;
    }

    @NonNull
    @Override
    public Yaml getYaml() {
        return YamlUtils.init(ApiParamsV1.class);
    }

    @NonNull
    @Override
    public ApiParams upgradeTo(@NonNull ApiParamsV1 v1) {
        v1.checkIntegrity();

        ApiParams t = new ApiParams();
        t.api.code = v1.api.code;
        t.api.authType = v1.api.authType;
        if (v1.api.basicAuth!=null) {
            t.api.basicAuth = new ApiParams.BasicAuth(v1.api.basicAuth.username, v1.api.basicAuth.password);
        }

        if (v1.api.tokenAuth!=null) {
            t.api.tokenAuth = new ApiParams.TokenAuth(v1.api.tokenAuth.token);
        }

        t.checkIntegrity();
        return t;
    }

    @NonNull
    @Override
    public Void downgradeTo(@NonNull Void yaml) {
        return null;
    }

    @Override
    public Void nextUtil() {
        return null;
    }

    @Override
    public Void prevUtil() {
        return null;
    }

    @Override
    public String toString(@NonNull ApiParamsV1 yaml) {
        yaml.checkIntegrity();

        return getYaml().dump(yaml);
    }

    @NonNull
    @Override
    public ApiParamsV1 to(@NonNull String s) {
        final ApiParamsV1 p = getYaml().load(s);
        return p;
    }

}
