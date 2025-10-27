package com.fintrack.fintrackbackend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

@Component
public class JwtAuthFilter extends OncePerRequestFilter{
    @Autowired
    private JwtUtils jwtUtils;

    private static final Set<String> PUBLIC_PREFIXES = Set.of("/api/auth/");

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        if (isPublicPath(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 1️⃣ Leer el header "Authorization"
        String authHeader = request.getHeader("Authorization");

        // 2️⃣ Verificar que el header no sea nulo y comience con "Bearer "
        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            // Extraer el token quitando el prefijo "Bearer "
            String token = authHeader.substring(7);

            // 3️⃣ Validar el token con JwtUtils
            if (jwtUtils.validateToken(token)) {
                // 4️⃣ Extraer el userId y guardarlo como atributo del request
                Integer userId = jwtUtils.extractUserId(token);
                request.setAttribute("userId", userId);

                // ✅ Crear Authentication vacío pero válido
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userId, null, null);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // ✅ Registrar al usuario como autenticado en el contexto de Spring Security
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                // ❌ Token inválido → devolver 401
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid or expired JWT token");
                return;
            }
        } else {
            // ❌ Si no hay token → devolver 401
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Missing Authorization header");
            return;
        }

        // 5️⃣ Si todo va bien, continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }

    private boolean isPublicPath(HttpServletRequest request) {
        String path = request.getRequestURI();
        if (path == null) {
            return false;
        }
        return PUBLIC_PREFIXES.stream().anyMatch(path::startsWith);
    }
}
