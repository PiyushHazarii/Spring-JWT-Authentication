package me.learning.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final Logger logger = LoggerFactory.getLogger(OncePerRequestFilter.class);

    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Get the token from Authorization header
        String requestHeader = request.getHeader("Authorization");
        logger.info(" Header : {}", requestHeader);

        String username = null;
        String token = null;

        if (requestHeader != null && requestHeader.startsWith("Bearer ")) {
            token = requestHeader.substring(7); // Remove "Bearer " prefix

            try {
                username = this.jwtHelper.getUsernameFromToken(token);
            } catch (IllegalArgumentException e) {
                logger.info("Illegal Argument while fetching the username!!");
                e.printStackTrace();
            } catch (ExpiredJwtException e) {
                logger.info("Given JWT token is expired!!");
                e.printStackTrace();
            } catch (MalformedJwtException e) {
                logger.info("Invalid JWT!!");
                e.printStackTrace();
            }
        } else {
            logger.info("Invalid Header value!!");
        }

        // Once we get the token, validate it and set authentication
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Load user details
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // Validate token
            Boolean isValid = this.jwtHelper.validateToken(token, userDetails);

            if (isValid) {
                // Set authentication
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                logger.info("Validation fails!!");
            }
        }

        // Continue with filter chain
        filterChain.doFilter(request, response);
    }
}

