package ai.metaheuristic.mhbp;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Sergio Lissner
 * Date: 2/28/2023
 * Time: 1:44 PM
 */
@SpringBootApplication
@Slf4j
public class MhbpApplication {

    public static void main(String[] args) {
        final String encoding = System.getProperty("file.encoding");
        if (!StringUtils.equalsAnyIgnoreCase(encoding, "utf8", "utf-8")) {
            System.out.println("Encoding must be utf-8. Launch app with -Dfile.encoding=UTF-8, actual file.encoding: " + encoding);
            System.exit(-1);
        }
        SpringApplication.run(MhbpApplication.class, args);
    }
}