package com.example.shop.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        log.info("---------------Security Filter Chain---------------");

        http
                .authorizeHttpRequests(
                                        config ->
                                        config.requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                                                .requestMatchers("/", "/members/**", "/item/**").permitAll()
                                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                                .anyRequest().authenticated()
                );

        http
                .csrf(csrf -> csrf.disable())
                .formLogin(
                        form -> form.loginPage("/members/login")
                                .defaultSuccessUrl("/")

                                //login화면에서 name=username이면 생략O
                                //username으로 email 사용하기에 반드시 기입
                                .usernameParameter("email")
                                .failureUrl("/members/login/error")
                )
                .logout(
                        logout -> logout
                                .logoutUrl("/members/logout")
                                .logoutSuccessUrl("/")
                                .invalidateHttpSession(true)    //세션 무효화 (선택 사항이지만 일반적으로 사용)
                                .deleteCookies("JSESSIONID")  //쿠키 삭제 (선택 사항이지만 일반적으로 사용)
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
