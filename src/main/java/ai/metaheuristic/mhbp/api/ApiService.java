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

package ai.metaheuristic.mhbp.api;

import ai.metaheuristic.mhbp.Enums;
import ai.metaheuristic.mhbp.beans.Api;
import ai.metaheuristic.mhbp.data.ApiData;
import ai.metaheuristic.mhbp.data.OperationStatusRest;
import ai.metaheuristic.mhbp.data.RequestContext;
import ai.metaheuristic.mhbp.events.EvaluateProviderEvent;
import ai.metaheuristic.mhbp.repositories.ApiRepository;
import ai.metaheuristic.mhbp.utils.ControllerUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Sergio Lissner
 * Date: 4/11/2023
 * Time: 12:39 AM
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ApiService {

    private final ApiRepository apiRepository;
    private final ApplicationEventPublisher eventPublisher;

    public ApiData.Apis getApis(Pageable pageable, RequestContext context) {
        pageable = ControllerUtils.fixPageSize(20, pageable);

        Page<Api> apis = apiRepository.findAllByCompanyUniqueId(pageable, context.getCompanyId());
        List<ApiData.SimpleApi> list = apis.stream().map(ApiData.SimpleApi::new).toList();
        var sorted = list.stream().sorted((o1, o2)->Long.compare(o2.id, o1.id)).collect(Collectors.toList());
        return new ApiData.Apis(new PageImpl<>(sorted, pageable, list.size()));
    }

    public List<Api> getApisAllowedForCompany(RequestContext context) {
        List<Api> apis = apiRepository.findAllByCompanyUniqueId(context.getCompanyId());
        return apis;
    }

    public OperationStatusRest evaluate(@Nullable Long apiId, RequestContext context, int limit) {
        if (apiId==null) {
            return OperationStatusRest.OPERATION_STATUS_OK;
        }
        Api api = apiRepository.findById(apiId).orElse(null);
        if (api == null) {
            return new OperationStatusRest(Enums.OperationStatus.ERROR,
                    "#565.150 API wasn't found, apiId: " + apiId, null);
        }
        if (api.companyId!=context.getCompanyId()) {
            return new OperationStatusRest(Enums.OperationStatus.ERROR, "#565.200 apiId: " + apiId);
        }
        eventPublisher.publishEvent(new EvaluateProviderEvent(apiId, limit));
        return OperationStatusRest.OPERATION_STATUS_OK;
    }

    @Transactional
    public OperationStatusRest deleteApiById(Long apiId, RequestContext context) {
        if (apiId==null) {
            return OperationStatusRest.OPERATION_STATUS_OK;
        }
        Api api = apiRepository.findById(apiId).orElse(null);
        if (api == null) {
            return new OperationStatusRest(Enums.OperationStatus.ERROR,
                    "#565.250 API wasn't found, apiId: " + apiId, null);
        }
        if (api.companyId!=context.getCompanyId()) {
            return new OperationStatusRest(Enums.OperationStatus.ERROR, "#565.500 apiId: " + apiId);
        }

        apiRepository.deleteById(apiId);
        return OperationStatusRest.OPERATION_STATUS_OK;
    }

    @Transactional
    public OperationStatusRest createApi(String name, String code, String scheme, RequestContext context) {
        Api api = new Api();
        api.name = name;
        api.code = code;
        api.setScheme(scheme);
        api.companyId = context.getCompanyId();
        api.accountId = context.getAccountId();

        apiRepository.save(api);

        return OperationStatusRest.OPERATION_STATUS_OK;
    }

    public ApiData.Api getApi(Long apiId, RequestContext context) {
        if (apiId==null) {
            return new ApiData.Api("Not found");
        }
        Api api = apiRepository.findById(apiId).orElse(null);
        if (api == null) {
            return new ApiData.Api("Not found");
        }
        return new ApiData.Api(new ApiData.SimpleApi(api));
    }
}
