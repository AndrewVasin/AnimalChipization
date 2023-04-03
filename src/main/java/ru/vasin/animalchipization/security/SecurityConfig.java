package ru.vasin.animalchipization.security;

import org.springframework.context.annotation.Bean;
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

    private static final String[] AUTH_WHITELIST = {

            // for Swagger UI v2
            "/v2/api-docs",
            "/swagger-ui.html",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/webjars/**",
            "/configuration",

            // for Swagger UI v3 (OpenAPI)
            "/v3/api-docs/**",
            "/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui/index.html"
    };

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
                .csrf()
//                .and().cors()
                .disable()
//                .authorizeHttpRequests()
//                    .antMatchers(AUTH_WHITELIST).permitAll()
//                .and()
                    .antMatcher("/registration")
                .anonymous()
                .and()
                .authorizeHttpRequests()
                    .anyRequest()
                    .authenticated()
                .and()
                .httpBasic()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
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
