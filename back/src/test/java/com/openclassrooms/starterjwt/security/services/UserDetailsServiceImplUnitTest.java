package com.openclassrooms.starterjwt.security.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

@SpringBootTest
public class UserDetailsServiceImplUnitTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private User mockUser;

    @BeforeEach
    public void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("yoga@studio.com");
        mockUser.setFirstName("admin");
        mockUser.setLastName("admin");
        mockUser.setPassword("password");

        when(userRepository.findByEmail("yoga@studio.com")).thenReturn(Optional.of(mockUser));
        when(userRepository.findByEmail("unknown@studio.com")).thenReturn(Optional.empty());
    }

    @Test
    @DisplayName("Chargement d'un utilisateur existant par son nom d'utilisateur")
    public void testLoadUserByUsername_ExistingUser() {
        // ACT : Tente de charger l'utilisateur par son nom d'utilisateur (e-mail).
        UserDetails userDetails = userDetailsService.loadUserByUsername("yoga@studio.com");

        // ASSERT : Vérifie que les détails de l'utilisateur (nom d'utilisateur et mot de passe) sont corrects.
        assertEquals("yoga@studio.com", userDetails.getUsername());
        assertEquals(mockUser.getPassword(), userDetails.getPassword());
    }

    @Test
    @DisplayName("Le chargement d'un utilisateur inexistant par son nom d'utilisateur provoque une exception.")
    public void testLoadUserByUsername_NonExistingUser() {
        // ACT & ASSERT : Anticipe une exception UsernameNotFoundException lors de la tentative de chargement d'un utilisateur avec un nom d'utilisateur (e-mail) inexistant
        assertThrows(UsernameNotFoundException.class, 
            () -> userDetailsService.loadUserByUsername("unknown@studio.com"));
    }
}