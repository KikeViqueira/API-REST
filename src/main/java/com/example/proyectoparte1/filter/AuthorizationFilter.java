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
        try{
            // Lemos o token da cabeceira "Authentication"
            String header = request.getHeader("Authorization");

            // Se non hai un token, ou non comeza co string "Bearer" (é dicir, non é un token JWT)
            // pasamos seguimos executando a cadea de filtros, e non facemos nada mais neste
            if(header == null || !header.startsWith("Bearer")){
                /*
                En este caso, llama a chain.doFilter(request, response); sin realizar ninguna autenticación y sin establecer el contexto de seguridad en Spring.
                 Esto permite que la solicitud continúe, pero sin un contexto de autenticación establecido, lo que equivale a una solicitud no autenticada.
                Si el endpoint al que se accede requiere autenticación, Spring Security devolverá automáticamente una respuesta de 401 Unauthorized.
                */
                chain.doFilter(request, response);
                return;
            }

            // No caso de que o token sexa un JWT, comprobamos que sexa valido
            UsernamePasswordAuthenticationToken authentication = getAuthentication(header);

            // Se o token era válido, establecemolo no contexto de seguridade de Spring para poder empregalo
            // nos nosos servizos
            SecurityContextHolder.getContext().setAuthentication(authentication);

            chain.doFilter(request, response);
        } catch(ExpiredJwtException e){
            // Se se sobrepasou a duración do token devolvemos un erro 419.
            response.setStatus(419);
        }
    }

    private UsernamePasswordAuthenticationToken getAuthentication(String token) throws ExpiredJwtException {
        // Creamos un parser para o token coa clave de firmado da nosa aplicación

        System.out.println(token);
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                // Parseamos o corpo do token
                .parseClaimsJws(token.replace("Bearer ", "").trim())
                .getBody();

        // Obtemos o nome do propietario do token
        String user = claims.getSubject();

        // Obtemos o listado de roles do usuario
        List<GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(String.join(",",(List)claims.get("roles")));

        // Devolvemos o token interno de Spring, que será engadido no contexto.
        return user == null ? null : new UsernamePasswordAuthenticationToken(user, token, authorities);
    }

}
