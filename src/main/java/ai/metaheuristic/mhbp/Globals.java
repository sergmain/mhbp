package ai.metaheuristic.mhbp;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Sergio Lissner
 * Date: 3/4/2023
 * Time: 3:29 PM
 */
@ConfigurationProperties("mhbp")
@Getter
@Setter
@Slf4j
@RequiredArgsConstructor
public class Globals {

    public static class Threads {
        @Getter @Setter
        private int scheduler = 10;
        @Getter @Setter
        private int event =  10;
    }

    public Threads threads;

    public String masterPassword;
}
