package com.srsoft.modorder25.controller;

import com.srsoft.modorder25.dto.UserRequest;
import com.srsoft.modorder25.dto.UserResponse;
import com.srsoft.modorder25.dto.PasswordChangeRequest;
import com.srsoft.modorder25.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Set;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "02.Manage Users ", description = "API per la Gestione degli Utenti")
public class UserController {
    private final UserService userService;
	@Operation(summary = "01.Info sull'utente", 
			description = "Restituisce i dettagli dell'utente loggato")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Informazioni utente ok"),
		@ApiResponse(responseCode = "403", description = "Operazione non consentita"),
		@ApiResponse(responseCode = "500", description = "Errore interno del server")
	})
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {
        UserResponse user = UserResponse.fromEntity(userService.getCurrentUser());
        return ResponseEntity.ok(user);
    }

	@Operation(summary = "02.Info sugli Utenti", 
			description = "Restituisce i dettagli di tutti gli utenti")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Informazioni utenti ok"),
		@ApiResponse(responseCode = "403", description = "Operazione non consentita"),
		@ApiResponse(responseCode = "500", description = "Errore interno del server")
	})
    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER','ROLE_DEVELOP')")
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }
	
	@Operation(summary = "03.Info su Utente specifico", 
			description = "Restituisce i dettagli di tutti gli utenti")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Informazioni utente ok"),
		@ApiResponse(responseCode = "403", description = "Operazione non consentita"),
		@ApiResponse(responseCode = "500", description = "Errore interno del server")
	})
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER','ROLE_DEVELOP') or #id == authentication.principal.id")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }
	
	@Operation(summary = "04.Aggiorna Utente", 
			description = "Aggiorna i dati di un utente")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Aggiornamento eseguito"),
		@ApiResponse(responseCode = "403", description = "Operazione non consentita"),
		@ApiResponse(responseCode = "500", description = "Errore interno del server")
	})
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER','ROLE_DEVELOP') or #id == authentication.principal.id")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

	@Operation(summary = "05.Elimina Utente", 
			description = "Restituisce i dettagli di tutti gli utenti")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Informazioni utenti ok"),
		@ApiResponse(responseCode = "403", description = "Operazione non consentita"),
		@ApiResponse(responseCode = "500", description = "Errore interno del server")
	})
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER','ROLE_DEVELOP') or #id == authentication.principal.id")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
	
	
	@Operation(summary = "06.Info sugli Utenti", 
			description = "Restituisce i dettagli di tutti gli utenti")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Informazioni utenti ok"),
		@ApiResponse(responseCode = "403", description = "Operazione non consentita"),
		@ApiResponse(responseCode = "500", description = "Errore interno del server")
	})
    @PostMapping("/{id}/roles/{roleName}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER','ROLE_DEVELOP')")
    public ResponseEntity<Void> addRoleToUser(
            @PathVariable Long id,
            @PathVariable String roleName) {
        userService.addRoleToUser(id, roleName);
        return ResponseEntity.noContent().build();
    }
	@Operation(summary = "07.Info sugli Utenti", 
			description = "Restituisce i dettagli di tutti gli utenti")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Informazioni utenti ok"),
		@ApiResponse(responseCode = "403", description = "Operazione non consentita"),
		@ApiResponse(responseCode = "500", description = "Errore interno del server")
	})
    @GetMapping("/{id}/roles")
    @PreAuthorize("hasRole('ROLE_ADMIN', 'ROLE_MANAGER','ROLE_DEVELOP') or #id == authentication.principal.id")
    public ResponseEntity<Set<String>> getUserRoles(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserRoles(id));
    }
	
	@Operation(summary = "08.Info sugli Utenti", 
			description = "Restituisce i dettagli di tutti gli utenti")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Informazioni utenti ok"),
		@ApiResponse(responseCode = "403", description = "Operazione non consentita"),
		@ApiResponse(responseCode = "500", description = "Errore interno del server")
	})
    @PostMapping("/{id}/change-password")
    @PreAuthorize("hasRole('ROLE_ADMIN', 'ROLE_MANAGER','ROLE_DEVELOP') or #id == authentication.principal.id")
    public ResponseEntity<Void> changePassword(
            @PathVariable Long id,
            @Valid @RequestBody PasswordChangeRequest request) {
        userService.changePassword(id, request.getOldPassword(), request.getNewPassword());
        return ResponseEntity.noContent().build();
    }
}