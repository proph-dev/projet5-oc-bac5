package com.openclassrooms.starterjwt.services;

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

import java.util.Optional;

@SpringBootTest
public class UserServiceUnitTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User mockUser;

    @BeforeEach
    public void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("yoga@studio.com");
        mockUser.setFirstName("admin");
        mockUser.setLastName("admin");
        mockUser.setPassword("password");

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
    }

    @Test
    @DisplayName("Supprimer un utilisateur par son ID")
    public void testDeleteUserById() {
        // ACT : Supprimer l'utilisateur avec l'ID spécifié.
        userService.delete(1L);

        // ASSERT : Vérifier que la méthode deleteById a été appelée sur le userRepository avec l'ID spécifié.
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Trouver un utilisateur par son ID")
    public void testFindUserById() {
        // ACT : Trouver l'utilisateur avec l'ID spécifié.
        User foundUser = userService.findById(1L);

        // ASSERT : Vérifier que l'utilisateur trouvé n'est pas null et correspond au mockUser.
        assertNotNull(foundUser, "L'utilisateur trouvé ne devrait pas être null");
        assertEquals(mockUser, foundUser, "L'utilisateur trouvé devrait correspondre au mockUser");
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Échec de la recherche d'un utilisateur par un ID inexistant")
    public void testFindUserByIdNotFound() {
        // ARRANGE : Configurer le userRepository pour retourner un résultat vide pour un ID inexistant.
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        // ACT : Tenter de trouver un utilisateur avec un ID inexistant.
        User foundUser = userService.findById(2L);

        // ASSERT : Vérifier qu'aucun utilisateur n'est trouvé et que findById a été appelé avec l'ID inexistant.
        assertNull(foundUser, "Aucun utilisateur ne devrait être trouvé avec un ID inexistant");
        verify(userRepository, times(1)).findById(2L);
    }
}