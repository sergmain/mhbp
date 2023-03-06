package ai.metaheuristic.mhbp.repositories;

import ai.metaheuristic.mhbp.beans.Account;
import ai.metaheuristic.mhbp.data.SimpleAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
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

    @Transactional(readOnly = true)
    @Query(value= "select new ai.metaheuristic.mhbp.data.SimpleAccount(a.id, a.companyId, a.username, a.publicName, a.enabled, a.createdOn, a.updatedOn, a.roles) " +
                  " from Account a where a.companyId=:companyUniqueId")
    Page<SimpleAccount> findAllByCompanyUniqueId(Pageable pageable, Long companyUniqueId);

    @Transactional(readOnly = true)
    @Nullable
    @Query(value="select a from Account a where a.id=:id")
    Account findByIdForUpdate(Long id);


}
