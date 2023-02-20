package com.example.vilkipalki2.config;


import com.example.vilkipalki2.security.JwtFilter;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@Log
@SuppressWarnings("unused")
public class WebSecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    @Order(1)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {

        log.info("starting api filter chain");

        http.securityMatcher("/api/**").csrf().disable()
                        .authorizeHttpRequests()
                        .requestMatchers("/api/register", "/api/auth").permitAll()
                        .requestMatchers("/api/v3/items/**").permitAll()
                        .requestMatchers("/api/v3/ingredients").permitAll()
                        .requestMatchers("/api/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                        .and()
                                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    @Bean
    public SecurityFilterChain adminPanelFilterChain(HttpSecurity http) throws Exception {

        log.info("starting admin panel filter chain");

        http.csrf().disable()
                .authorizeHttpRequests()
                        .requestMatchers("/admin").hasRole("ADMIN")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/resources/**").permitAll()
                        .requestMatchers("/images/**").permitAll()
                .anyRequest().authenticated()
                        .and()
                                .formLogin()
                                        .loginPage("/login")
                                        .defaultSuccessUrl("/admin", true)
                                        .failureUrl("/login?error=true")
                        .and()
                                .logout()
                                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                                        .logoutSuccessUrl("/login?logout=true").deleteCookies("JSESSIONID")
                                        .invalidateHttpSession(true);

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);

        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
