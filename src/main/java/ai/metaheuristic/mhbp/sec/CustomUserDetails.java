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

package ai.metaheuristic.mhbp.sec;

import ai.metaheuristic.mhbp.Consts;
import ai.metaheuristic.mhbp.Globals;
import ai.metaheuristic.mhbp.account.AccountTxService;
import ai.metaheuristic.mhbp.beans.Account;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
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
            ComplexUsername complexUsername = new ComplexUsername(fullUsername);
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

            // main admin will belong to companyUniqueId==1
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
