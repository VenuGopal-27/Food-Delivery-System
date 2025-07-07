package com.cts.fds.service.impl;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.stereotype.Service;

import com.cts.fds.entity.Restaurant;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtRestaurantService {

	// Use a static secret key

	private static final String SECRET_KEY = "juqnreaDD6ogVJxfHKMBrMvC5ZfdcKYxcT944gac2y4=";

	private Key getSignInKey() {
		byte[] keyBytes = Base64.getDecoder().decode(SECRET_KEY);
		return Keys.hmacShaKeyFor(keyBytes);
	}

	public String generateToken(Restaurant user) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("username", user.getEmail());
		claims.put("role", "Restaurant");
		claims.put("id", user.getId());

		return Jwts.builder().setClaims(claims).setSubject(user.getEmail()) // Set the subject claim
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
				.signWith(getSignInKey(), SignatureAlgorithm.HS256).compact();
	}

	public String extractUserName(String token) {

		return extractClaim(token, Claims::getSubject);
	}

	public boolean isTokenValid(String token, String email) {
		return extractUserName(token).equals(email) && !isTokenExpired(token); // Check both username and expiration
	}

	public boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = Jwts.parserBuilder().setSigningKey(getSignInKey()).build().parseClaimsJws(token)
				.getBody();
		return claimsResolver.apply(claims);
	}
}
