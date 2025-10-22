
package com.srsoft.modorder25.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;

import org.springframework.beans.factory.annotation.Value;
import com.srsoft.modorder25.security.filters.JwtAuthenticationFilter;
import com.srsoft.modorder25.security.filters.RateLimitFilter;
import com.srsoft.modorder25.security.filters.RequestCachingFilter;
import com.srsoft.modorder25.security.filters.SecurityHeadersFilter;
import com.srsoft.modorder25.security.filters.InputValidationFilter;
import java.io.IOException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Slf4j
public class SecurityConfig {
	private final RateLimitFilter rateLimitFilter;
	private final SecurityHeadersFilter securityHeadersFilter;
	private final InputValidationFilter inputValidationFilter;

	@Value("${cors.allowed-origins}")
	private String allowedOriginsStr;




	public SecurityConfig(
			SecurityHeadersFilter securityHeadersFilter,		
			RateLimitFilter rateLimitFilter , InputValidationFilter inputValidationFilter) {
		this.securityHeadersFilter = securityHeadersFilter;
		this.inputValidationFilter = inputValidationFilter;
		this.rateLimitFilter = rateLimitFilter;
	}

	@Bean
	public     PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(11);
	}

	@Bean
	public   AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

	@Bean
	public   SecurityFilterChain securityFilterChain(HttpSecurity http, 
			JwtAuthenticationFilter jwtAuthFilter,
			RequestCachingFilter requestCachingFilter,
			UserDetailsService userDetailsService) throws Exception {

		// Aggiungi il RequestCachingFilter per primo
		http.addFilterBefore(requestCachingFilter, UsernamePasswordAuthenticationFilter.class);

		// Poi aggiungi gli altri filtri in ordine
		http.addFilterAfter(inputValidationFilter, RequestCachingFilter.class);
		http.addFilterAfter(securityHeadersFilter, InputValidationFilter.class);
		http.addFilterAfter(rateLimitFilter, SecurityHeadersFilter.class);
		http.addFilterAfter(jwtAuthFilter, RateLimitFilter.class);                            

		// loggare tutte le richieste POST
		http.addFilterBefore(new OncePerRequestFilter() {
			@Override
			protected void doFilterInternal(HttpServletRequest request, 
					HttpServletResponse response, 
					FilterChain filterChain) throws ServletException, IOException {
				if ("POST".equalsIgnoreCase(request.getMethod())) {
					log.info("Ricevuta richiesta POST a: {}", request.getRequestURI());
					log.info("Headers: {}", Collections.list(request.getHeaderNames())
							.stream()
							.map(headerName -> {
								if (headerName.equalsIgnoreCase("Authorization") || 
										headerName.equalsIgnoreCase("Cookie") || 
										headerName.contains("token")) {
									return headerName + ": [REDACTED]";
								} else {
									return headerName + ": " + request.getHeader(headerName);
								}
							})

							.collect(Collectors.joining(", ")));
				}



				filterChain.doFilter(request, response);
			}
		}, UsernamePasswordAuthenticationFilter.class);


		/*	 http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);*/


		http
		.cors(cors -> cors.configurationSource(corsConfigurationSource()))
		.csrf(csrf -> csrf.disable())
		.authorizeHttpRequests(auth -> auth
				//Non Autenticati
				.requestMatchers("/test/**").permitAll()  
				.requestMatchers("/auth/login").permitAll()
				.requestMatchers("/error").permitAll()
				.requestMatchers("/swagger-api/**").permitAll()
				.requestMatchers("/internal-doc/**", "/swagger-ui/**").permitAll()
				.requestMatchers("/v3/api-docs/**").permitAll()
								
				// Solo Autenticati
				.requestMatchers("/auth/renew").authenticated()
				.requestMatchers("/auth/logout").authenticated()
				.requestMatchers("/auth/register").authenticated()
				.requestMatchers("/file/storage/**").authenticated()	      
				.requestMatchers("/parametri/**").authenticated()	 
				.anyRequest().authenticated()
				)
		.sessionManagement(session -> session
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				)
		.userDetailsService(userDetailsService);
		return http.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		return request -> {
			CorsConfiguration config = new CorsConfiguration();
			String origin = request.getHeader("Origin");

			log.info("Richiesta CORS ricevuta da origine: {}", origin);
			List<String> allowedOrigins = Arrays.asList(allowedOriginsStr.split(",") );

			if (origin != null && allowedOrigins.contains(origin)) {
				config.setAllowedOrigins(Arrays.asList(origin));
				log.info("Origine Consentita: {}", origin);
			} else {
				log.warn("Origine non  Consentita: {}", origin);
				return null;
			}

			config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
			config.setAllowCredentials(false);
			config.setAllowedHeaders(Arrays.asList(
					"Authorization",
					"Content-Type",
					"X-Requested-With"
					));
			config.setMaxAge(3600L);
			config.setExposedHeaders(Arrays.asList("Content-Disposition"));
			log.debug("Metodi permessi: {}", config.getAllowedMethods());
			log.debug("Headers permessi: {}", config.getAllowedHeaders());

			return config;
		};

	}
}
