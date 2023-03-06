package ai.metaheuristic.mhbp.repositories;

import ai.metaheuristic.mhbp.beans.Account;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Sergio Lissner
 * Date: 3/5/2023
 * Time: 1:02 AM
 */
@Repository
@Transactional
public interface AccountRepository extends CrudRepository<Account, Long> {

    @Transactional(readOnly = true)
    @Nullable
    Account findByUsername(String username);
}
