package com.checkpointBlog.secutiry;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RequestFilter extends OncePerRequestFilter{

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		String token = request.getHeader("Authorization");
		
		if(token != null) {
			try {
				UsernamePasswordAuthenticationToken authentication = TokenUtils.decodeToken(token);
				
				SecurityContextHolder.getContext().setAuthentication(authentication);
				
			} catch (ExpiredJwtException e) {
				throw new RuntimeException("Token expirado");
			} catch (MalformedJwtException e) {
				throw new MalformedJwtException("Token mal formado");
			} catch (Exception e) {
				throw new RuntimeException("Error en autenticación: " + e.getMessage());
			}
		}
		
		filterChain.doFilter(request, response);
		
	}

}
