package com.srsoft.modapimanager.security.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Lazy;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.srsoft.modapimanager.entity.User;
import com.srsoft.modapimanager.repository.UserRepository;
import com.srsoft.modapimanager.security.JwtService;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtService jwtService;
	private final UserDetailsService userDetailsService;
	private final UserRepository userRepository;


	public JwtAuthenticationFilter(JwtService jwtService, 
			@Lazy UserDetailsService userDetailsService, UserRepository userRepository) {
		this.jwtService = jwtService;
		this.userDetailsService = userDetailsService;
		this.userRepository = userRepository;
	}

	@Override
	protected void doFilterInternal(
			@NonNull HttpServletRequest request,
			@NonNull HttpServletResponse response,
			@NonNull FilterChain filterChain
			) throws ServletException, IOException {
		final String authHeader = request.getHeader("Authorization");
		final String jwt;
		final String username;


		if (authHeader == null || !authHeader.startsWith("Bearer")) {
			filterChain.doFilter(request, response);
			return;
		}

		jwt = authHeader.substring(7);
		try
		{
			username = jwtService.extractUsername(jwt);
			if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
				UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
				User user= userRepository.findByUsername(username)
						.orElseThrow(() -> new UsernameNotFoundException("Utente non trovato: " + username));
				if (jwtService.isTokenValid(jwt, userDetails,user)) {
					UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
							userDetails,
							null,
							userDetails.getAuthorities()
							);
					authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(authToken);
				}
				else {
					log.info("token non valido " +jwt) ;
				}
			}
			filterChain.doFilter(request, response);}
		catch (Exception e) {
			// Gestisci qui tutte le eccezioni JWT
			log.error("Errore durante la validazione del token: {}", e.getMessage());

			// Crea una risposta di errore
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("application/json");

			// Costruisci il messaggio di errore
			Map<String, Object> errorDetails = new HashMap<>();
			errorDetails.put("timestamp", new Date());
			errorDetails.put("status", HttpServletResponse.SC_UNAUTHORIZED);
			errorDetails.put("error", "Unauthorized");
			errorDetails.put("message", "Token non valido o scaduto");
			errorDetails.put("path", request.getRequestURI());

			// Scrivi la risposta JSON
			ObjectMapper mapper = new ObjectMapper();
			mapper.writeValue(response.getOutputStream(), errorDetails);
		}
	}













}