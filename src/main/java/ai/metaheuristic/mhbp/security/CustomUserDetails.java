package ai.metaheuristic.mhbp.security;

import ai.metaheuristic.mhbp.Globals;
import ai.metaheuristic.mhbp.account.AccountTxService;
import ai.metaheuristic.mhbp.beans.Account;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * User: Serg
 * Date: 12.08.13
 * Time: 23:17
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetailsService {

    private static final String USERNAME_QQQ = "qqq";
    public static final long ADMIN_ID = Integer.MAX_VALUE - 10L;

    private final AccountTxService accountTxService;
    private final Globals globals;

    @Value("${com.htrucker.manager.enabled}")
    boolean isManagerEnabled;

    @Data
    public static class ComplexUsername {
        public String username;

        private ComplexUsername(String username) {
            this.username = username;
        }

        @Nullable
        public static ComplexUsername getInstance(String fullUsername) {
            final String username = fullUsername;
            ComplexUsername complexUsername = new ComplexUsername(username);

            return complexUsername.isValid() ? complexUsername : null;
        }

        private boolean isValid() {
            return username!=null && !username.isBlank();
        }
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // strength - 10
        // pass     - 123
        // bcrypt   - $2a$10$jaQkP.gqwgenn.xKtjWIbeP4X.LDJx92FKaQ9VfrN2jgdOUTPTMIu

        ComplexUsername complexUsername = ComplexUsername.getInstance(username);
        if (complexUsername==null) {
            throw new UsernameNotFoundException("Username not found");
        }

        if (isManagerEnabled && StringUtils.equals(USERNAME_QQQ,complexUsername.getUsername())) {

            Account account = new Account();

            account.setId(ADMIN_ID);
            account.setUsername(USERNAME_QQQ);
            account.setAccountNonExpired(true);
            account.setAccountNonLocked(true);
            account.setCredentialsNonExpired(true);
            account.setEnabled(true);
            account.setPassword(globals.masterPassword);
            account.setPublicName(USERNAME_QQQ);

            account.setRoles("ROLE_ADMIN, ROLE_MANAGER");

            return account;
        }

        Account account = accountTxService.findByUsername(complexUsername.getUsername());
        if (account==null) {
            throw new UsernameNotFoundException("Bad credential");
        }
        return account;
    }

}
