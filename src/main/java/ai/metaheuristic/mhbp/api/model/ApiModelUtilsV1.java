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

package ai.metaheuristic.mhbp.api.model;

import ai.metaheuristic.mhbp.utils.YamlUtils;
import ai.metaheuristic.mhbp.utils.versioning.AbstractParamsUtils;
import org.springframework.lang.NonNull;
import org.yaml.snakeyaml.Yaml;

import java.util.stream.Collectors;

@SuppressWarnings("ConstantValue")
public class ApiModelUtilsV1 extends
        AbstractParamsUtils<ApiModelV1, ApiModel, Void, Void, Void, Void> {

    @Override
    public int getVersion() {
        return 1;
    }

    @NonNull
    @Override
    public Yaml getYaml() {
        return YamlUtils.init(ApiModelV1.class);
    }

    @NonNull
    @Override
    public ApiModel upgradeTo(@NonNull ApiModelV1 v1) {
        v1.checkIntegrity();

        ApiModel t = new ApiModel();
        t.code = v1.code;
        t.authType = v1.authType;
        if (v1.model!=null) {
            t.model = new ApiModel.Model();
            t.model.apiDocuUrl = v1.model.apiDocuUrl;
            t.model.baseMeta = toMeta(v1.model.baseMeta);

            if (v1.model.basicAuth != null) {
                t.model.basicAuth = new ApiModel.BasicAuth(v1.model.basicAuth.usernameParam, v1.model.basicAuth.passwordParam);
            }

            if (v1.model.tokenAuth != null) {
                t.model.tokenAuth = new ApiModel.TokenAuth(v1.model.tokenAuth.tokenParam);
            }
            v1.model.metas.stream().map(ApiModelUtilsV1::toMetaWithResponse).collect(Collectors.toCollection(()->t.model.metas));
        }
        t.checkIntegrity();
        return t;
    }

    private static ApiModel.Meta toMeta(ApiModelV1.MetaV1 v1) {
        if (v1==null) {
            throw new IllegalStateException("(v1==null)");
        }
        ApiModel.Meta m = new ApiModel.Meta();
        m.object = v1.object;
        m.desc = v1.desc;
        m.uri = v1.uri;
        m.param = v1.param;
        if (v1.attrs!=null) {
            m.attrs = v1.attrs.stream().map(ApiModelUtilsV1::toMeta).collect(Collectors.toList());
        }
        return m;
    }

    private static ApiModel.Response toResponse(ApiModelV1.ResponseV1 v1) {
        ApiModel.Response r = new ApiModel.Response();
        r.asText = v1.asText;
        r.attrs = v1.attrs.stream().map(ApiModelUtilsV1::toMeta).collect(Collectors.toList());
        return r;
    }

    private static ApiModel.MetaWithResponse toMetaWithResponse(ApiModelV1.MetaWithResponseV1 v1) {
        ApiModel.MetaWithResponse r = new ApiModel.MetaWithResponse();
        r.meta = toMeta(v1.meta);
        r.response = toResponse(v1.response);
        return r;
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
    public String toString(@NonNull ApiModelV1 yaml) {
        yaml.checkIntegrity();

        return getYaml().dump(yaml);
    }

    @NonNull
    @Override
    public ApiModelV1 to(@NonNull String s) {
        final ApiModelV1 p = getYaml().load(s);
        return p;
    }

}
