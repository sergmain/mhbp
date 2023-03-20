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

package ai.metaheuristic.mhbp.account;

import ai.metaheuristic.mhbp.Globals;
import ai.metaheuristic.mhbp.data.AccountData;
import ai.metaheuristic.mhbp.data.OperationStatusRest;
import ai.metaheuristic.mhbp.data.RequestContext;
import ai.metaheuristic.mhbp.utils.ControllerUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

/**
 * @author Sergio Lissner
 * Date: 3/5/2023
 * Time: 5:19 PM
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountTxService accountTxService;
    private final Globals globals;

    public AccountData.AccountsResult getAccounts(Pageable pageable, RequestContext context) {
        pageable = ControllerUtils.fixPageSize(globals.rowsLimit.defaultLimit, pageable);
        return accountTxService.getAccounts(pageable, context.getCompanyId());
    }

    public OperationStatusRest addAccount(AccountData.NewAccount account, RequestContext context) {
        return accountTxService.addAccount(account, context.getCompanyId(), "ROLE_OPERATOR");
    }

    public AccountData.AccountResult getAccount(Long id, RequestContext context) {
        return accountTxService.getAccount(id, context.getCompanyId());
    }

    public OperationStatusRest editFormCommit(@Nullable Long accountId, @Nullable String publicName, boolean enabled, RequestContext context) {
        return accountTxService.editFormCommit(accountId, publicName, enabled, context.getCompanyId());
    }

    public OperationStatusRest passwordEditFormCommit(Long accountId, String password, String password2, RequestContext context) {
        return accountTxService.passwordEditFormCommit(accountId, password, password2, context.getCompanyId());
    }

    public OperationStatusRest roleFormCommit(Long accountId, String roles, RequestContext context) {
        return accountTxService.roleFormCommit(accountId, roles, context.getCompanyId());
    }


}
