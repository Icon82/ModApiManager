package com.srsoft.modapimanager.security.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.srsoft.modapimanager.config.CachedBodyHttpServletRequest;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.springframework.http.MediaType;

@Component
@Slf4j
public class InputValidationFilter extends OncePerRequestFilter {

	// Pattern più precisi per SQL Injection
	private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
			"(?i)\\b(select\\s+\\*|select.+?from|insert\\s+into|update.+?set|delete\\s+from|drop\\s+table|" +
					"alter\\s+table|exec\\s+\\w+|xp_\\w+|declare\\s+@|cast\\s*\\(|union\\s+select|" +
					"waitfor\\s+delay|sp_executesql)\\b|.*(\\s|')--.*|.*;.*--|.*'\\s*or\\s*'.*'\\s*=\\s*'.*'",
					Pattern.CASE_INSENSITIVE
			);

	// Pattern migliorato per XSS
	private static final Pattern XSS_PATTERN = Pattern.compile(
			"(?i)(<script[^>]*>.*?</script>|javascript:|vbscript:|expression\\s*\\(|" +
					"<iframe|onload\\s*=|onerror\\s*=|onclick\\s*=|onmouseover\\s*=|" +
					"eval\\s*\\(|document\\.cookie|document\\.write|window\\.location|" +
					"\\+ADw-script|\\\\u003C|\\\\\\\"><)",
					Pattern.CASE_INSENSITIVE
			);

	// Pattern migliorato per Path Traversal
	private static final Pattern PATH_TRAVERSAL_PATTERN = Pattern.compile(
			"(?i)(\\.\\.(\\\\|\\/)|%2e%2e%2f|%252e%252e%252f|%uff0e%uff0e/)",
			Pattern.CASE_INSENSITIVE
			);

	// Pattern per Command Injection
	private static final Pattern COMMAND_INJECTION_PATTERN = Pattern.compile(
			/*"(?i)(\\s*\\|\\s*\\w+|\\s*;\\s*\\w+\\s*|\\s*&&\\s*\\w+|`.*?`)",*/
			"(?i)(\\s*\\|\\s*\\w+|\\s*&&\\s*\\w+|`.*?`)",
			Pattern.CASE_INSENSITIVE
			);

	// Limite dimensione del corpo della richiesta
	@Value("${input.filter.max.body}")
	private  int MAX_BODY_SIZE ;
	
	@Value("${input.filter.max.file}")
	private  int MAX_FILE_SIZE ;
	
	// Headers da escludere dalla validazione
	private static final Set<String> EXCLUDED_HEADERS = new HashSet<>(Arrays.asList(
			"user-agent", "accept", "accept-language", "accept-encoding", 
	        "connection", "host", "referer", "content-length", "content-type",
	        "cache-control", "pragma", "sec-ch-ua", "sec-ch-ua-mobile", 
	        "sec-ch-ua-platform", "sec-fetch-site", "sec-fetch-mode", 
	        "sec-fetch-dest", "sec-fetch-user", "upgrade-insecure-requests",
	        "origin", "if-none-match", "if-modified-since","x-railway-request-id"
			));

	// Percorsi da escludere dalla validazione
	private static final Set<String> EXCLUDED_PATHS = new HashSet<>(Arrays.asList(
			"/public/", "/swagger", "/api-docs", "/v3/api-docs", "/webjars/", 
			"/actuator/", "/error", "/favicon.ico", "/srsoft-documentation/swagger-ui","swagger-ui"
			));

	@Override
	protected void doFilterInternal(
			HttpServletRequest request,
			HttpServletResponse response,
			FilterChain filterChain) throws ServletException, IOException {

		// Verifica se la richiesta è già un'istanza di CachedBodyHttpServletRequest
		if (!(request instanceof CachedBodyHttpServletRequest)) {
			log.debug("La richiesta non è stata wrappata dal RequestCachingFilter");
		} else {
			log.debug("La richiesta è stata correttamente wrappata dal RequestCachingFilter");
		}

		try {
			// Verifica il content type
			String contentType = request.getContentType();



			if (contentType != null && request.getContentLength() > 0) {
				// Limiti diversi per diversi tipi di contenuto
				int applicableLimit = MAX_BODY_SIZE;
				log.info("limite 1: " + applicableLimit);

				if (contentType.startsWith(MediaType.MULTIPART_FORM_DATA_VALUE)) {
					// Limite più alto per upload di file (50MB)
					applicableLimit = MAX_FILE_SIZE;
					log.info("limite 2: " + applicableLimit);
				}
				log.info("limite 3: " + applicableLimit);
				if (request.getContentLength() > applicableLimit) {
					log.warn("Dimensione del corpo della richiesta troppo grande: {} bytes", request.getContentLength());
					handleMaliciousRequest(response, "La dimensione del corpo della richiesta supera il limite consentito", 
							HttpStatus.PAYLOAD_TOO_LARGE);
					return;
				}


				if (contentType.startsWith(MediaType.APPLICATION_JSON_VALUE)) {
					// Per richieste JSON
					String body;

					if (request instanceof CachedBodyHttpServletRequest) {
						CachedBodyHttpServletRequest cachedRequest = (CachedBodyHttpServletRequest) request;
						body = new String(cachedRequest.getCachedBody(), StandardCharsets.UTF_8);
						log.debug("Utilizzando corpo della richiesta JSON dalla cache");
					} else {
						log.warn("Leggendo il corpo della richiesta senza cache (potrebbe causare problemi)");
						body = request.getReader().lines().collect(Collectors.joining());
					}

					Map<String, String> validationResult = validateContent(body);
					if (!validationResult.isEmpty()) {
						handleMaliciousRequest(response, 
								"Contenuto malevolo rilevato nel body: " + validationResult.get("type"), 
								HttpStatus.BAD_REQUEST);
						return;
					}
				} else if (contentType.startsWith(MediaType.APPLICATION_FORM_URLENCODED_VALUE)) {
					// Per richieste form-urlencoded, i parametri sono già analizzati da Spring
					// e saranno controllati nel blocco successivo
					log.debug("Validazione form-urlencoded verrà eseguita tramite i parametri");
				} else if (contentType.startsWith(MediaType.MULTIPART_FORM_DATA_VALUE)) {
					// Per richieste multipart/form-data (upload di file), controllo superficiale
					// Nota: una validazione approfondita richiederebbe un parser multipart personalizzato
					log.debug("Richiesta multipart/form-data rilevata, controllo parametri");
				}
			}

			// Verifica i parametri della query string
			Map<String, String[]> parameterMap = request.getParameterMap();
			for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
				String paramName = entry.getKey();
				String[] values = entry.getValue();

				for (String value : values) {
					Map<String, String> validationResult = validateContent(value);
					if (!validationResult.isEmpty()) {
						log.warn("Rilevato parametro sospetto: {} con valore: {}", paramName, value);
						handleMaliciousRequest(response, 
								"Contenuto malevolo rilevato nel parametro '" + paramName + "': " + 
										validationResult.get("type"), 
										HttpStatus.BAD_REQUEST);
						return;
					}
				}
			}

			// Verifica gli headers (escludendo quelli comuni)
			Enumeration<String> headerNames = request.getHeaderNames();
			while (headerNames.hasMoreElements()) {
				String headerName = headerNames.nextElement();

				// Salta headers nella lista di esclusione
				if (EXCLUDED_HEADERS.contains(headerName.toLowerCase())) {
					continue;
				}

				String headerValue = request.getHeader(headerName);
				Map<String, String> validationResult = validateContent(headerValue);

				if (!validationResult.isEmpty()) {
					log.warn("Header sospetto: {} con valore: {}", headerName, headerValue);
					handleMaliciousRequest(response, 
							"Contenuto malevolo rilevato nell'header '" + headerName + "': " + 
									validationResult.get("type"), 
									HttpStatus.BAD_REQUEST);
					return;
				}
			}

			// Verifica il path
			String requestPath = request.getRequestURI();
			Map<String, String> validationResult = validateContent(requestPath);

			if (!validationResult.isEmpty()) {
				log.warn("Path sospetto: {}", requestPath);
				handleMaliciousRequest(response, 
						"Contenuto malevolo rilevato nel path: " + validationResult.get("type"), 
						HttpStatus.BAD_REQUEST);
				return;
			}

			// Se tutto è ok, procedi con la richiesta
			filterChain.doFilter(request, response);

		} catch (Exception e) {
			log.error("Errore durante la validazione dell'input", e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			response.getWriter().write("{\"error\": \"Errore durante l'elaborazione della richiesta\"}");
		}
	}

	/**
	 * Valida il contenuto rispetto a vari pattern di attacco
	 * @param value Il valore da validare
	 * @return Mappa vuota se il contenuto è sicuro, altrimenti contiene il tipo di attacco rilevato
	 */
	private Map<String, String> validateContent(String value) {
		Map<String, String> result = new HashMap<>();

		if (value == null) {
			return result;
		}

		// Verifica ogni tipo di pattern e restituisce informazioni specifiche sul tipo di attacco
		if (SQL_INJECTION_PATTERN.matcher(value).find()) {
			result.put("type", "SQL Injection");
			result.put("pattern", "sql");
			return result;
		}

		if (XSS_PATTERN.matcher(value).find()) {
			result.put("type", "Cross-Site Scripting");
			result.put("pattern", "xss");
			return result;
		}

		if (PATH_TRAVERSAL_PATTERN.matcher(value).find()) {
			result.put("type", "Path Traversal");
			result.put("pattern", "path");
			return result;
		}

		if (COMMAND_INJECTION_PATTERN.matcher(value).find()) {
			result.put("type", "Command Injection");
			result.put("pattern", "command");
			return result;
		}

		return result;
	}

	/**
	 * Gestisce una richiesta rilevata come malevola
	 */
	private void handleMaliciousRequest(HttpServletResponse response, String message, HttpStatus status) throws IOException {
		log.warn("Rilevato tentativo di attacco: {}", message);
		response.setStatus(status.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.getWriter().write("{\"error\": \"" + message + "\"}");
	}

	/**
	 * Versione semplificata per retrocompatibilità
	 */
	private void handleMaliciousRequest(HttpServletResponse response, String message) throws IOException {
		handleMaliciousRequest(response, message, HttpStatus.BAD_REQUEST);
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		String path = request.getServletPath();

		// Controlla se il percorso inizia con uno dei prefissi esclusi
		for (String excludedPath : EXCLUDED_PATHS) {
			if (path.startsWith(excludedPath) || path.contains(excludedPath)) {
				return true;
			}
		}

		return false;
	}
}