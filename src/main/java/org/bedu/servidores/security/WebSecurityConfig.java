package org.bedu.servidores.security;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@AllArgsConstructor
public class WebSecurityConfig {
    private final UserDetailsService userDetailsService;
    private final JWTAuthorizationFilter jwtAuthorizationFilter;

    @Bean
    SecurityFilterChain  filterChain(HttpSecurity http, AuthenticationManager authManager) throws Exception{
        //Autenticacion Basic
        /*return http.csrf().disable().authorizeRequests().anyRequest().authenticated().and().
                httpBasic().and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).
                and().build();*/
        //Autenticacion por JWT
        JWTAuthenticationFilter jwtAuthenticationFilter = new JWTAuthenticationFilter();
        jwtAuthenticationFilter.setAuthenticationManager(authManager);
        jwtAuthenticationFilter.setFilterProcessesUrl("/login");

        return http.cors(Customizer.withDefaults()).
                csrf().disable().authorizeRequests().anyRequest().authenticated().and().
                httpBasic().and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).
                and().addFilter(jwtAuthenticationFilter).
                addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class).build();

    }

    @Bean
    CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration conf = new CorsConfiguration();
        conf.setAllowedOrigins(java.util.Arrays.asList("*"));
        conf.setAllowedMethods(java.util.Arrays.asList("*"));
        conf.setAllowedHeaders(java.util.Arrays.asList("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",conf);
        return source;
    }

   /* @Bean
    public UserDetailsService userDetailsService(){
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.withUsername("admin").
                password(passwordEncoder().encode("admin")).
                roles("ADMIN").build());
        return manager;
    }*/

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authManager(HttpSecurity http) throws Exception{
        return http.getSharedObject(AuthenticationManagerBuilder.class).
                userDetailsService(userDetailsService).
                passwordEncoder(passwordEncoder()).
                and().build();
    }

}

