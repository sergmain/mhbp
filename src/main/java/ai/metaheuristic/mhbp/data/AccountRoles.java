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

package ai.metaheuristic.mhbp.data;

import ai.metaheuristic.mhbp.utils.S;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static ai.metaheuristic.mhbp.data.AccountData.*;

/**
 * @author Sergio Lissner
 * Date: 3/5/2023
 * Time: 5:46 PM
 */
public class AccountRoles {

    @Data
    public static class InitedRoles {
        public boolean inited;
        public final List<String> roles = new ArrayList<>();

        public void reset() {
            inited = false;
            roles.clear();
        }
        public boolean contains(String role) {
            if (!inited) {
                throw new IllegalStateException("(!inited)");
            }
            return roles.contains(role);
        }

        public void addRole(String role) {
            roles.add(role);
        }

        public void removeRole(String role) {
            roles.remove(role);
        }

        public String asString() {
            return String.join(", ", roles);
        }
    }

    private final Supplier<String> roleGetter;
    private final Consumer<String> roleSetter;

    private final InitedRoles initedRoles = new InitedRoles();
    private final List<SerializableGrantedAuthority> authorities = new ArrayList<>();

    public AccountRoles(Supplier<String> roleGetter, Consumer<String> roleSetter) {
        this.roleSetter = roleSetter;
        this.roleGetter = roleGetter;
    }

    public boolean hasRole(String role) {
        initRoles();
        return initedRoles.contains(role);
    }

    public List<SerializableGrantedAuthority> getAuthorities() {
        initRoles();
        return authorities;
    }

    public List<String> getRolesAsList() {
        initRoles();
        return new ArrayList<>(initedRoles.roles);
    }

    public void addRole(String role) {
        synchronized (this) {
            initedRoles.addRole(role);
            this.roleSetter.accept(initedRoles.asString());
            initedRoles.reset();
            authorities.clear();
            initRoles();
        }
    }

    public void removeRole(String role) {
        synchronized (this) {
            initedRoles.removeRole(role);
            this.roleSetter.accept(initedRoles.asString());
            initedRoles.reset();

            authorities.clear();
            initRoles();
        }
    }

    private void initRoles() {
        if (initedRoles.inited) {
            return;
        }
        synchronized (this) {
            if (initedRoles.inited) {
                return;
            }
            if (this.roleGetter.get()!=null) {
                StringTokenizer st = new StringTokenizer(this.roleGetter.get(), ",");
                while (st.hasMoreTokens()) {
                    String token = st.nextToken();
                    if (S.b(token)) {
                        continue;
                    }
                    String role = token.trim();
                    initedRoles.roles.add(role);
                    authorities.add(new SerializableGrantedAuthority(role));
                }
            }
            initedRoles.inited = true;
        }
    }

}
