package com.example.consultatii.filter;


import com.example.consultatii.config.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;


    @Override
    protected void doFilterInternal(jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response, jakarta.servlet.FilterChain filterChain) throws jakarta.servlet.ServletException, IOException {

        final String requestTokenHeader = request.getHeader("Authorization");

        String username = null;
        String jwtToken;
        Claims claims = null;

        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            try {
                //logger.error("we in");
                //logger.error("extracted claims in");

                claims = jwtUtil.extractClaims(jwtToken);
                UserDetails userDetails = jwtUtil.getUserDetails(claims);
                //logger.error("extracted claims out");
                //logger.error("extracted user in");


                //its actually the uid
                username = jwtUtil.extractUserId(claims);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    String role = jwtUtil.extractRole(claims);


                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            username, null, userDetails.getAuthorities());
                    //Collections.emptyList()
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails((jakarta.servlet.http.HttpServletRequest) request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }


                //logger.error("extracted user out");
            } catch (SignatureException e) {
                System.out.println("Invalid JWT signature: " + e.getMessage());
            }
        } else {
            logger.warn("JWT Token does not begin with Bearer String");
        }



        filterChain.doFilter(request, response);
    }
}