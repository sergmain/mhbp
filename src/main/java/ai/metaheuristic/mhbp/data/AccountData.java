package ai.metaheuristic.mhbp.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.lang.Nullable;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serial;
import java.util.Collections;
import java.util.List;

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

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NewAccount {
        public String username;
        public String password;
        public String password2;
        public String publicName;
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    @NoArgsConstructor
    public static class AccountResult extends BaseDataClass {
        public SimpleAccount account;

        public AccountResult(String errorMessage) {
            this.errorMessages = Collections.singletonList(errorMessage);
        }

        public AccountResult(SimpleAccount account, String errorMessage) {
            this.account = account;
            this.errorMessages = Collections.singletonList(errorMessage);
        }

        public AccountResult(SimpleAccount account) {
            this.account = account;
        }
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class AccountsResult extends BaseDataClass {
        public Page<SimpleAccount> accounts;
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    @NoArgsConstructor
    public static class AccountWithRoleResult extends BaseDataClass {
        public SimpleAccount account;
        public List<String> possibleRoles;

        public AccountWithRoleResult(SimpleAccount account, List<String> possibleRoles, @Nullable List<String> errorMessage) {
            this.account = account;
            this.possibleRoles = possibleRoles;
            this.errorMessages = errorMessage;
        }
    }


}
