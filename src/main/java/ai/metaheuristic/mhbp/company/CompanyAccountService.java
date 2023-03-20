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

package ai.metaheuristic.mhbp.company;

import ai.metaheuristic.mhbp.Consts;
import ai.metaheuristic.mhbp.account.AccountTxService;
import ai.metaheuristic.mhbp.data.AccountData;
import ai.metaheuristic.mhbp.data.OperationStatusRest;
import ai.metaheuristic.mhbp.utils.ControllerUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

/**
 * @author Sergio Lissner
 * Date: 3/5/2023
 * Time: 6:05 PM
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyAccountService {
    private final AccountTxService accountTxService;

    public AccountData.AccountsResult getAccounts(Pageable pageable, Long companyUniqueId)  {
        pageable = ControllerUtils.fixPageSize(50, pageable);
        return accountTxService.getAccounts(pageable, companyUniqueId);
    }

    public OperationStatusRest addAccount(AccountData.NewAccount account, Long companyUniqueId) {
        // don't set any role when account is created
        return accountTxService.addAccount(account, companyUniqueId, "");
    }

    public AccountData.AccountResult getAccount(Long id, Long companyUniqueId){
        return accountTxService.getAccount(id, companyUniqueId);
    }

    public AccountData.AccountWithRoleResult getAccountWithRole(Long id, Long companyUniqueId){
        AccountData.AccountResult account = accountTxService.getAccount(id, companyUniqueId);
        return new AccountData.AccountWithRoleResult(
                account.account,
                Consts.ID_1.equals(companyUniqueId) ? Consts.COMPANY_1_POSSIBLE_ROLES : Consts.POSSIBLE_ROLES, account.getErrorMessages());
    }

    public OperationStatusRest editFormCommit(@Nullable Long accountId, @Nullable String publicName, boolean enabled, @Nullable Long companyUniqueId) {
        return accountTxService.editFormCommit(accountId, publicName, enabled, companyUniqueId);
    }

    public OperationStatusRest passwordEditFormCommit(Long accountId, String password, String password2, Long companyUniqueId) {
        return accountTxService.passwordEditFormCommit(accountId, password, password2, companyUniqueId);
    }

    public OperationStatusRest storeRolesForUserById(Long accountId, String role, boolean checkbox, Long companyUniqueId) {
        return accountTxService.storeRolesForUserById(accountId, role, checkbox, companyUniqueId);
    }


}
