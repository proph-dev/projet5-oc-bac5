package com.openclassrooms.starterjwt.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.Optional;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.payload.request.LoginRequest;
import com.openclassrooms.starterjwt.payload.request.SignupRequest;
import com.openclassrooms.starterjwt.payload.response.JwtResponse;
import com.openclassrooms.starterjwt.payload.response.MessageResponse;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
public class AuthControllerUnitTest {

    @Mock
    private AuthenticationManager mockAuthenticationManager;
    @Mock
    private JwtUtils mockJwtUtils;
    @Mock
    private PasswordEncoder mockPasswordEncoder;
    @Mock
    private UserRepository mockUserRepository;

    @InjectMocks
    private AuthController authController;

    private UserDetailsImpl userDetails;
    private User mockUser;

    @BeforeEach
    void setUp() {
        // Utilisez le builder pour créer l'instance de UserDetailsImpl
        userDetails = UserDetailsImpl.builder()
            .id(1L)
            .username("yoga@studio.com")
            .firstName("admin")
            .lastName("admin")
            .password("password123")
            .admin(true)
            .build();

        // Création de l'instance de User
        mockUser = new User("yoga@studio.com", "admin", "admin", "password123", true);
    }

    @Test
    @DisplayName("Authentification réussie")
    public void whenValidLogin_thenAuthenticate() {
        // GIVEN
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("yoga@studio.com");
        loginRequest.setPassword("password123");
        
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null);

        given(mockAuthenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())))
                .willReturn(authentication);
        given(mockJwtUtils.generateJwtToken(authentication)).willReturn("token");
        given(mockUserRepository.findByEmail(userDetails.getUsername())).willReturn(Optional.of(mockUser));

        // WHEN
        ResponseEntity<?> response = authController.authenticateUser(loginRequest);
        
        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        JwtResponse jwtResponse = (JwtResponse) response.getBody();
        assertThat(jwtResponse).isNotNull();
        assertThat(jwtResponse.getToken()).isEqualTo("token");
        assertThat(jwtResponse.getId()).isEqualTo(userDetails.getId());
        assertThat(jwtResponse.getUsername()).isEqualTo(userDetails.getUsername());
        assertThat(jwtResponse.getAdmin()).isEqualTo(mockUser.isAdmin());
    }

    @Test
    @DisplayName("Enregistrement réussi")
    public void whenNewUser_thenRegister() {
        // GIVEN
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("newuser@example.com");
        signupRequest.setPassword("newPass123");
        signupRequest.setFirstName("Alice");
        signupRequest.setLastName("Wonderland");

        given(mockUserRepository.existsByEmail("newuser@example.com")).willReturn(false);
        given(mockPasswordEncoder.encode("newPass123")).willReturn("encodedPassword");
        given(mockUserRepository.save(any(User.class))).willReturn(new User());

        // WHEN
        ResponseEntity<?> response = authController.registerUser(signupRequest);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        MessageResponse messageResponse = (MessageResponse) response.getBody();
        assertThat(messageResponse).isNotNull();
        assertThat(messageResponse.getMessage()).isEqualTo("User registered successfully!");
    }

    @Test
    @DisplayName("Inscription avec l'adresse électronique existante")
    public void whenEmailExists_thenFailRegistration() {
        // GIVEN
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("existing@example.com");

        given(mockUserRepository.existsByEmail("existing@example.com")).willReturn(true);

        // WHEN
        ResponseEntity<?> response = authController.registerUser(signupRequest);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        MessageResponse messageResponse = (MessageResponse) response.getBody();
        assertThat(messageResponse).isNotNull();
        assertThat(messageResponse.getMessage()).isEqualTo("Error: Email is already taken!");
    }
}