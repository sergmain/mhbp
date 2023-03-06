package ai.metaheuristic.mhbp.rest.stub;

import ai.metaheuristic.mhbp.Enums;
import ai.metaheuristic.mhbp.company.CompanyAccountService;
import ai.metaheuristic.mhbp.company.CompanyService;
import ai.metaheuristic.mhbp.data.AccountData;
import ai.metaheuristic.mhbp.data.CompanyData;
import ai.metaheuristic.mhbp.data.OperationStatusRest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * @author Sergio Lissner
 * Date: 3/5/2023
 * Time: 7:20 PM
 */
@RestController
@RequestMapping("/rest/v1/mhbp/company")
@Slf4j
@Profile("stub-data")
@CrossOrigin
@RequiredArgsConstructor
public class CompanyRestController {

    private final CompanyService companyTopLevelService;
    private final CompanyAccountService companyAccountTopLevelService;

    @PreAuthorize("hasAnyRole('MASTER_ADMIN', 'MASTER_OPERATOR', 'MASTER_SUPPORT')")
    @GetMapping("/companies")
    public CompanyData.SimpleCompaniesResult companies(@PageableDefault(size = 50) Pageable pageable) {
        CompanyData.SimpleCompaniesResult companies = companyTopLevelService.getCompanies(pageable);
        return companies;
    }

    @PostMapping("/company-add-commit")
    @PreAuthorize("hasAnyRole('MASTER_ADMIN')")
    public OperationStatusRest addFormCommit(String companyName) {
        OperationStatusRest operationStatusRest = companyTopLevelService.addCompany(companyName);
        return operationStatusRest;
    }

    @GetMapping(value = "/company-edit/{companyUniqueId}")
    @PreAuthorize("hasAnyRole('MASTER_ADMIN')")
    public CompanyData.SimpleCompanyResult editCompany(@PathVariable Long companyUniqueId){
        CompanyData.SimpleCompanyResult companyResult = companyTopLevelService.getSimpleCompany(companyUniqueId);
        return companyResult;
    }

    @PostMapping("/company-edit-commit")
    @PreAuthorize("hasAnyRole('MASTER_ADMIN')")
    public OperationStatusRest editFormCommit(Long companyUniqueId, String name, String groups) {
        OperationStatusRest operationStatusRest = companyTopLevelService.editFormCommit(companyUniqueId, name, groups);
        return operationStatusRest;
    }

    // === accounts for companies =====================

    @GetMapping("/company-accounts/{companyUniqueId}")
    @PreAuthorize("hasAnyRole('MASTER_ADMIN')")
    public AccountData.AccountsResult accounts(@PageableDefault(size = 5) Pageable pageable, @PathVariable Long companyUniqueId) {
        AccountData.AccountsResult accounts = companyAccountTopLevelService.getAccounts(pageable, companyUniqueId);
        return accounts;
    }

    @PostMapping("/company-account-add-commit/{companyUniqueId}")
    @PreAuthorize("hasAnyRole('MASTER_ADMIN')")
    public OperationStatusRest addFormCommit(@RequestBody AccountData.NewAccount account, @PathVariable Long companyUniqueId) {
        OperationStatusRest operationStatusRest = companyAccountTopLevelService.addAccount(account, companyUniqueId);
        return operationStatusRest;
    }

    @GetMapping(value = "/company-account-edit/{companyUniqueId}/{id}")
    @PreAuthorize("hasAnyRole('MASTER_ADMIN')")
    public AccountData.AccountResult edit(@PathVariable Long id, @PathVariable Long companyUniqueId){
        AccountData.AccountResult accountResult = companyAccountTopLevelService.getAccount(id, companyUniqueId);
        return accountResult;
    }

    @PostMapping("/company-account-edit-commit/{companyUniqueId}")
    @PreAuthorize("hasAnyRole('MASTER_ADMIN')")
    public OperationStatusRest editFormCommit(@Nullable Long id, @Nullable String publicName, boolean enabled, @Nullable @PathVariable Long companyUniqueId) {
        OperationStatusRest operationStatusRest = companyAccountTopLevelService.editFormCommit(id, publicName, enabled, companyUniqueId);
        return operationStatusRest;
    }

    @GetMapping(value = "/company-account-password-edit/{companyUniqueId}/{id}")
    @PreAuthorize("hasAnyRole('MASTER_ADMIN')")
    public AccountData.AccountResult passwordEdit(@PathVariable Long id, @PathVariable Long companyUniqueId){
        AccountData.AccountResult accountResult = companyAccountTopLevelService.getAccount(id, companyUniqueId);
        return accountResult;
    }

    @PostMapping("/company-account-password-edit-commit/{companyUniqueId}")
    @PreAuthorize("hasAnyRole('MASTER_ADMIN')")
    public OperationStatusRest passwordEditFormCommit(Long id, String password, String password2, @PathVariable Long companyUniqueId) {
        OperationStatusRest operationStatusRest = companyAccountTopLevelService.passwordEditFormCommit(id, password, password2, companyUniqueId);
        return operationStatusRest;
    }

    @GetMapping(value = "/company-account-edit-roles/{companyUniqueId}/{id}")
    @PreAuthorize("hasAnyRole('MASTER_ADMIN')")
    public AccountData.AccountWithRoleResult editRoles(@PathVariable Long id, @PathVariable Long companyUniqueId) {
        AccountData.AccountWithRoleResult accountWithRole = companyAccountTopLevelService.getAccountWithRole(id, companyUniqueId);
        return accountWithRole;
    }

    /**
     *
     * @param accountId Account.id
     * @param role name of role to set or remove
     * @param checkbox flag to set a role or to remove it
     * @param companyUniqueId Account.companyId
     */
    @PostMapping("/company-account-edit-roles-commit/{companyUniqueId}")
    @PreAuthorize("hasAnyRole('MASTER_ADMIN')")
    public OperationStatusRest rolesEditFormCommit(Long accountId, String role, @RequestParam(required = false, defaultValue = "false") boolean checkbox,
                                                   @PathVariable Long companyUniqueId) {
        AccountData.AccountResult accountResult = companyAccountTopLevelService.getAccount(accountId, companyUniqueId);
        if (accountResult.isErrorMessages()) {
            return new OperationStatusRest(Enums.OperationStatus.ERROR, accountResult.getErrorMessages(), accountResult.infoMessages);
        }
        OperationStatusRest operationStatusRest = companyAccountTopLevelService.storeRolesForUserById(accountId, role, checkbox, companyUniqueId);
        return operationStatusRest;
    }


}
