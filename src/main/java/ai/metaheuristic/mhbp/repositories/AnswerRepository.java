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
import ai.metaheuristic.mhbp.data.SimpleAnswerStats;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
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
    @Query(value="select new ai.metaheuristic.mhbp.data.SimpleAnswerStats(a.id, a.sessionId, a.chapterId, a.total, a.failed, a.systemError) " +
                 " from Answer a where a.sessionId in (:sessionIds)")
    List<SimpleAnswerStats> getStatusesJpql(List<Long> sessionIds);

    // status - public enum AnswerStatus { normal(0), fail(1), error(2);
    @Transactional(readOnly = true)
    @Query(value="select s from Answer s where s.sessionId=:sessionId")
    Page<Answer> findAllBySessionId(Pageable pageable, Long sessionId);

}