package com.srsoft.modorder25.security.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;

@Component
@Slf4j
public class SecurityHeadersFilter extends OncePerRequestFilter {


	@Value("${app.cors.max-age:3600}")
	private String maxAge;

	@Override
	protected void doFilterInternal(
			HttpServletRequest request,
			HttpServletResponse response,
			FilterChain filterChain) throws ServletException, IOException {

		// Headers di sicurezza essenziali
		response.setHeader("Content-Security-Policy", "default-src 'self'; script-src 'self'; object-src 'none'");
		response.setHeader("X-XSS-Protection", "1; mode=block");
		response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Expires", "0");
		response.setHeader("X-Frame-Options", "DENY");
		response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");



		// Gestione delle richieste OPTIONS (preflight)
		if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
			response.setStatus(HttpServletResponse.SC_OK);
			response.setHeader("Access-Control-Max-Age", maxAge);
			return;
		}

		try {
			log.debug("Processing request for URI: {}", request.getRequestURI());
			filterChain.doFilter(request, response);
		} catch (Exception e) {
			log.error("Error occurred while processing request", e);
			throw e;
		}
	}



	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		String path = request.getServletPath();
		// Esclude path specifici se necessario
		return path.startsWith("/public/") || 
				path.startsWith("/resources/") ||
				path.contains("swagger") ||
				path.contains("api-docs");
	}
}