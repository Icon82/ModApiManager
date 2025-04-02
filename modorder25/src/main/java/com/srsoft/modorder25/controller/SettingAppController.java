package com.srsoft.modorder25.controller;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.srsoft.modorder25.config.SecurityRoles;
import com.srsoft.modorder25.dto.SettingAppRequest;
import com.srsoft.modorder25.dto.SettingAppResponse;

import com.srsoft.modorder25.service.SettingAppService;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;




@RestController
@RequestMapping("/parametri")
@Tag(name = "13 - Impostazioni Applicazione ", description = "API Impostazioni Applicazione")
public class SettingAppController {

	@Autowired
	private  SettingAppService settingAppService;        

	@PostMapping("/crypto")
	@PreAuthorize(SecurityRoles.HAS_ANY_ADMIN)
	public ResponseEntity<SettingAppResponse> saveSettingCrypto( @Parameter(description = "Dati Parametro Salvato Cryptato", required = true)  @Valid @RequestBody SettingAppRequest request) {
		SettingAppResponse setting = settingAppService.saveSetting(request);
		return ResponseEntity.ok(setting);
	}    


	@PostMapping("/std")
	@PreAuthorize(SecurityRoles.HAS_ANY_STANDARD_USER) 

	public ResponseEntity<?> saveSettingNonCry(  @Parameter(description = "Dati Parametro Salvato In Chiaro", required = true)  @Valid @RequestBody  SettingAppRequest request) {
		SettingAppResponse setting = settingAppService.saveSetting(request);
		return ResponseEntity.ok(setting);
	}


	@GetMapping("/{key}")
	@PreAuthorize(SecurityRoles.HAS_ANY_STANDARD_USER) 
	public ResponseEntity<SettingAppResponse> getSetting(@PathVariable String key) {
		SettingAppResponse setting = settingAppService.getSetting(key);
		return ResponseEntity.ok(setting);
	}



	@GetMapping("/value/{key}")
	@PreAuthorize(SecurityRoles.HAS_ANY_STANDARD_USER) 
	public ResponseEntity<String> getSettingValue(@PathVariable String key) {
		String value = settingAppService.getSettingValue(key);
		return ResponseEntity.ok(value);
	}


	@GetMapping
	@PreAuthorize(SecurityRoles.HAS_ANY_STANDARD_USER) 
	public ResponseEntity<List<SettingAppResponse>> getAllSettings() {
		List<SettingAppResponse> settings = settingAppService.getAllSettings();
		return ResponseEntity.ok(settings);
	}


	@GetMapping("/map")
	@PreAuthorize(SecurityRoles.HAS_ANY_STANDARD_USER) 
	public ResponseEntity<Map<String, String>> getAllSettingsAsMap() {
		Map<String, String> settings = settingAppService.getAllSettingsAsMap();
		return ResponseEntity.ok(settings);
	}


	@GetMapping("/category/{category}")
	@PreAuthorize(SecurityRoles.HAS_ANY_STANDARD_USER) 
	public ResponseEntity<List<SettingAppResponse>> getSettingsByCategory(@PathVariable String category) {
		List<SettingAppResponse> settings = settingAppService.getSettingsByCategory(category);
		return ResponseEntity.ok(settings);
	}


	@GetMapping("/category/{category}/map")
	public ResponseEntity<Map<String, String>> getSettingsByCategoryAsMap(@PathVariable String category) {
		Map<String, String> settings = settingAppService.getSettingsByCategoryAsMap(category);
		return ResponseEntity.ok(settings);
	}


	@DeleteMapping("/{key}")
	@PreAuthorize(SecurityRoles.HAS_DEV_OR_ADMIN) 
	public ResponseEntity<Void> deleteSetting(@PathVariable Long id) {
		settingAppService.deleteSetting(id);
		return ResponseEntity.noContent().build();
	}
}
