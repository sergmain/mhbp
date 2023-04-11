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
import ai.metaheuristic.mhbp.beans.Session;
import ai.metaheuristic.mhbp.data.*;
import ai.metaheuristic.mhbp.repositories.ApiRepository;
import ai.metaheuristic.mhbp.utils.ControllerUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

    public final ApiRepository apiRepository;

    public ApiData.Apis getApis(Pageable pageable, RequestContext context) {
        pageable = ControllerUtils.fixPageSize(20, pageable);

        Page<Api> apis = apiRepository.findAllByCompanyUniqueId(pageable, context.getCompanyId());
        List<ApiData.Api> list = apis.stream().map(ApiData.Api::new).toList();
        var sorted = list.stream().sorted((o1, o2)->Long.compare(o2.id, o1.id)).collect(Collectors.toList());
        return new ApiData.Apis(new PageImpl<>(sorted, pageable, list.size()));
    }

    @Transactional
    public OperationStatusRest deleteApiById(Long id, RequestContext context) {
        if (id==null) {
            return OperationStatusRest.OPERATION_STATUS_OK;
        }
        Api api = apiRepository.findById(id).orElse(null);
        if (api == null) {
            return new OperationStatusRest(Enums.OperationStatus.OK,
                    "#565.250 API wasn't found, id: " + id, null);
        }
        apiRepository.deleteById(id);
        return OperationStatusRest.OPERATION_STATUS_OK;
    }


}