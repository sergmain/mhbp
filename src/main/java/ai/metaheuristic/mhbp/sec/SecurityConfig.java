package ai.metaheuristic.mhbp.sec;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * @author Sergio Lissner
 * Date: 3/5/2023
 * Time: 1:41 AM
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    public static final String REST_REALM = "REST realm";

    @Bean
    @Order(0)
    public SecurityFilterChain restFilterChain(HttpSecurity http) throws Exception {
        initRestSecurity(http);
        return http.build();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain defaultFilterChain(HttpSecurity http) throws Exception {
        initSecurity(http);
        return http.build();
    }

    private static void initRestSecurity(HttpSecurity http) throws Exception {
        http
                .antMatcher("/rest/**/**").sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/rest/login", "/rest/v1/public/**/**").permitAll()
                .antMatchers("/rest/**/**").authenticated()
                .and()
                .antMatcher("/rest/**/**").httpBasic().realmName(REST_REALM)
                .and()
                .antMatcher("/rest/**/**").csrf().disable().headers().cacheControl();
    }

    private static void initSecurity(HttpSecurity http) throws Exception {
        http
                .headers().frameOptions().sameOrigin()
                .and()
                .authorizeRequests()
                .antMatchers("/manager/html").denyAll()
                .antMatchers("/static/**/**", "/css/**", "/js/**", "/webjars/**").permitAll()
                .antMatchers("/favicon.ico", "/", "/index", "/content/public/**/**", "/login", "/jssc", "/error/**").permitAll()
                .antMatchers("/login").anonymous()
                .antMatchers("/logout", "/content/secured/**/**").authenticated()
                .antMatchers("/admin/**").hasAnyRole("ADMIN")
                .antMatchers("/user/**").hasAnyRole("USER")
                .antMatchers("/**/**").denyAll()
                .and()
                .formLogin()
                .loginPage("/login")
                .usernameParameter("j_username")
                .passwordParameter("j_password")
                .loginProcessingUrl("/jssc")
                .defaultSuccessUrl("/index")
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/index");
    }
}
