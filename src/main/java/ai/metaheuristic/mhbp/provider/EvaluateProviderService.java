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

package ai.metaheuristic.mhbp.provider;

import ai.metaheuristic.mhbp.events.EvaluateProviderEvent;
import ai.metaheuristic.mhbp.questions.QuestionData;
import ai.metaheuristic.mhbp.questions.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Sergio Lissner
 * Date: 3/19/2023
 * Time: 10:46 PM
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class EvaluateProviderService {

    public final QuestionService questionService;

    private final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);

    private final LinkedList<EvaluateProviderEvent> queue = new LinkedList<>();

    @Async
    @EventListener
    public void handleEvaluateProviderEvent(EvaluateProviderEvent event) {
        putToQueue(event);
    }

    public void putToQueue(final EvaluateProviderEvent event) {
        synchronized (queue) {
            if (queue.contains(event)) {
                return;
            }
            queue.add(event);
        }
    }

    @Nullable
    private EvaluateProviderEvent pullFromQueue() {
        synchronized (queue) {
            return queue.pollFirst();
        }
    }

    public void processEvaluateProviderEvent() {
        if (executor.getActiveCount()>0) {
            return;
        }
        executor.submit(() -> {
            EvaluateProviderEvent event;
            while ((event = pullFromQueue())!=null) {
                evaluateProvider(event);
            }
        });
    }

    public void evaluateProvider(EvaluateProviderEvent event) {
        try {
            log.debug("call EvaluateProviderService.evaluateProvider({})", event.providerCode());
            List<QuestionData.QuestionToAsk> questions = questionService.getQuestionToAsk();
            for (QuestionData.QuestionToAsk question : questions) {

            }

        } catch (Throwable th) {
            log.error("417.020 Error, need to investigate ", th);
        }
    }
}
