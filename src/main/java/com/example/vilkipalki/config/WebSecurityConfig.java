package com.example.vilkipalki.config;


import com.example.vilkipalki.security.JwtFilter;
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

@Configuration
@EnableWebSecurity
@Log
@SuppressWarnings("unused")
public class WebSecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {

        log.info("starting api filter chain");

        http.csrf().disable()
                .httpBasic().disable()
                        .formLogin().disable();

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/**").permitAll());

//        http.csrf().disable()
//                        .authorizeHttpRequests()
//                                .requestMatchers("/auth", "/login").permitAll()
//                        .requestMatchers("/**").authenticated()
//                        .and().formLogin()
//                        .loginPage("/login")
//                .defaultSuccessUrl("/admin", true)
//                .failureUrl("/login?error=true")
//                .and()
//                .logout()
//                .logoutUrl("/logout")
//                .deleteCookies("JSESSIONID");

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


//    @Bean
//    public SecurityFilterChain adminPanelFilterChain(HttpSecurity http) throws Exception {

//        log.info("starting admin panel filter chain");
//
//        http.csrf().disable().httpBasic().disable();
//
//        http
//            .authorizeHttpRequests(authorize -> authorize
//                    .anyRequest().authenticated()
//            )
//            .formLogin()
//            .loginPage("/login")
//            .defaultSuccessUrl("/admin", true)
//            .failureUrl("/login?error=true")
//            .and()
//            .logout()
//            .logoutUrl("/logout")
//            .deleteCookies("JSESSIONID");

//        http.csrf()
//                .disable()
//                .authorizeHttpRequests(auth -> {
//                        try {
//                            auth.requestMatchers("/admin/**")
//                                .hasAnyRole("ADMIN", "USER")
//                                .requestMatchers("/login*").permitAll()
//                                .anyRequest()
//                                .authenticated()
//                                    .and()
//                                .formLogin()
//                                .loginPage("/login")
//                                .defaultSuccessUrl("/admin", true)
//                                .failureUrl("/login?error=true")
//                                    .and()
//                                .logout()
//                                .logoutUrl("/logout")
//                                .deleteCookies("JSESSIONID");
//                        } catch (Exception e) {
//                            throw new RuntimeException(e);
//                        }
//                    }
//                );

//        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);

//        return http.build();
//    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
