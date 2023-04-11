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

package ai.metaheuristic.mhbp.provider;

import ai.metaheuristic.mhbp.Enums;
import ai.metaheuristic.mhbp.api.scheme.ApiScheme;
import ai.metaheuristic.mhbp.api.params.ApiParams;
import ai.metaheuristic.mhbp.beans.Api;
import ai.metaheuristic.mhbp.data.ApiData;
import ai.metaheuristic.mhbp.data.CommunicationData;
import ai.metaheuristic.mhbp.data.NluData;
import ai.metaheuristic.mhbp.repositories.ApiRepository;
import ai.metaheuristic.mhbp.utils.RestUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author Sergio Lissner
 * Date: 3/19/2023
 * Time: 9:04 PM
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProviderApiSchemeService {

    private final ApiRepository apiRepository;

    public final Map<String, List<ApiData.SchemeAndParams>> schemes = new HashMap<>();

    @PostConstruct
    public void init() {
        for (Api api : apiRepository.findAll()) {
            ApiScheme scheme = api.getApiScheme();
            ApiParams params = api.getApiParams();
            for (ApiScheme.MetaWithResponse meta : scheme.scheme.metas) {
                schemes.computeIfAbsent(meta.meta.object, o->new ArrayList<>()).add(new ApiData.SchemeAndParams(scheme, params));
            }
        }
    }

    public List<ApiData.SchemeAndParams> getProviderSchemeAndParams(NluData.QueriedInfo info) {
        return schemes.getOrDefault(info.object, List.of());
    }

    public List<ApiData.SchemeAndParamResult> queryProviders(NluData.QueriedInfo info) {
        List<ApiData.SchemeAndParams> schemeAndParamses = getProviderSchemeAndParams(info);
        List<ApiData.SchemeAndParamResult> result = new ArrayList<>();
        for (ApiData.SchemeAndParams schemeAndParams : schemeAndParamses) {
            String data = queryProviderApi(schemeAndParams, info);
            result.add(new ApiData.SchemeAndParamResult(schemeAndParams, data));
        }
        return result;
    }

    public static String queryProviderApi(ApiData.SchemeAndParams schemeAndParams, NluData.QueriedInfo info) {
        CommunicationData.Query query = buildApiQueryUri(schemeAndParams, info);

        String data = getData( schemeAndParams, query, (uri) -> Request.Get(uri).connectTimeout(5000).socketTimeout(20000));
        return data;
    }

    private static CommunicationData.Query buildApiQueryUri(ApiData.SchemeAndParams schemeAndParams, NluData.QueriedInfo info) {

        StringBuilder url = null;
        if (schemeAndParams.scheme.scheme.baseMeta.uri!=null) {
            url = new StringBuilder(schemeAndParams.scheme.scheme.baseMeta.uri);
        }
        if (url==null) {
            throw new RuntimeException("can't build an URL");
        }

        List<NameValuePair> nvps = new ArrayList<>();
        for (NluData.Property prop : info.properties) {
            if (schemeAndParams.scheme.scheme.baseMeta.attrs==null) {
                break;
            }
            ApiScheme.Meta attr = schemeAndParams.scheme.scheme.baseMeta.attrs.stream().filter(o->o.object.equals(prop.name)).findFirst().orElse(null);
            if (attr==null) {
                continue;
            }
            if (attr.param==null) {
                throw new RuntimeException("(attr.param==null)");
            }
            nvps.add(new BasicNameValuePair(attr.param, prop.value));
        }

        for (ApiScheme.MetaWithResponse meta : schemeAndParams.scheme.scheme.metas) {
            if (info.object.equals(meta.meta.object)) {
                url.append(meta.meta.uri);
            }
        }

        if (schemeAndParams.scheme.authType==Enums.AuthType.token) {
            if (schemeAndParams.scheme.scheme.tokenAuth==null) {
                throw new RuntimeException("(schemeAndParams.model.scheme.tokenAuth==null)");
            }
            if (schemeAndParams.params.api.tokenAuth==null) {
                throw new RuntimeException("(schemeAndParams.params.api.tokenAuth==null)");
            }
            nvps.add(new BasicNameValuePair(schemeAndParams.scheme.scheme.tokenAuth.tokenParam, schemeAndParams.params.api.tokenAuth.token));
        }

        return new CommunicationData.Query(url.toString(), nvps);
    }

    @SneakyThrows
    public static String getData(ApiData.SchemeAndParams schemeAndParams, CommunicationData.Query query, Function<URI, Request> requestFunc) {
        if (query.url==null) {
            throw new RuntimeException("url is null");
        }
        if (query.url.indexOf('?')!=-1) {
            throw new RuntimeException("params of query must be set via nvps");
        }

        final URIBuilder builder = new URIBuilder(query.url).setCharset(StandardCharsets.UTF_8);
        if (!query.nvps.isEmpty()) {
            builder.addParameters(query.nvps);
        }
        final URI build = builder.build();
        final Request request = requestFunc.apply(build);

        RestUtils.addHeaders(request);
        final Executor executor;
        if (schemeAndParams.scheme.authType==Enums.AuthType.basic) {
            if (schemeAndParams.params.api.basicAuth==null) {
                throw new IllegalStateException("(schemeAndParams.params.api.basicAuth==null)");
            }
            executor = getExecutor(schemeAndParams.scheme.scheme.baseMeta.uri, schemeAndParams.params.api.basicAuth.username, schemeAndParams.params.api.basicAuth.password);
        }
        else {
            executor = Executor.newInstance();
        }
        Response response = executor.execute(request);

        final HttpResponse httpResponse = response.returnResponse();
        final HttpEntity entity = httpResponse.getEntity();
        final int statusCode = httpResponse.getStatusLine().getStatusCode();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (entity != null) {
            entity.writeTo(baos);
        }
        final String data = baos.toString();
        if (statusCode !=200) {
            final String msg = "Server response:\n'" + data +"'";
            log.error(msg);
            throw new RuntimeException(msg);
        }
        return data;
    }

    @SuppressWarnings("ConstantValue")
    private static Executor getExecutor(String url, String username, String password) {
        HttpHost httpHost;
        try {
            httpHost = URIUtils.extractHost(new URL(url).toURI());
        } catch (Throwable th) {
            throw new IllegalArgumentException("Can't build HttpHost for " + url, th);
        }
        if (username == null || password == null) {
            throw new IllegalStateException("(username == null || password == null)");
        }
        return Executor.newInstance().authPreemptive(httpHost).auth(httpHost, username, password);
    }

}