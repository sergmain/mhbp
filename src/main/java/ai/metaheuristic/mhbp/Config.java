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

import ai.metaheuristic.mhbp.repositories.RefToRepositories;
import ai.metaheuristic.mhbp.utils.CleanerInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Sergio Lissner
 * Date: 3/4/2023
 * Time: 3:31 PM
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
@EnableConfigurationProperties(Globals.class)
public class Config {

    private final Globals globals;

    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        log.info("Config.threadPoolTaskScheduler() will use {} as a number of threads for an schedulers", globals.threads.getScheduler());
        threadPoolTaskScheduler.setPoolSize(globals.threads.getScheduler());
        return threadPoolTaskScheduler;
    }

    @Configuration
    public static class MhMvcConfig implements WebMvcConfigurer {
        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            registry.addInterceptor(new CleanerInterceptor());
        }
    }

    @Configuration
    @ComponentScan("ai.metaheuristic.mhbp")
    @EnableAsync
    @RequiredArgsConstructor
    @Slf4j
    public static class SpringAsyncConfig implements AsyncConfigurer {

        private final Globals globals;

        @Override
        public Executor getAsyncExecutor() {
            int threads = globals.threads.getEvent();
            log.info("Config.SpringAsyncConfig will use {} as a number of threads for an event processing", threads);

            ThreadPoolExecutor executor =  (ThreadPoolExecutor) Executors.newFixedThreadPool(threads);
            return new ConcurrentTaskExecutor(executor);
        }
    }

    @EnableCaching
    @Configuration
    @ComponentScan("ai.metaheuristic.mhbp")
    @EnableTransactionManagement
    @EnableJpaRepositories(basePackageClasses = {RefToRepositories.class} )
    public static class DispatcherConfig {
    }
}
