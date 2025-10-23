package com.srsoft.modapimanager.service;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.srsoft.modapimanager.dto.SettingAppRequest;
import com.srsoft.modapimanager.dto.SettingAppResponse;
import com.srsoft.modapimanager.entity.SettingApp;
import com.srsoft.modapimanager.repository.SettingAppRepository;

import jakarta.transaction.Transactional;


@Transactional
@Service
public class SettingAppService {

	private static final String ENCRYPTION_ALGORITHM = "AES/CBC/PKCS5Padding";
	private static final String SECRET_KEY_ALGORITHM = "PBKDF2WithHmacSHA256";
	private static final String AES = "AES";
	private static final int ITERATION_COUNT = 65536;
	private static final int KEY_LENGTH = 256;
	private static final byte[] SALT = "ApplicationSalt123456".getBytes(StandardCharsets.UTF_8);


	@Autowired
	private  SettingAppRepository settingAppRepository;


	@Value("${app.settings.master-password}")
	private  String masterPassword;

	/**
	 * Salva un'impostazione con o senza crittografia.
	 */
	public SettingAppResponse saveSetting(SettingAppRequest request) {
		try {
			String key = request.getChiave();

			String value = request.getValore();
			boolean encrypted =request.isEncrypted();
			String description = request.getDescription();
			String category = request.getCategory();

			SettingApp setting = settingAppRepository.findBychiave(key)
					.orElse(new SettingApp());

			setting.setChiave(key);
			setting.setEncrypted(encrypted);
			setting.setDescription(description);
			setting.setCategory(category);

			if (encrypted) {
				// Genera IV e crittografa il valore
				byte[] iv = generateIv();
				String ivString = Base64.getEncoder().encodeToString(iv);
				String encryptedValue = encrypt(value, iv);

				setting.setValore(encryptedValue);
				setting.setInitializationVector(ivString);
			} else {
				// Salva il valore in chiaro
				setting.setValore(value);
				setting.setInitializationVector(null); // Non necessario per valori non crittografati
			}

			try {

				setting = settingAppRepository.save(setting);
			}
			catch (Exception e) {
				throw new RuntimeException("Errore durante salva  "+setting.getChiave() +" "+ e.getMessage(), e);
			}  


			// Crea e restituisci il DTO
			return new SettingAppResponse(
					setting.getId(),
					setting.getChiave(),
					value, // Valore originale, non crittografato
					encrypted,
					setting.getDescription(),
					setting.getCategory()
					);
		} catch (Exception e) {
			throw new RuntimeException("Errore nel salvataggio dell'impostazione: " + e.getMessage(), e);
		}
	}

	/**
	 * Recupera il DTO di un'impostazione.
	 */
	public SettingAppResponse getSetting(String key) {
		try {
			SettingApp setting = settingAppRepository.findBychiave(key)
					.orElseThrow(() -> new RuntimeException("Impostazione non trovata: " + key));

			String decryptedValue;
			if (setting.isEncrypted()) {
				byte[] iv = Base64.getDecoder().decode(setting.getInitializationVector());
				decryptedValue = decrypt(setting.getValore(), iv);
			} else {
				decryptedValue = setting.getValore(); // Il valore è già in chiaro
			}

			return new SettingAppResponse(
					setting.getId(),
					setting.getChiave(),
					decryptedValue,
					setting.isEncrypted(),
					setting.getDescription(),
					setting.getCategory()
					);
		} catch (Exception e) {
			throw new RuntimeException("Errore nel recupero dell'impostazione: " + e.getMessage(), e);
		}
	}

	/**
	 * Recupera solo il valore di un'impostazione, decrittografato se necessario.
	 */
	public String getSettingValue(String key) {
		return getSetting(key).getValore();
	}

	/**
	 * Recupera tutte le impostazioni come lista di DTO.
	 */
	public List<SettingAppResponse> getAllSettings() {
		try {
			List<SettingApp> allSettings = settingAppRepository.findAll();

			return allSettings.stream()
					.map(setting -> {
						try {
							String decryptedValue;
							if (setting.isEncrypted()) {
								byte[] iv = Base64.getDecoder().decode(setting.getInitializationVector());
								decryptedValue = decrypt(setting.getValore(), iv);
							} else {
								decryptedValue = setting.getValore(); // Il valore è già in chiaro
							}

							return new SettingAppResponse(
									setting.getId(),
									setting.getChiave(),
									decryptedValue,
									setting.isEncrypted(),
									setting.getDescription(),
									setting.getCategory()
									);
						} catch (Exception e) {
							throw new RuntimeException("Errore nella decrittografia: " + e.getMessage(), e);
						}
					})
					.collect(Collectors.toList());
		} catch (Exception e) {
			throw new RuntimeException("Errore nel recupero delle impostazioni: " + e.getMessage(), e);
		}
	}

	/**
	 * Recupera tutte le impostazioni come mappa key-value.
	 */
	public Map<String, String> getAllSettingsAsMap() {

		return getAllSettings().stream()
				.collect(Collectors.toMap(SettingAppResponse::getChiave, SettingAppResponse::getValore));
	}

	/**
	 * Recupera tutte le impostazioni di una categoria come lista di DTO.
	 */
	public List<SettingAppResponse> getSettingsByCategory(String category) {
		try {
			List<SettingApp> categorySettings = settingAppRepository.findByCategory(category);

			return categorySettings.stream()
					.map(setting -> {
						try {
							String decryptedValue;
							if (setting.isEncrypted()) {
								byte[] iv = Base64.getDecoder().decode(setting.getInitializationVector());
								decryptedValue = decrypt(setting.getValore(), iv);
							} else {
								decryptedValue = setting.getValore(); // Il valore è già in chiaro
							}

							return new SettingAppResponse(
									setting.getId(),
									setting.getChiave(),
									decryptedValue,
									setting.isEncrypted(),
									setting.getDescription(),
									setting.getCategory()
									);
						} catch (Exception e) {
							throw new RuntimeException("Errore nella decrittografia: " + e.getMessage(), e);
						}
					})
					.collect(Collectors.toList());
		} catch (Exception e) {
			throw new RuntimeException("Errore nel recupero delle impostazioni per categoria: " + e.getMessage(), e);
		}
	}

	/**
	 * Recupera tutte le impostazioni di una categoria come mappa key-value.
	 */
	public Map<String, String> getSettingsByCategoryAsMap(String category) {
		return getSettingsByCategory(category).stream()
				.collect(Collectors.toMap(SettingAppResponse::getChiave, SettingAppResponse::getValore));
	}

	/**
	 * Elimina un'impostazione.
	 */
	public void deleteSetting(Long id) {
		settingAppRepository.deleteById(id);
	}

	/**
	 * Genera un vettore di inizializzazione per AES.
	 */
	private byte[] generateIv() {
		SecureRandom random = new SecureRandom();
		byte[] iv = new byte[16]; // 16 bytes per AES
		random.nextBytes(iv);
		return iv;
	}

	/**
	 * Crittografa un valore.
	 */
	private String encrypt(String value, byte[] iv) throws Exception {
		SecretKey key = generateKey();
		Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
		IvParameterSpec ivSpec = new IvParameterSpec(iv);
		cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
		byte[] encrypted = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));
		return Base64.getEncoder().encodeToString(encrypted);
	}

	/**
	 * Decrittografa un valore.
	 */
	private String decrypt(String encryptedValue, byte[] iv) throws Exception {
		SecretKey key = generateKey();
		Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
		IvParameterSpec ivSpec = new IvParameterSpec(iv);
		cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
		byte[] original = cipher.doFinal(Base64.getDecoder().decode(encryptedValue));
		return new String(original, StandardCharsets.UTF_8);
	}

	/**
	 * Genera una chiave segreta basata sulla password master.
	 */
	private SecretKey generateKey() throws Exception {
		PBEKeySpec spec = new PBEKeySpec(
				masterPassword.toCharArray(), 
				SALT, 
				ITERATION_COUNT, 
				KEY_LENGTH
				);
		SecretKeyFactory skf = SecretKeyFactory.getInstance(SECRET_KEY_ALGORITHM);
		byte[] key = skf.generateSecret(spec).getEncoded();
		return new SecretKeySpec(key, AES);
	}
}