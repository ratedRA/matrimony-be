package com.matrimony.common.springsecurity.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.matrimony.common.exceptionhandling.RestResponseEntityExceptionHandler;
import com.matrimony.common.matrimonytoken.JjwtImpl;
import com.matrimony.common.springsecurity.userservice.MatrimonyUserDetailsService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Component
public class AuthenticationTokenRequestFilter extends OncePerRequestFilter {

    @Autowired
    private MatrimonyUserDetailsService userDetailsService;

    @Autowired
    private RestResponseEntityExceptionHandler responseErrorHandler;

    @Autowired
    private JjwtImpl jjwt;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        String authToken = null;
        try{
            if(authHeader != null && authHeader.startsWith("Bearer ")){
                authToken = authHeader.substring(7);
            }
            if(authToken != null) {
                Jws<Claims> claimsJws = jjwt.validateJwt(authToken);
                String phone = (String) claimsJws.getBody().get("phone");
                UserDetails userDetails = userDetailsService.loadUserByUsername(phone);

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
            filterChain.doFilter(request, response);
        } catch (Exception ex){

            ResponseEntity<Object>
                    exceptionResponseEntity = responseErrorHandler.defaultErrorHandler(ex);

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");

            //pass down the actual obj that exception com.demo.handler normally send
            ObjectMapper mapper = new ObjectMapper();
            PrintWriter out = response.getWriter();
            out.print(mapper.writeValueAsString(exceptionResponseEntity.getBody()));
            out.flush();

            return;
        }
    }
}
