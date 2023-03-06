package ai.metaheuristic.mhbp.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serial;

/**
 * @author Sergio Lissner
 * Date: 3/5/2023
 * Time: 3:42 PM
 */
public class AccountData {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SerializableGrantedAuthority implements GrantedAuthority {
        @Serial
        private static final long serialVersionUID = 9030978659705840954L;

        public String authority;
    }

    public static class AccountsResult {

    }
}
