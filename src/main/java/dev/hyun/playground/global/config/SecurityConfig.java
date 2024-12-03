package dev.hyun.playground.global.config;

import dev.hyun.playground.domain.account.service.OAuth2AccountService;
import dev.hyun.playground.global.security.filter.ExceptionHandlerFilter;
import dev.hyun.playground.global.security.filter.JwtAuthenticationFilter;
import dev.hyun.playground.global.security.handler.AccessDeniedHandlerImpl;
import dev.hyun.playground.global.security.handler.AuthenticationEntryPointImpl;
import dev.hyun.playground.global.security.handler.LogoutSuccessHandlerImpl;
import dev.hyun.playground.global.security.handler.OAuth2FailureHandler;
import dev.hyun.playground.global.security.handler.OAuth2SuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ExceptionHandlerFilter exceptionHandlerFilter;
    private final AuthenticationEntryPointImpl authenticationEntryPointImpl;
    private final AccessDeniedHandlerImpl accessDeniedHandlerImpl;
    private final LogoutSuccessHandlerImpl logoutSuccessHandlerImpl;
    private final OAuth2AccountService oAuth2AccountService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final OAuth2FailureHandler oAuth2FailureHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web -> web.ignoring().requestMatchers(
                "/swagger-ui/**", "/api-docs/**", "/error", "/resources/static/**"));
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        httpSecurity
                .authorizeHttpRequests(authorizeRequest -> authorizeRequest
                        .requestMatchers("/socialLogin.html").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/api/account/**").permitAll()
                        .requestMatchers("/login/oauth2/code/**").permitAll()
//                        .anyRequest().authenticated());
                        .anyRequest().permitAll());

        httpSecurity
                .oauth2Login(oauth2 -> { oauth2
                        .successHandler(oAuth2SuccessHandler)
                        .failureHandler(oAuth2FailureHandler)
                        .userInfoEndpoint(userInfo -> { userInfo
                                .userService(oAuth2AccountService);
                        });
                });

        httpSecurity
                .logout(logout -> { logout
                        .logoutUrl("/api/sign-out")
                        .logoutSuccessHandler(logoutSuccessHandlerImpl);
                });

        httpSecurity
                .addFilterAt(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(exceptionHandlerFilter, LogoutFilter.class)
                .exceptionHandling(exception -> { exception
                        .authenticationEntryPoint(authenticationEntryPointImpl)
                        .accessDeniedHandler(accessDeniedHandlerImpl);
                });

        return httpSecurity.build();
    }
}
