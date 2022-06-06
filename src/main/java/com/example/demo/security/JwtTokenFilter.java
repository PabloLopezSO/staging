package com.example.demo.security;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashSet;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.demo.service.LoginService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
public class JwtTokenFilter extends OncePerRequestFilter {

	@Autowired
	private LoginService userCredentials;

    @Override
    protected void doFilterInternal(HttpServletRequest httpRequest,HttpServletResponse httpResponse,FilterChain filterChain) throws ServletException, IOException {

		String jwtToken = extractJwtFromRequest(httpRequest);

		try {
			// ToDo: reduce the times the JWT parser is invoked (expensive operation!)
			if (StringUtils.hasText(jwtToken) && Boolean.TRUE.equals(userCredentials.validateToken(jwtToken))){

				UserDetails userDetails = new User(userCredentials.getUsernameFromToken(jwtToken), "", new HashSet<>());

				UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
						userDetails, null, userDetails.getAuthorities());

				SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

			} else {
				log.info("Cannot set the Security Context A");
			}

		} catch (NoSuchAlgorithmException | InvalidKeySpecException exception) {

			log.info("Cannot set the Security Context B");
		}

        filterChain.doFilter(httpRequest, httpResponse);
    }


	public String extractJwtFromRequest(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7, bearerToken.length());
		}
		return null;
	}
}