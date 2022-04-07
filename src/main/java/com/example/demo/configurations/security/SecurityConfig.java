package com.example.demo.configurations.security;

import com.example.demo.configurations.security.filter.CustomAuthenticationFilter;
import com.example.demo.configurations.security.filter.CustomAuthorizationFilter;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.authorizeRequests().antMatchers("/login").permitAll();
//        http.authorizeRequests().antMatchers("/users").permitAll();
        http.authorizeRequests().antMatchers(HttpMethod.GET, "/users", "/users/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN");
        http.authorizeRequests().antMatchers(HttpMethod.GET, "/roles/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN");
        http.authorizeRequests().antMatchers(HttpMethod.POST, "/users/**").permitAll();
        http.authorizeRequests().antMatchers(HttpMethod.POST, "/roles/**").hasAnyAuthority("ROLE_ADMIN");
        http.authorizeRequests().antMatchers(HttpMethod.PUT, "/users/**").hasAnyAuthority("ROLE_ADMIN");
        http.addFilter(new CustomAuthenticationFilter(authenticationManagerBean()));
        http.addFilterBefore(new CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception{
        return super.authenticationManagerBean();
    }
}
