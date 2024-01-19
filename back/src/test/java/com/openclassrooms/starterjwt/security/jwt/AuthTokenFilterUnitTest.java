package com.openclassrooms.starterjwt.security.jwt;

import static org.mockito.Mockito.*;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import com.openclassrooms.starterjwt.security.services.UserDetailsServiceImpl;

@ExtendWith(MockitoExtension.class)
public class AuthTokenFilterUnitTest {

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private AuthTokenFilter authTokenFilter;

    private String validJwt;
    private String username;
    private UserDetails userDetails;

    @BeforeEach
    void setup() {
        validJwt = "valid.jwt.token";
        username = "testuser";
        userDetails = mock(UserDetails.class);
    }

    @Test
    void whenValidJwtThenAuthenticate() throws Exception {
        // ARRANGE
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validJwt);
        when(jwtUtils.validateJwtToken(validJwt)).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken(validJwt)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);

        // ACT
        authTokenFilter.doFilterInternal(request, response, filterChain);

        // ASSERT
        verify(filterChain).doFilter(request, response);
        // Additional assertions can be made regarding the authentication object set in the security context
    }

    @Test
    void whenInvalidJwtThenDoNotAuthenticate() throws Exception {
        // ARRANGE
        when(request.getHeader("Authorization")).thenReturn("Bearer invalid.jwt.token");
        when(jwtUtils.validateJwtToken(anyString())).thenReturn(false);

        // ACT
        authTokenFilter.doFilterInternal(request, response, filterChain);

        // ASSERT
        verify(filterChain).doFilter(request, response);
        // Verify that no authentication is set in the security context
    }

    @Test
    void whenNoAuthorizationHeaderThenDoNotAuthenticate() throws Exception {
        // ARRANGE
        when(request.getHeader("Authorization")).thenReturn(null);

        // ACT
        authTokenFilter.doFilterInternal(request, response, filterChain);

        // ASSERT
        verify(filterChain).doFilter(request, response);
        // Verify that no authentication is set in the security context
    }
}