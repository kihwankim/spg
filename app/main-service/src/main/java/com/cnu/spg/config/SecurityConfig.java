package com.cnu.spg.config;

import com.cnu.spg.handler.UserAuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService; // register servic class

    @Autowired
    UserAuthenticationSuccessHandler userAuthenticationSuccessHandler;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(this.userDetailsService).passwordEncoder(this.bCryptPasswordEncoder());
    }

    @Bean
    public AuthenticationManager customAuthenticationManager() throws Exception {
        return authenticationManager();
    }

    @Override
    public void configure(WebSecurity webSecurity) {
        webSecurity.ignoring()
                .antMatchers(
                        "/resources/**",
                        "/static/**",
                        "/css/**",
                        "/fonts/**",
                        "/js/**",
                        "/fonts/**",
                        "/img_beom/**",
                        "/img/**")
                .antMatchers(
                        "/v3/api-docs",
                        "/configuration/**",
                        "/swagger-resources/**",
                        "/configuration/security",
                        "/swagger-ui.html",
                        "/swagger*/**",
                        "/webjars/**")
                .antMatchers("/h2-console/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/")
                .permitAll()
                .antMatchers("/register/**")
                .permitAll()
                .antMatchers("/board/**")
                .hasAnyRole("ADMIN", "STUDENT")
                .antMatchers("/admin/**")
                .hasRole("ADMIN")
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                .loginPage("/login/signInPage")
                .loginProcessingUrl("/authenticateTheUser")
                .successHandler(this.userAuthenticationSuccessHandler)
                .failureHandler((httpServletRequest, httpServletResponse, e) -> {
                    httpServletRequest.setAttribute("username", httpServletRequest.getParameter("username"));
                    httpServletRequest.getRequestDispatcher("/login/signInPage").forward(httpServletRequest, httpServletResponse);
                })
                .permitAll()
                .and()
                .logout()
                .logoutSuccessUrl("/")
//                .deleteCookies("JSESSIONID")
//                .invalidateHttpSession(true)
                .permitAll()
                .and()
                .exceptionHandling()
                .accessDeniedPage("/accessDenied");

        http.sessionManagement()
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
                .expiredUrl("/login/signInPage");
    }
}
