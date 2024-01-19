package com.openclassrooms.starterjwt.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.services.UserService;
import com.openclassrooms.starterjwt.mapper.UserMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
public class UserControllerUnitTest {

    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;
    
    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private UserController userController;

    private User testUser;
    private UserDto testUserDto;

    @BeforeEach
    public void setup() {
        LocalDateTime now = LocalDateTime.parse("2023-08-29T00:00:00");
        testUser = new User(1L, "yoga@studio.com", "admin", "admin", "password", false, now, now);
        testUserDto = new UserDto(1L, "yoga@studio.com", "admin", "admin", false, "password", now, now);
    }

    @Nested
    @DisplayName("Tests pour la méthode findById")
    class FindByIdTests {

        @Test
        @DisplayName("Devrait retourner un utilisateur lorsqu'il est trouvé")
        void whenUserFound_thenReturnUser() {
            // ARRANGE : Configuration des mocks pour simuler le comportement des services
            given(userService.findById(anyLong())).willReturn(testUser);
            given(userMapper.toDto(any(User.class))).willReturn(testUserDto);

            // ACT : Appel de la méthode à tester
            ResponseEntity<?> result = userController.findById("1");

            // ASSERT : Vérification des résultats attendus
            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(result.getBody()).isEqualToComparingFieldByField(testUserDto);
        }

        @Test
        @DisplayName("Devrait retourner 404 lorsque l'utilisateur n'est pas trouvé")
        void whenUserNotFound_thenReturnNotFound() {
            // ARRANGE : Configuration pour retourner null quand l'utilisateur n'est pas trouvé
            given(userService.findById(anyLong())).willReturn(null);

            // ACT : Test avec un utilisateur inexistant
            ResponseEntity<?> result = userController.findById("1");

            // ASSERT : Vérification que le statut est NOT_FOUND
            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }

        @Test
        @DisplayName("Devrait retourner 400 pour un identifiant utilisateur invalide")
        void whenUserIdInvalid_thenReturnBadRequest() {
            // ACT : Test avec un identifiant utilisateur invalide
            ResponseEntity<?> result = userController.findById("invalid");

            // ASSERT : Vérification que le statut est BAD_REQUEST
            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }

    @Nested
    @DisplayName("Tests pour la méthode deleteUser")
    class DeleteUserTests {

        @Test
        @DisplayName("Should delete user when authorized")
        void whenAuthorizedToDelete_thenReturnOk() {
            // ARRANGE : Configuration des détails de l'utilisateur et de l'authentification
            UserDetailsImpl userDetails = new UserDetailsImpl(testUser.getId(), testUser.getEmail(),
                    testUser.getFirstName(), testUser.getLastName(), null, testUser.getPassword());
            Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null);
            SecurityContextHolder.setContext(securityContext);
            given(securityContext.getAuthentication()).willReturn(auth);
            given(userService.findById(anyLong())).willReturn(testUser);

            // ACT : Tentative de suppression de l'utilisateur
            ResponseEntity<?> result = userController.save("1");

            // ASSERT : Vérification de la suppression réussie
            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        @Test
        @DisplayName("Devrait retourner 404 lorsque l'utilisateur à supprimer n'est pas trouvé")
        void whenUserToDeleteNotFound_thenReturnNotFound() {
            // ARRANGE : Configuration pour simuler un utilisateur non trouvé
            given(userService.findById(anyLong())).willReturn(null);

            // ACT : Test avec un utilisateur inexistant à supprimer
            ResponseEntity<?> result = userController.save("1");

            // ASSERT : Vérification que le statut est NOT_FOUND
            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }

        @Test
        @DisplayName("Devrait retourner 401 lorsque non autorisé à supprimer")
        void whenUnauthorizedToDelete_thenReturnUnauthorized() {
            // ARRANGE : Configuration pour un utilisateur avec des détails différents
            UserDetailsImpl userDetails = new UserDetailsImpl(testUser.getId(), "differentEmail@gmail.com",
                    testUser.getFirstName(), testUser.getLastName(), null, testUser.getPassword());
            Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null);
            SecurityContextHolder.setContext(securityContext);
            given(securityContext.getAuthentication()).willReturn(auth);
            given(userService.findById(anyLong())).willReturn(testUser);

            // ACT : Tentative de suppression non autorisée
            ResponseEntity<?> result = userController.save("1");

            // ASSERT : Vérification que le statut est UNAUTHORIZED
            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        }

        @Test
        @DisplayName("Devrait retourner 400 pour un identifiant utilisateur invalide lors de la suppression")
        void whenUserIdInvalidOnDelete_thenReturnBadRequest() {
            // ACT : Test de suppression avec un identifiant invalide
            ResponseEntity<?> result = userController.save("invalid");

            // ASSERT : Vérification que le statut est BAD_REQUEST
            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }
}