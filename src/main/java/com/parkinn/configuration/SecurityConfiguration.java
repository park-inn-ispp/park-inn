package com.parkinn.configuration;

import java.util.Arrays;

import javax.sql.DataSource;

import com.parkinn.filter.JWTAuthenticationFilter;
import com.parkinn.filter.JWTAuthorizationFilter;
import com.parkinn.service.CustomUserDetailsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * @author japarejo
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)

public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired
    private CustomUserDetailsService UserDetailsService;

    @Bean
    BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

	@Autowired
	DataSource dataSource;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers(HttpMethod.POST, "/api/**").permitAll()
				.anyRequest().authenticated()
				.and()
				.addFilter(new JWTAuthenticationFilter(authenticationManager()))
                .addFilter(new JWTAuthorizationFilter(authenticationManager()))
                // this disables session creation on Spring Security
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); 
                // Configuración para que funcione la consola de administración 
                // de la BD H2 (deshabilitar las cabeceras de protección contra
                // ataques de tipo csrf y habilitar los framesets si su contenido
                // se sirve desde esta misma página.

                http.csrf().ignoringAntMatchers("/h2-console/**");
                http.headers().frameOptions().sameOrigin();
				http.cors().configurationSource(corsConfigurationSource()).and().csrf().disable();

	}

	@Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(UserDetailsService)
                .passwordEncoder(passwordEncoder());
    }
	

	@Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

	    

	// @Override
	// public void configure(AuthenticationManagerBuilder auth) throws Exception {
	// 	auth.jdbcAuthentication()
	//       .dataSource(dataSource)
	//       .usersByUsernameQuery(
	//        "select username,password,enabled "
	//         + "from users "
	//         + "where username = ?")
	//       .authoritiesByUsernameQuery(
	//        "select username, authority "
	//         + "from authorities "
	//         + "where username = ?")	      	      
	//       .passwordEncoder(passwordEncoder());	
	// }
	
	
	//@Bean
	//public PasswordEncoder passwordEncoder() {	    
	//	PasswordEncoder encoder =  NoOpPasswordEncoder.getInstance();
	  //  return encoder;
	//}
	
	@Bean
	CorsConfigurationSource corsConfigurationSource() {
    final CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList("https://park-inn-ispp-fe.herokuapp.com/"));
    configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
    configuration.setAllowCredentials(true);
    configuration.setAllowedHeaders(Arrays.asList("*"));
    configuration.setExposedHeaders(Arrays.asList("X-Auth-Token", "Authorization", "Access-Control-Allow-Origin",
            "Access-Control-Allow-Credentials"));
    final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
}
}
