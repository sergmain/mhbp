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
import ai.metaheuristic.mhbp.api.auth.ApiAuth;
import ai.metaheuristic.mhbp.api.scheme.ApiScheme;
import ai.metaheuristic.mhbp.beans.Api;
import ai.metaheuristic.mhbp.beans.Auth;
import ai.metaheuristic.mhbp.data.ApiData;
import ai.metaheuristic.mhbp.data.CommunicationData;
import ai.metaheuristic.mhbp.data.NluData;
import ai.metaheuristic.mhbp.repositories.AuthRepository;
import ai.metaheuristic.mhbp.utils.RestUtils;
import ai.metaheuristic.mhbp.utils.S;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static ai.metaheuristic.mhbp.Enums.OperationStatus.OK;

/**
 * @author Sergio Lissner
 * Date: 3/19/2023
 * Time: 9:04 PM
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProviderApiSchemeService {

    private final AuthRepository authRepository;

    public ApiData.SchemeAndParamResult queryProviders(Api api, NluData.QueriedPrompt info) {
        ApiScheme scheme = api.getApiScheme();
        List<Auth> auths = authRepository.findAllByCompanyUniqueId(api.companyId);
        Auth auth = auths.stream().filter(o->o.getAuthParams().auth.code.equals(scheme.scheme.auth.code)).findFirst().orElse(null);
        if (auth==null) {
            throw new RuntimeException("Auth wasn't found for code " + scheme.scheme.auth.code);
        }
        ApiData.SchemeAndParams schemeAndParams = new ApiData.SchemeAndParams(scheme, auth.getAuthParams());

        ApiData.SchemeAndParamResult result = queryProviderApi(schemeAndParams, info);
        return result;
    }

    private static CommunicationData.Query buildApiQueryUri(ApiData.SchemeAndParams schemeAndParams, NluData.QueriedPrompt info) {

        if (S.b(schemeAndParams.scheme.scheme.request.uri)) {
            throw new RuntimeException("can't build an URL");
        }
        List<NameValuePair> nvps = new ArrayList<>();
        if (schemeAndParams.scheme.scheme.request.prompt.place==Enums.PromptPlace.uri) {
            nvps.add(new BasicNameValuePair(schemeAndParams.scheme.scheme.request.prompt.param, info.text));
        }

        if (schemeAndParams.auth.auth.type==Enums.AuthType.token) {
            if (schemeAndParams.auth.auth.token==null) {
                throw new RuntimeException("(schemeAndParams.auth.auth.tokenAuth==null)");
            }
            if (schemeAndParams.auth.auth.token.place!=Enums.TokenPlace.header) {
                nvps.add(new BasicNameValuePair(schemeAndParams.auth.auth.token.param, schemeAndParams.auth.auth.token.token));
            }
        }

        return new CommunicationData.Query(schemeAndParams.scheme.scheme.request.uri, nvps);
    }

    @SneakyThrows
    public static ApiData.SchemeAndParamResult queryProviderApi(ApiData.SchemeAndParams schemeAndParams, NluData.QueriedPrompt info) {
        CommunicationData.Query query = buildApiQueryUri(schemeAndParams, info);

        if (query.url==null) {
            return new ApiData.SchemeAndParamResult(schemeAndParams, "url is null");
        }
        if (query.url.indexOf('?')!=-1) {
            return new ApiData.SchemeAndParamResult(schemeAndParams, "params of query must be set via nvps");
        }

        final URIBuilder uriBuilder1 = new URIBuilder(query.url).setCharset(StandardCharsets.UTF_8);
        if (!query.nvps.isEmpty()) {
            uriBuilder1.addParameters(query.nvps);
        }
        final URI uri = uriBuilder1.build();

        final Request request;
        if (schemeAndParams.scheme.scheme.request.type==Enums.HttpMethodType.post) {
            request = Request.Post(uri).connectTimeout(5000).socketTimeout(20000);
            if (schemeAndParams.scheme.scheme.request.prompt.place==Enums.PromptPlace.text) {
                String json = schemeAndParams.scheme.scheme.request.prompt.text.replace(schemeAndParams.scheme.scheme.request.prompt.replace, info.text);
                StringEntity entity = new StringEntity(json, StandardCharsets.UTF_8);
                request.body(entity);
            }
        }
        else if (schemeAndParams.scheme.scheme.request.type==Enums.HttpMethodType.get) {
            request = Request.Get(uri).connectTimeout(5000).socketTimeout(20000);
        }
        else {
            return new ApiData.SchemeAndParamResult(schemeAndParams, "unknown type of request: " + schemeAndParams.scheme.scheme.request.type);
        }

        ApiData.SchemeAndParamResult result = getData( schemeAndParams, request);
        return result;
    }

    @SneakyThrows
    public static ApiData.SchemeAndParamResult getData(ApiData.SchemeAndParams schemeAndParams, final Request request) {

        RestUtils.addHeaders(request);
        final Executor executor;
        if (schemeAndParams.auth.auth.type==Enums.AuthType.basic) {
            if (schemeAndParams.auth.auth.basic==null) {
                return new ApiData.SchemeAndParamResult(schemeAndParams, "(schemeAndParams.params.api.basicAuth==null)");
            }
            executor = getExecutor(schemeAndParams.scheme.scheme.request.uri, schemeAndParams.auth.auth.basic.username, schemeAndParams.auth.auth.basic.password);
        }
        else {
            if (schemeAndParams.auth.auth.token==null) {
                return new ApiData.SchemeAndParamResult(schemeAndParams, "(schemeAndParams.auth.auth.token==null)");
            }
            if (schemeAndParams.auth.auth.token.place==Enums.TokenPlace.header) {
                String token = getActualToken(schemeAndParams.auth.auth.token);
                request.addHeader("Authorization", "Bearer " + token);
            }
            executor = Executor.newInstance();
        }
        request.addHeader("Content-Type", "application/json");

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
            return new ApiData.SchemeAndParamResult(schemeAndParams, msg);
        }
        return new ApiData.SchemeAndParamResult(schemeAndParams, data, OK, data, null);
    }

    public static String getActualToken(ApiAuth.TokenAuth tokenAuth) {
        if (tokenAuth.token!=null) {
            return tokenAuth.token;
        }
        String evnParam = getEnvParamName(tokenAuth.env);
        final String value = System.getenv(evnParam);
        if (S.b(value)) {
            throw new RuntimeException("(S.b(value)) , evn param: " + evnParam);
        }
        return value;
    }

    public static String getEnvParamName(String env) {
        if (S.b(env)) {
            throw new IllegalStateException("(S.b(env))");
        }
        int start = 0;
        int end = 0;
        if (StringUtils.startsWithAny(env, "%", "$")) {
            ++start;
        }
        if (StringUtils.endsWithAny(env, "%", "$")) {
            ++end;
        }
        return env.substring(start, env.length()-end);
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
