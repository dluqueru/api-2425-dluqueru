package com.checkpointBlog.security;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.checkpointBlog.model.Role;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;

public class TokenUtils {
	
	// Se crea la semilla
	private final static String ACCESS_TOKEN_SECRET="maripilisímaripilinomaripilisímaripilino";
	
	// Tiempo en segundos 4 minutos
	// SOLO RECOMENDABLE MIENTRAS DESARROLLAMOS
	private final static Long ACCESS_TOKEN_LIFE_TIME=(long) (60*4*1000);
	
	public static String generateToken(String username, String email, Role role) {
		Date expirationDate = new Date(System.currentTimeMillis() + ACCESS_TOKEN_LIFE_TIME);
		
		Map<String, Object> payLoad = new HashMap<>();
		
		payLoad.put("username", username);
		payLoad.put("email", email);
		payLoad.put("role", role.toString());
		
		String token = Jwts.builder()
							.subject(username)
							.expiration(expirationDate)
							.claims(payLoad)
							.signWith(Keys.hmacShaKeyFor(ACCESS_TOKEN_SECRET.getBytes()))
							.compact();
		
		return "Bearer " + token;
	}
	
	public static UsernamePasswordAuthenticationToken decodeToken(String token) {
		if(!token.startsWith("Bearer ")) {
			throw new MalformedJwtException("Formato erróneo");
		}
		
		token = token.substring(7);
		Claims claims = Jwts.parser().verifyWith(Keys.hmacShaKeyFor(ACCESS_TOKEN_SECRET.getBytes()))
										.build()
										.parseSignedClaims(token)
										.getPayload();
		
//		claims.get("user");
		String username = claims.getSubject();
		String role = (String) claims.get("role");
		
		List<SimpleGrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority(role));
		
		return new UsernamePasswordAuthenticationToken(username, null, authorities);
	}
	
}
