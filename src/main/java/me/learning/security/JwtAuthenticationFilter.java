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

// ye extends karega OncePerRequestFilter and overide karega ek method and in
// this class write the logic to check whether the token is comming in header or not.
// 1. get token from request
// 2. validate token
// 3. get usernname from the token
// 4. load user associated with this token
// 5. set authentication

// ye class har bar chalega jab bhi request krna chahega to ye pehele validate karega ki
// request valid hai ki nhi header check karega then wo aage bhej dega filterChain.doFilter(request, response);
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final Logger logger = LoggerFactory.getLogger(OncePerRequestFilter.class);

    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // Get the header from Authorization
        // in this way the header and token is comming
        // Authorization = Bearer woeruipoweiur  like this
        String requestHeader = request.getHeader("Authorization");
        logger.info(" Header : {}", requestHeader);

        String username = null;
        String token = null;

        // check karega ki requestHeader null nhi hona chaiye and start hone chaiye bearer se
        if (requestHeader != null && requestHeader.startsWith("Bearer ")) {
            token = requestHeader.substring(7); // Remove "Bearer " prefix

            try {
                // from this method you can get the particular username.
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

        // agar username mein null nhi hai and uska authentication null hai matlab
        // wo authenticate nhi hua hai then hum usko authenticate karenge
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Load user details
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // Validate token
            Boolean isValid = this.jwtHelper.validateToken(token, userDetails);

            if (isValid) {
                // Set authentication
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
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

