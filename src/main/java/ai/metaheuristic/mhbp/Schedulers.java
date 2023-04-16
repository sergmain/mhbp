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

package ai.metaheuristic.mhbp;

import ai.metaheuristic.mhbp.provider.ProcessSessionOfEvaluationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * @author Sergio Lissner
 * Date: 3/19/2023
 * Time: 10:49 PM
 */
public class Schedulers {

    @Service
    @EnableScheduling
    @Slf4j
    @RequiredArgsConstructor
    public static class DispatcherSchedulers {

        private final Globals globals;
        private final ProcessSessionOfEvaluationService evaluateProviderService;

        @Scheduled(initialDelay = 15_000, fixedDelay = 5_000 )
        public void processEvaluateProviderService() {
            if (globals.testing) {
                return;
            }
            evaluateProviderService.processSessionEvent();
        }
    }
}
