package com.example.proyectoparte1.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Key;
import java.util.List;

public class AuthorizationFilter extends BasicAuthenticationFilter {
    private final Key key;

    public AuthorizationFilter(AuthenticationManager manager, Key key) {
        super(manager);
        this.key = key;
    }

    // Método a executar cando se comproba o control de acceso
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            String header = request.getHeader("Authorization");

            // Verificamos si hay un token y si empieza con "Bearer"
            if (header == null || !header.startsWith("Bearer ")) {
                chain.doFilter(request, response);
                return;
            }

            // Intentamos autenticar el token
            UsernamePasswordAuthenticationToken authentication = getAuthentication(header);

            if (authentication != null) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token invalido o no autenticado");
                return;
            }

            chain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            response.setStatus(419); // Error personalizado para token expirado
            response.getWriter().write("Token expirado");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token no valido o firma incorrecta");
        }
    }

    private UsernamePasswordAuthenticationToken getAuthentication(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token.replace("Bearer ", "").trim())
                    .getBody();

            String user = claims.getSubject();
            List<GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(String.join(",", (List) claims.get("roles")));

            return user == null ? null : new UsernamePasswordAuthenticationToken(user, token, authorities);

        } catch (ExpiredJwtException e) {
            throw e; // Dejar que se capture en `doFilterInternal`
        } catch (Exception e) {
            return null; // Devolvemos null si el token no es válido
        }
    }

}
