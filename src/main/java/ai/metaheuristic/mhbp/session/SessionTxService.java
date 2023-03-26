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

package ai.metaheuristic.mhbp.session;

import ai.metaheuristic.mhbp.Enums;
import ai.metaheuristic.mhbp.beans.Session;
import ai.metaheuristic.mhbp.repositories.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Sergio Lissner
 * Date: 3/22/2023
 * Time: 2:51 AM
 */
@Service
@RequiredArgsConstructor
public class SessionTxService {

    public final SessionRepository sessionRepository;

    @Transactional
    public Session create(String providerCode) {
        Session s = new Session();
        s.createdOn = System.currentTimeMillis();
        s.providerCode = providerCode;
        s.status = Enums.SessionStatus.created.code;
        sessionRepository.save(s);
        return s;
    }

    @Transactional
    public void finish(Session s, Enums.SessionStatus status) {
        s.finishedOn = System.currentTimeMillis();
        s.status = status.code;
        sessionRepository.save(s);
    }


}
