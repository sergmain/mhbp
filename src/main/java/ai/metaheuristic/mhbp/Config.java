package ai.metaheuristic.mhbp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

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
}
