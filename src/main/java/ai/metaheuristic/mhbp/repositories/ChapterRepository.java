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
import ai.metaheuristic.mhbp.beans.Chapter;
import ai.metaheuristic.mhbp.data.SimpleChapterUid;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Sergio Lissner
 * Date: 4/27/2023
 * Time: 3:19 AM
 */
@Repository
@Transactional
public interface ChapterRepository extends CrudRepository<Chapter, Long> {

    @Nullable
    Chapter findByKbIdAndCode(long kbId, String code);

    @Transactional(readOnly = true)
    @Query(value= "select new ai.metaheuristic.mhbp.data.SimpleChapterUid(a.id, a.code) " +
                  " from Chapter a where a.kbId in(:kbIds)")
    List<SimpleChapterUid> findAllByKbIds(List<Long> kbIds);
}
