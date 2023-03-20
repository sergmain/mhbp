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

package ai.metaheuristic.mhbp.rest;

import ai.metaheuristic.mhbp.beans.Account;
import ai.metaheuristic.mhbp.data.AccountData;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Collection;

/**
 * @author Sergio Lissner
 * Date: 3/5/2023
 * Time: 7:10 PM
 */
@RestController
@RequestMapping("/rest/v1")
//@Profile("!stub-data")
//@CrossOrigin
public class AuthRestController {

    // this end-point is used by angular's part only
    @RequestMapping("/user")
    public AccountData.UserData user(Principal user) {
        UsernamePasswordAuthenticationToken passwordAuthenticationToken = (UsernamePasswordAuthenticationToken) user;
        Account acc = (Account) passwordAuthenticationToken.getPrincipal();
        Collection<GrantedAuthority> authorities = passwordAuthenticationToken.getAuthorities();
        return new AccountData.UserData(acc.username, acc.getPublicName(), authorities, acc.companyId);
    }


}