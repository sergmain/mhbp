package ai.metaheuristic.mhbp.account;

import ai.metaheuristic.api.data.OperationStatusRest;
import ai.metaheuristic.commons.utils.PageUtils;
import ai.metaheuristic.mhbp.Globals;
import ai.metaheuristic.mhbp.data.AccountData;
import ai.metaheuristic.mhbp.data.RequestContext;
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
        pageable = PageUtils.fixPageSize(globals.rowsLimit.defaultLimit, pageable);
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
