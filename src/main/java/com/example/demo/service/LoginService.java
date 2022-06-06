package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import org.springframework.http.ResponseEntity;

import java.util.Base64;
import javax.annotation.PostConstruct;

import com.example.demo.dtos.UserCredentialsDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.log4j.Log4j2;

import org.springframework.security.authentication.BadCredentialsException;

@Service
@Log4j2
public class LoginService {

	@Value("${spring.security.oauth2.client.registration.client-id}")
    private String registrationClientId;

    @Value("${spring.security.oauth2.client.registration.secret}")
    private String registrationClientSecret;

    @Value("${spring.security.oauth2.client.registration.scope}")
    private String registrationClientScope;

    @Value("${spring.security.oauth2.client.registration.authorization-grant-type}")
    private String registrationClientGrantType;

	private PublicKey publicKey;

	public LoginService(@Value("${spring.security.oauth2.client.registration.client-id}") String registrationClientId, @Value("${spring.security.oauth2.client.registration.secret}") String registrationClientSecret,
	@Value("${spring.security.oauth2.client.registration.scope}") String registrationClientScope, @Value("${spring.security.oauth2.client.registration.authorization-grant-type}") String registrationClientGrantType){
		this.registrationClientId = registrationClientId;
		this.registrationClientSecret = registrationClientSecret;
		this.registrationClientScope = registrationClientScope;
		this.registrationClientGrantType = registrationClientGrantType;
	}

	public LoginService(){
		//Emnpty
	}
	
	@PostConstruct
	public void modulusExponentPublicKey(){

		String modulus = "spvQcXWqYrMcvcqQmfSMYnbUC8U03YctnXyLIBe148OzhBrgdAOmPfMfJi_tUW8L9svVGpk5qG6dN0n669cRHKqU52GnG0tlyYXmzFC1hzHVgQz9ehve4tlJ7uw936XIUOAOxx3X20zdpx7gm4zHx4j2ZBlXskAj6U3adpHQNuwUE6kmngJWR-deWlEigMpRsvUVQ2O5h0-RSq8Wr_x7ud3K6GTtrzARamz9uk2IXatKYdnj5Jrk2jLY6nWt-GtxlA_l9XwIrOl6Sqa_pOGIpS01JKdxKvpBC9VdS8oXB-7P5qLksmv7tq-SbbiOec0cvU7WP7vURv104V4FiI_qoQ";
		String exponent = "AQAB";
		try {
			publicKey = getPublicKey(modulus, exponent);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException exception) {
			log.error("Invalid Key");
		}
	}

	private PublicKey getPublicKey(String encodedModulus, String encodedExponent) throws NoSuchAlgorithmException, InvalidKeySpecException {

        byte[] modulus = Base64.getUrlDecoder().decode(encodedModulus);
        byte[] exponent = Base64.getUrlDecoder().decode(encodedExponent);

        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : modulus) {
            stringBuilder.append(String.format("%02x", b));
        }
        String sm = stringBuilder.toString();

        stringBuilder = new StringBuilder();
        for (byte b : exponent) {
            stringBuilder.append(String.format("%02x", b));
        }
        String se = stringBuilder.toString();

        BigInteger bm = new BigInteger(sm,16);
        BigInteger be = new BigInteger(se,16);

        KeyFactory rsa = KeyFactory.getInstance("RSA");
        return rsa.generatePublic(new RSAPublicKeySpec(bm, be));
    }

	public Boolean validateToken(String authToken) throws NoSuchAlgorithmException, InvalidKeySpecException {
		try {

			Jwts.parser().setSigningKey(publicKey).parseClaimsJws(authToken);
			
			return true;

		} catch (SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException exception) {
			throw new BadCredentialsException("INVALID_CREDENTIALS", exception);
		} catch (ExpiredJwtException exception) {
            throw new BadCredentialsException("Token has Expired", exception);
		}
	}


	public String getUsernameFromToken(String token) {
		Claims claims = Jwts.parser().setSigningKey(publicKey).parseClaimsJws(token).getBody();

		return claims.get("preferred_username", String.class);
	}


	public String loginUserByToken(UserCredentialsDTO userCredentials) throws JsonProcessingException{

		String credentialsUsername = userCredentials.getUsername();
		String credentialsPassword = userCredentials.getPassword();

		RestTemplate restTemplate = new RestTemplate();
		
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("client_id", registrationClientId);
		map.add("scope", registrationClientScope);
		map.add("client_secret", registrationClientSecret);
		map.add("username", credentialsUsername);
		map.add("password", credentialsPassword);
		map.add("grant_type",registrationClientGrantType);

		HttpEntity<MultiValueMap<String, String>> httpEntityRequest = new HttpEntity<>(map, httpHeaders);
        
		ResponseEntity<String> response = restTemplate.postForEntity(
  		"https://login.microsoftonline.com/8d9934ac-b4d0-4ae6-9e75-0b54dbf6a4bb/oauth2/v2.0/token", httpEntityRequest , String.class);

		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode rootNode = objectMapper.readTree(response.getBody());
		JsonNode idToken = rootNode.path("id_token");
		
		String userCredentialsToken = idToken.toString();

		return (userCredentialsToken);	

    }

    
}
