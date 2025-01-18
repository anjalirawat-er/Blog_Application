package com.blog.security;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;


@Component 
public class JwtTokenHelper {

	public static final long JWT_TOKEN_VALIDITY = 5*60*60;
	
	private String secret = "jwtTokenKey";
	
	// Retrieve username from JWT token
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    // Retrieve expiration date from JWT token
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }
	
	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}
	
	//for retrieving any information from token we will need the secret key
	private Claims getAllClaimsFromToken(String token) {
		return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
	}
	
	//check if the token has expired 
	private Boolean isTokenExpired(String token) {
		final Date expiration = getExpirationDateFromToken(token);
		return expiration.before(new Date());
	}
	
	//generate token for user
	public String generateToken(UserDetails userDetails){
		 Map<String, Object> claims = new HashMap<>();
		 return doGenerateToken(claims, userDetails.getUsername());
	}
	

	//while creating the token
	//1. Define claims of the token, like Issue,Expiration, Subject and the Id
	//2. Sign the JWT using the HS512 algorithm and secret key
	//3. According to JWS compact Serialization (https://tool.ietf.org/html/draft-isft-jose-json
	//   compaction of the JWT to a URl-safe string
	  private String doGenerateToken(Map<String, Object> claims, String subject) {
	        // Create the JWT token and sign it with the HS512 algorithm and the secret key
	        return Jwts.builder()
	                .setClaims(claims) // Set any additional claims if needed
	                .setSubject(subject) // Set the subject (typically the username)
	                .setIssuedAt(new Date()) // Set the issue date
	                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000)) // Set expiration date
	                .signWith(SignatureAlgorithm.HS512, secret) // Sign with the HS512 algorithm and secret key
	                .compact(); // Return the compacted (URL-safe) JWT token string
	    }

	//Validate token
	public Boolean validateToken(String token, UserDetails userDetails) {
		final String username=getUsernameFromToken(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}
}
