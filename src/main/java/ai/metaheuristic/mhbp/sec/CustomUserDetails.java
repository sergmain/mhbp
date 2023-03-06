package ai.metaheuristic.mhbp.sec;

import ai.metaheuristic.mhbp.Consts;
import ai.metaheuristic.mhbp.Globals;
import ai.metaheuristic.mhbp.account.AccountTxService;
import ai.metaheuristic.mhbp.beans.Account;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.lang.Nullable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author Sergio Lissner
 * Date: 3/5/2023
 * Time: 7:54 PM
 */
@Service
@Profile("dispatcher")
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetailsService {

    private final Globals globals;
    private final AccountTxService accountTxService;

    @Data
    public static class ComplexUsername {
        String username;

        private ComplexUsername(String username) {
            this.username = username;
        }

        @Nullable
        public static ComplexUsername getInstance(String fullUsername) {
            int idx = fullUsername.lastIndexOf('=');
            final String username;
            if (idx == -1) {
                username = fullUsername;
            }
            else {
                username = fullUsername.substring(0, idx);
            }
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
        if (complexUsername == null) {
            throw new UsernameNotFoundException("Username not found");
        }

        if (StringUtils.equals(globals.mainUsername, complexUsername.getUsername())) {

            Account account = new Account();

            // fake Id, I hope it won't make any collision with the real accounts
            // need to think of better solution for virtual accounts
            account.setId(Integer.MAX_VALUE -5L);

            // master admin will belong to companyUniqueId==1
            account.setCompanyId(1L);
            account.setUsername(globals.mainUsername);
            account.setAccountNonExpired(true);
            account.setAccountNonLocked(true);
            account.setCredentialsNonExpired(true);
            account.setEnabled(true);
            account.setPassword(globals.mainPassword);

            account.setRoles(Consts.ROLE_MAIN_ADMIN);
            return account;
        }

        Account account = accountTxService.findByUsername(complexUsername.getUsername());
        if (account == null) {
            throw new UsernameNotFoundException("Username not found");
        }

        if (!Consts.ID_1.equals(account.getCompanyId()) && account.accountRoles.hasRole(Consts.ROLE_SERVER_REST_ACCESS)) {
            account.accountRoles.removeRole(Consts.ROLE_SERVER_REST_ACCESS);
        }

        return account;
    }
}
