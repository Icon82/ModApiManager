package com.srsoft.modorder25.controller;

import com.srsoft.modorder25.dto.AuthRequest;
import com.srsoft.modorder25.dto.AuthResponse;
import com.srsoft.modorder25.dto.UserRequest;
import com.srsoft.modorder25.dto.UserResponse;
import com.srsoft.modorder25.dto.ErrorResponse;
import com.srsoft.modorder25.entity.User;
import com.srsoft.modorder25.security.JwtService;
import com.srsoft.modorder25.service.LoginTrackingService;
import com.srsoft.modorder25.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.srsoft.modorder25.config.SecurityRoles;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "01.Autenticazione", description = "API per la registrazione e l'autenticazione degli utenti")
public class AuthController {
	private final AuthenticationManager authenticationManager;
	private final UserService userService;
	private final JwtService jwtService;
	private final LoginTrackingService loginTrackingService;

	@Operation(summary = "01.Registra un nuovo utente", 
			description = "Crea un nuovo utente e restituisce i dettagli dell'utente registrato")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Utente registrato con successo"),
		@ApiResponse(responseCode = "403", description = "Operazione non consentita"),
		@ApiResponse(responseCode = "500", description = "Errore interno del server")
	})
	@PostMapping("/register")
	@PreAuthorize(SecurityRoles.HAS_ANY_ADMIN)
	public ResponseEntity<UserResponse> register(
			@Parameter(description = "Dati dell'utente per la registrazione", required = true, content = @Content(schema = @Schema(implementation = UserRequest.class)))
			@Valid @RequestBody UserRequest request) {
		log.info("Ricevuta richiesta di registrazione per username: {}", request.getUsername());
		return ResponseEntity.ok(userService.createUser(request));
	}


	@PostMapping("/login")
	@Operation(summary = "02.Autenticazione utente", 
	description = "Verifica le credenziali e restituisce un token di accesso JWT insieme ai ruoli dell'utente")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Autenticazione riuscita"),
		@ApiResponse(responseCode = "401", description = "Credenziali non valide"),
		@ApiResponse(responseCode = "500", description = "Errore interno del server")
	})
	public ResponseEntity<?> login(@Valid @RequestBody AuthRequest request) {
		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
					);

			UserDetails userDetails = userService.loadUserByUsername(request.getUsername());
			/*    String token = jwtService.generateToken(userDetails);*/

			User user = userService.findByUsername(request.getUsername());
			AuthResponse authResp =jwtService.generateTokenExp(Map.of(), userDetails,user);
			loginTrackingService.trackSuccessfulLogin(user);



			return ResponseEntity.ok(authResp);


		} catch (BadCredentialsException e) {
			// Gestione specifica per credenziali errate
			log.error("Credenziali non valide per username: {}", request.getUsername());
			User user = userService.findByUsername(request.getUsername());
			if (user != null) {
				loginTrackingService.trackFailedLogin(user, "Credenziali non valide");
			}
			return ResponseEntity
					.status(HttpStatus.UNAUTHORIZED)
					.body(new ErrorResponse("Credenziali non valide", LocalDateTime.now()));

		} catch (Exception e) {
			// Altre eccezioni
			log.error("Errore durante il login per username: {}", request.getUsername(), e);
			User user = userService.findByUsername(request.getUsername());
			if (user != null) {
				loginTrackingService.trackFailedLogin(user, e.getMessage());
			}
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ErrorResponse("Errore durante l'autenticazione", LocalDateTime.now()));
		}

	}

	@PostMapping("/logout")
	@PreAuthorize(SecurityRoles.HAS_ANY_STANDARD_USER)
	public ResponseEntity<Map<String, String>> logout(HttpServletRequest request) {
		String authHeader = request.getHeader("Authorization");
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			String token = authHeader.substring(7);
			jwtService.invalidateToken(token);
		}

		return ResponseEntity.ok(Collections.singletonMap("message", "Logout effettuato con successo"));
	}



	@PostMapping("/renew")
	@Operation(summary = "03.Rinnova il token", 
	description = "Verifica la validità del token e lo estende")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Autenticazione riuscita"),
		@ApiResponse(responseCode = "401", description = "Credenziali non valide"),
		@ApiResponse(responseCode = "500", description = "Errore interno del server")
	})
	@PreAuthorize(SecurityRoles.HAS_ANY_STANDARD_USER)
	public ResponseEntity<?> renewToken(@RequestHeader("Authorization") String authHeader) {
		try {

			String token = authHeader.substring(7);
			User user = userService.getCurrentUser();
			UserDetails userDetails = userService.loadUserByUsername(user.getUsername());
			AuthResponse response= jwtService.renewToken(token,userDetails, user );

			return ResponseEntity.ok(response);
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("error", "Token non valido"));
		}
	}



}