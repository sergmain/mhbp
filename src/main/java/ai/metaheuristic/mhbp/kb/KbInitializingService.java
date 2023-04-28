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

package ai.metaheuristic.mhbp.kb;

import ai.metaheuristic.mhbp.events.InitKbEvent;
import ai.metaheuristic.mhbp.provider.ProviderQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bytecode.Throw;
import org.springframework.context.event.EventListener;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Sergio Lissner
 * Date: 4/23/2023
 * Time: 11:57 PM
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class KbInitializingService {

    public final KbService kbService;
    private final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
    private final LinkedList<InitKbEvent> queue = new LinkedList<>();

    @Async
    @EventListener
    public void handleInitKbEvent(InitKbEvent event) {
        putToQueue(event);
    }

    public void putToQueue(final InitKbEvent event) {
        synchronized (queue) {
            if (queue.contains(event)) {
                return;
            }
            queue.add(event);
        }
    }

    @Nullable
    private InitKbEvent pullFromQueue() {
        synchronized (queue) {
            return queue.pollFirst();
        }
    }

    public void processEvent() {
        if (executor.getActiveCount()>0) {
            return;
        }
        executor.submit(() -> {
            InitKbEvent event;
            while ((event = pullFromQueue())!=null) {
                try {
                    kbService.processInitKbEvent(event);
                }
                catch (Throwable th) {
                    log.error("Error while initialization of KB #"+event.kbId());
                }
            }
        });
    }

}
