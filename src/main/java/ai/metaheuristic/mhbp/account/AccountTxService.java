package ai.metaheuristic.mhbp.account;

import ai.metaheuristic.mhbp.beans.Account;
import lombok.RequiredArgsConstructor;
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

}
