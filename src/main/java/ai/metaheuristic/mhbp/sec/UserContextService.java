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

package ai.metaheuristic.mhbp.sec;

import ai.metaheuristic.mhbp.beans.Account;
import ai.metaheuristic.mhbp.beans.Company;
import ai.metaheuristic.mhbp.data.RequestContext;
import ai.metaheuristic.mhbp.repositories.CompanyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/**
 * @author Sergio Lissner
 * Date: 3/5/2023
 * Time: 5:04 PM
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserContextService {

    private final CompanyRepository companyRepository;

    public RequestContext getContext(Authentication authentication) {
        Account account = (Account)authentication.getPrincipal();
        if (account==null) {
            throw new RuntimeException("principal is null");
        }
        return getContext(authentication, account.companyId);
    }

    public RequestContext getContext(Authentication authentication, Long companyUniqueId) {
        Account account = (Account)authentication.getPrincipal();
        if (account==null) {
            throw new RuntimeException("principal is null");
        }
        Company company = companyRepository.findByUniqueId(companyUniqueId);
        if (company==null) {
            throw new RuntimeException("company not found not found for user: " + account.username);
        }
        RequestContext context = new RequestContext(account, company);
        return context;
    }

}
