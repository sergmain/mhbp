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

package ai.metaheuristic.mhbp.repositories;

import ai.metaheuristic.mhbp.beans.Answer;
import ai.metaheuristic.mhbp.data.ErrorData;
import ai.metaheuristic.mhbp.data.EvalStatusGrouped;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Sergio Lissner
 * Date: 3/22/2023
 * Time: 1:58 AM
 */
@Repository
@Transactional
public interface AnswerRepository extends CrudRepository<Answer, Long> {

    @Transactional(readOnly = true)
    @Query(value=
            """
              SELECT s.ID,
                     COALESCE(a.total, 0) AS total,
                     COALESCE(b.normal, 0) AS normal,
                     COALESCE(c.fail, 0) AS fail,
                     COALESCE(d.error, 0) AS error
              FROM mhbp_session s
                       LEFT OUTER JOIN (SELECT SESSION_ID, COUNT(*) AS total
                                        FROM mhbp_answer
                                        GROUP BY SESSION_ID) a ON s.ID = a.SESSION_ID
                       LEFT OUTER JOIN (SELECT SESSION_ID, COUNT(*) AS normal
                                        FROM mhbp_answer
                                        WHERE STATUS = 0
                                        GROUP BY SESSION_ID) b ON s.ID = b.SESSION_ID
                       LEFT OUTER JOIN (SELECT SESSION_ID, COUNT(*) AS fail
                                        FROM mhbp_answer
                                        WHERE STATUS = 1
                                        GROUP BY SESSION_ID) c ON s.ID = c.SESSION_ID
                       LEFT OUTER JOIN (SELECT SESSION_ID, COUNT(*) AS error
                                        FROM mhbp_answer
                                        WHERE STATUS = 2
                                        GROUP BY SESSION_ID) d ON s.ID = d.SESSION_ID
              WHERE s.ID = 7;""", nativeQuery = true)
    List<Object[]> getStatuses(List<Long> sessionIds);


    @Transactional(readOnly = true)
    @Query(value=
            """
SELECT s.id,\s
       COUNT(a.sessionId) AS total,\s
       COUNT(CASE WHEN a.status = 0 THEN 1 ELSE NULL END) AS normal,\s
       COUNT(CASE WHEN a.status = 1 THEN 1 ELSE NULL END) AS fail,\s
       COUNT(CASE WHEN a.status = 2 THEN 1 ELSE NULL END) AS error,\s
       s.evaluationId
FROM Session s\s
LEFT OUTER JOIN Answer a ON s.id = a.sessionId\s
WHERE s.id in :sessionIds
GROUP BY s.id

""", nativeQuery = false)
    List<Object[]> getStatusesJpql(List<Long> sessionIds);

    // status - public enum AnswerStatus { normal(0), fail(1), error(2);
    @Transactional(readOnly = true)
    @Query(value="select s from Answer s where s.sessionId=:sessionId and s.status!=0")
    Page<Answer> findAllBySessionId(Pageable pageable, Long sessionId);

}