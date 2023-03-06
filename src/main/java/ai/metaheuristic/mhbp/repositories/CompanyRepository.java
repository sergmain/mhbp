package ai.metaheuristic.mhbp.repositories;

import ai.metaheuristic.mhbp.beans.Company;
import ai.metaheuristic.mhbp.data.SimpleCompany;
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
 * Time: 5:01 PM
 */
@Repository
@Transactional
public interface CompanyRepository extends CrudRepository<Company, Long> {

    @Transactional(readOnly = true)
    @Nullable
    @Query(value="select a from Company a where a.uniqueId=:uniqueId")
    Company findByUniqueId(Long uniqueId);

    @Transactional(readOnly = true)
    @Query(value="select max(c.uniqueId) from Company c")
    Long getMaxUniqueIdValue();

    @Transactional(readOnly = true)
    @Query(value="select new ai.metaheuristic.mhbp.data.SimpleCompany(a.id, a.uniqueId, a.name) from Company a order by a.uniqueId")
    Page<SimpleCompany> findAllAsSimple(Pageable pageable);

    @Transactional(readOnly = true)
    @Nullable
    @Query(value="select a from Company a where a.uniqueId=:uniqueId")
    Company findByUniqueIdForUpdate(Long uniqueId);

}
