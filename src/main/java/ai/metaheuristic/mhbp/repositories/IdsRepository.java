package ai.metaheuristic.mhbp.repositories;

import ai.metaheuristic.mhbp.beans.Ids;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Sergio Lissner
 * Date: 3/5/2023
 * Time: 6:16 PM
 */
@Repository
@Transactional
public interface IdsRepository extends CrudRepository<Ids, Long> {

    @Override
    @Modifying
    @Query(value="delete from Ids t where t.id=:id")
    void deleteById(Long id);

}
