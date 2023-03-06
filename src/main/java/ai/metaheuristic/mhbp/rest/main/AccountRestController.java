package ai.metaheuristic.mhbp.rest.main;

import ai.metaheuristic.api.data.OperationStatusRest;
import ai.metaheuristic.mhbp.account.AccountService;
import ai.metaheuristic.mhbp.data.AccountData;
import ai.metaheuristic.mhbp.data.RequestContext;
import ai.metaheuristic.mhbp.sec.UserContextService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * @author Sergio Lissner
 * Date: 3/5/2023
 * Time: 1:47 AM
 */
@RestController
@RequestMapping("/rest/v1/mhbp/account")
@Slf4j
@Profile("!stub-data")
@CrossOrigin
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN')")
public class AccountRestController {

    private final AccountService accountTopLevelService;
    private final UserContextService userContextService;

    @GetMapping("/accounts")
    public AccountData.AccountsResult accounts(@PageableDefault(size = 5) Pageable pageable, Authentication authentication) {
        RequestContext context = userContextService.getContext(authentication);
        return accountTopLevelService.getAccounts(pageable, context);
    }

    @PostMapping("/account-add-commit")
    public OperationStatusRest addFormCommit(@RequestBody AccountData.NewAccount account, Authentication authentication) {
        RequestContext context = userContextService.getContext(authentication);
        return accountTopLevelService.addAccount(account, context);
    }

    @GetMapping(value = "/account/{id}")
    public AccountData.AccountResult getAccount(@PathVariable Long id, Authentication authentication) {
        RequestContext context = userContextService.getContext(authentication);
        return accountTopLevelService.getAccount(id, context);
    }

    @PostMapping("/account-edit-commit")
    public OperationStatusRest editFormCommit(Long id, String publicName, boolean enabled, Authentication authentication) {
        RequestContext context = userContextService.getContext(authentication);
        return accountTopLevelService.editFormCommit(id, publicName, enabled, context);
    }

    @PostMapping("/account-role-commit")
    public OperationStatusRest roleFormCommit(Long accountId, String roles, Authentication authentication) {
        RequestContext context = userContextService.getContext(authentication);
        return accountTopLevelService.roleFormCommit(accountId, roles, context);
    }

    @PostMapping("/account-password-edit-commit")
    public OperationStatusRest passwordEditFormCommit(Long id, String password, String password2, Authentication authentication) {
        RequestContext context = userContextService.getContext(authentication);
        return accountTopLevelService.passwordEditFormCommit(id, password, password2, context);
    }
}
