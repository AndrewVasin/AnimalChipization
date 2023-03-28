package ru.vasin.animalchipization.security;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import ru.vasin.animalchipization.service.AccountServiceImpl;

@EnableWebSecurity
public class SecurityConfig {

    private final AccountServiceImpl accountService;

    public SecurityConfig(AccountServiceImpl accountService) {
        this.accountService = accountService;
    }

//    private final CustomAuthenticationProvider authProvider;

//    public SecurityConfig(CustomAuthenticationProvider authProvider) {
//        this.authProvider = authProvider;
//    }
//
//    @Autowired
//    public void configAuthentication(AuthenticationManagerBuilder auth) throws Exception {
//        auth.authenticationProvider(authProvider);
//    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeRequests()
                .antMatchers(HttpMethod.POST,"/registration").anonymous()
                .and()
                .httpBasic().and().authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .csrf().disable()
                .build();
    }

    @Bean
    public DaoAuthenticationProvider daoAuth() {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setPasswordEncoder(passwordEncoder());
        auth.setUserDetailsService(accountService);
        return auth;
    }
}
