package com.srsoft.modapimanager.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.srsoft.modapimanager.dto.AuthResponse;
import com.srsoft.modapimanager.entity.User;
import com.srsoft.modapimanager.entity.UserSession;
import com.srsoft.modapimanager.exception.BusinessException;
import com.srsoft.modapimanager.repository.UserSessionRepository;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;


@Service
public class JwtService {
	@Autowired
	private  UserSessionRepository sessionRepository;

	private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);


	@Value("${jwt.expiration}")
	private long jwtExpiration;

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}


	public String generateToken(UserDetails userDetails) {
		User user = (User) userDetails; 

		// Verifica se l'utente ha già una sessione attiva
		sessionRepository.findByUserId(user.getId())
		.ifPresent(session -> {
			// Se c'è una sessione, la eliminiamo (logout forzato)
			sessionRepository.delete(session);
		});

		// Genera un nuovo token
		String token = Jwts.builder()
				.setSubject(userDetails.getUsername())
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis()  + jwtExpiration)) // 24 ore
				.signWith(key)
				.compact();

		// Salva la nuova sessione
		UserSession session = new UserSession();
		session.setUserId(user.getId());
		session.setToken(token);
		session.setExpiryDate(LocalDateTime.now().plusDays(1));
		sessionRepository.save(session);

		return token;
	}



	public AuthResponse generateTokenExp(Map<String, Object> extraClaims, UserDetails userDetails, User user) {

		Date dateStart	=new Date(System.currentTimeMillis());
		Date dataEnd =new Date(System.currentTimeMillis()+ jwtExpiration);

		// Verifica se l'utente ha già una sessione attiva
		sessionRepository.findByUserId(user.getId())
		.ifPresent(session -> {
			// Se c'è una sessione, la eliminiamo (logout forzato)
			sessionRepository.delete(session);
		});

		String token=Jwts.builder()
				.setClaims(extraClaims)
				.setSubject(userDetails.getUsername())
				.setIssuedAt(dateStart)
				.setExpiration(dataEnd)
				.signWith(key)
				.compact();




		// Salva la nuova sessione
		UserSession session = new UserSession();
		session.setUserId(user.getId());
		session.setToken(token);
		session.setExpiryDate(LocalDateTime.now().plusDays(1));
		sessionRepository.save(session);


		return new AuthResponse(token,user.getRoles(),dataEnd,user.getPermessi());
	}




	public boolean isTokenValid(String token, UserDetails userDetails, User user) {
		// Verifica nel database se il token è presente e attivo
		Optional<UserSession> session = sessionRepository.findByToken(token);
		if (session.isEmpty()) {
			return false;
		}

		// Verifica che il token appartenga all'utente giusto
	
		if (!session.get().getUserId().equals(user.getId())) {
			return false;
		}

		// Verifica che il token non sia scaduto nel database
		if (session.get().getExpiryDate().isBefore(LocalDateTime.now())) {
			sessionRepository.delete(session.get());
			return false;
		}

		// Verifica la validità del token con la firma JWT
		try {
			Claims claims = Jwts.parserBuilder()
					.setSigningKey(key)
					.build()
					.parseClaimsJws(token)
					.getBody();

			return claims.getSubject().equals(userDetails.getUsername()) && 
					!claims.getExpiration().before(new Date());
		} catch (Exception e) {
			return false;
		}
	}


	public AuthResponse renewToken(String token, UserDetails userDetails, User user) {
		try {

			/*Claims claims = extractAllClaims(token);*/
			
			if (!isTokenValid(token,userDetails,user)){
				throw new BusinessException("Token non valido o impossibile da rinnovare");
			}
/*
			if (claims.getExpiration().before(new Date())) {
				
			}*/
			return generateTokenExp(Map.of(), userDetails,user);
		}  catch (Exception ex) {
			throw new IllegalArgumentException("Token non valido o impossibile da rinnovare", ex);
		}
	}

    public void invalidateToken(String token) {
        sessionRepository.findByToken(token)
            .ifPresent(sessionRepository::delete);
    }



	private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	
	private Claims extractAllClaims(String token) {
		return Jwts.parserBuilder()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(token)
				.getBody();
	}




}