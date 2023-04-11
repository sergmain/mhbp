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

package ai.metaheuristic.mhbp.api.scheme;

import ai.metaheuristic.mhbp.utils.YamlUtils;
import ai.metaheuristic.mhbp.utils.versioning.AbstractParamsUtils;
import org.springframework.lang.NonNull;
import org.yaml.snakeyaml.Yaml;

import java.util.stream.Collectors;

@SuppressWarnings("ConstantValue")
public class ApiSchemeUtilsV1 extends
        AbstractParamsUtils<ApiSchemeV1, ApiScheme, Void, Void, Void, Void> {

    @Override
    public int getVersion() {
        return 1;
    }

    @NonNull
    @Override
    public Yaml getYaml() {
        return YamlUtils.init(ApiSchemeV1.class);
    }

    @NonNull
    @Override
    public ApiScheme upgradeTo(@NonNull ApiSchemeV1 v1) {
        v1.checkIntegrity();

        ApiScheme t = new ApiScheme();
        t.code = v1.code;
        t.authType = v1.authType;
        if (v1.scheme!=null) {
            t.scheme = new ApiScheme.Scheme();
            t.scheme.apiDocuUrl = v1.scheme.apiDocuUrl;
            t.scheme.baseMeta = toMeta(v1.scheme.baseMeta);

            if (v1.scheme.basicAuth != null) {
                t.scheme.basicAuth = new ApiScheme.BasicAuth(v1.scheme.basicAuth.usernameParam, v1.scheme.basicAuth.passwordParam);
            }

            if (v1.scheme.tokenAuth != null) {
                t.scheme.tokenAuth = new ApiScheme.TokenAuth(v1.scheme.tokenAuth.tokenParam);
            }
            v1.scheme.metas.stream().map(ApiSchemeUtilsV1::toMetaWithResponse).collect(Collectors.toCollection(()->t.scheme.metas));
        }
        t.checkIntegrity();
        return t;
    }

    private static ApiScheme.Meta toMeta(ApiSchemeV1.MetaV1 v1) {
        if (v1==null) {
            throw new IllegalStateException("(v1==null)");
        }
        ApiScheme.Meta m = new ApiScheme.Meta();
        m.object = v1.object;
        m.desc = v1.desc;
        m.uri = v1.uri;
        m.param = v1.param;
        if (v1.attrs!=null) {
            m.attrs = v1.attrs.stream().map(ApiSchemeUtilsV1::toMeta).collect(Collectors.toList());
        }
        return m;
    }

    private static ApiScheme.Response toResponse(ApiSchemeV1.ResponseV1 v1) {
        ApiScheme.Response r = new ApiScheme.Response();
        r.asText = v1.asText;
        r.attrs = v1.attrs.stream().map(ApiSchemeUtilsV1::toMeta).collect(Collectors.toList());
        return r;
    }

    private static ApiScheme.MetaWithResponse toMetaWithResponse(ApiSchemeV1.MetaWithResponseV1 v1) {
        ApiScheme.MetaWithResponse r = new ApiScheme.MetaWithResponse();
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
    public String toString(@NonNull ApiSchemeV1 yaml) {
        yaml.checkIntegrity();

        return getYaml().dump(yaml);
    }

    @NonNull
    @Override
    public ApiSchemeV1 to(@NonNull String s) {
        final ApiSchemeV1 p = getYaml().load(s);
        return p;
    }

}
