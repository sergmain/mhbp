package ai.metaheuristic.mhbp.account;

import ai.metaheuristic.mhbp.beans.Account;
import ai.metaheuristic.mhbp.data.AccountData;
import ai.metaheuristic.mhbp.repositories.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Sergio Lissner
 * Date: 3/1/2023
 * Time: 11:18 PM
 */
@Service
@RequiredArgsConstructor
public class AccountTxService {

    public final AccountRepository accountRepository;

    @Nullable
    @Transactional(readOnly = true)
    public Account findByUsername(String username) {
        return accountRepository.findByUsername(username);
    }

    @Transactional(readOnly = true)
    public AccountData.AccountsResult getAccounts(Pageable pageable, Long companyUniqueId)  {
        AccountData.AccountsResult result = new AccountData.AccountsResult();
        result.accounts = accountRepository.findAllByCompanyUniqueId(pageable, companyUniqueId);
        result.assetMode = globals.dispatcher.asset.mode;
        return result;
    }

}
