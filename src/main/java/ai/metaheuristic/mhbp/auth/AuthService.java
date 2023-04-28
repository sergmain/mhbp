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

package ai.metaheuristic.mhbp.auth;

import ai.metaheuristic.mhbp.Enums;
import ai.metaheuristic.mhbp.beans.Auth;
import ai.metaheuristic.mhbp.data.AuthData;
import ai.metaheuristic.mhbp.data.OperationStatusRest;
import ai.metaheuristic.mhbp.data.RequestContext;
import ai.metaheuristic.mhbp.repositories.AuthRepository;
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
 * Date: 4/13/2023
 * Time: 12:03 AM
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final AuthRepository authRepository;
    private final ApplicationEventPublisher eventPublisher;

    public AuthData.Auths getAuths(Pageable pageable, RequestContext context) {
        pageable = ControllerUtils.fixPageSize(20, pageable);

        Page<Auth> auths = authRepository.findAllByCompanyUniqueId(pageable, context.getCompanyId());
        List<AuthData.SimpleAuth> list = auths.stream().map(AuthData.SimpleAuth::new).toList();
        var sorted = list.stream().sorted((o1, o2)->Long.compare(o2.id, o1.id)).collect(Collectors.toList());
        return new AuthData.Auths(new PageImpl<>(sorted, pageable, list.size()));
    }

    @Transactional
    public OperationStatusRest deleteAuthById(Long authId, RequestContext context) {
        if (authId==null) {
            return OperationStatusRest.OPERATION_STATUS_OK;
        }
        Auth auth = authRepository.findById(authId).orElse(null);
        if (auth == null) {
            return new OperationStatusRest(Enums.OperationStatus.ERROR,
                    "247.040 API wasn't found, authId: " + authId, null);
        }
        if (auth.companyId!=context.getCompanyId()) {
            return new OperationStatusRest(Enums.OperationStatus.ERROR, "247.080 authId: " + authId);
        }

        authRepository.deleteById(authId);
        return OperationStatusRest.OPERATION_STATUS_OK;
    }

    @Transactional
    public OperationStatusRest createAuth(String code, String params, RequestContext context) {
        Auth auth = new Auth();
        auth.code = code;
        auth.setParams(params);
        auth.companyId = context.getCompanyId();
        auth.accountId = context.getAccountId();
        auth.createdOn = System.currentTimeMillis();

        authRepository.save(auth);

        return OperationStatusRest.OPERATION_STATUS_OK;
    }

    public AuthData.Auth getAuth(@Nullable Long authId, RequestContext context) {
        if (authId==null) {
            return new AuthData.Auth("247.120 Not found");
        }
        Auth auth = authRepository.findById(authId).orElse(null);
        if (auth == null) {
            return new AuthData.Auth("247.160 Not found");
        }
        if (auth.companyId!=context.getCompanyId()) {
            return new AuthData.Auth("247.200 Illegal access");
        }
        return new AuthData.Auth(new AuthData.SimpleAuth(auth));
    }
}
