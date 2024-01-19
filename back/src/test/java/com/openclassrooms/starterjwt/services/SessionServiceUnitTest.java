package com.openclassrooms.starterjwt.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.exception.NotFoundException;

@SpringBootTest
public class SessionServiceUnitTest {

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SessionService sessionService;

    private Session mockSession;
    private User mockUser;

    @BeforeEach
    public void setUp() {
        // Création d'un utilisateur fictif pour les tests
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("yoga@studio.com");
        mockUser.setFirstName("admin");
        mockUser.setLastName("admin");
        mockUser.setPassword("password");

        // Création d'une session fictive pour les tests
        mockSession = new Session();
        mockSession.setId(1L);
        mockSession.setName("Test Session");
        mockSession.setUsers(new ArrayList<>()); // Liste vide d'utilisateurs initialement

        // Configuration des méthodes mock pour simuler le comportement souhaité
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(mockSession));
    }

    @Test
    @DisplayName("Créer une session avec succès")
    public void whenCreateSession_thenSaveSession() {
        // ARRANGE : Configure le mock de sessionRepository pour renvoyer mockSession lorsque la méthode save est appelée avec n'importe quelle session.
        when(sessionRepository.save(any(Session.class))).thenReturn(mockSession);

        // ACT : Appelle la méthode create du sessionService avec une nouvelle session.
        Session savedSession = sessionService.create(new Session());

        // ASSERT : Vérifie que la session retournée est égale à mockSession et que la méthode save du sessionRepository a été appelée une fois.
        assertEquals(mockSession, savedSession);
        verify(sessionRepository, times(1)).save(any(Session.class));
    }

    @Test
    @DisplayName("Échec de la création d'une session avec une session nulle")
    public void whenCreateNullSession_thenHandleGracefully() {    
        // ACT : Appelle la méthode create du sessionService avec null.
        Session result = sessionService.create(null);
    
        // ASSERT : Vérifie que la méthode gère l'entrée nulle comme prévu (par exemple, retourne null ou ne lève pas d'exception).
        assertNull(result, "La méthode create devrait gérer null en retournant null ou en ne levant pas d'exception");
    }

    @Test
    @DisplayName("Supprimer une session existante")
    public void whenDeleteExistingSession_thenPerformDeletion() {
        // ARRANGE : Configure le mock de sessionRepository pour ne rien faire lorsqu'on appelle deleteById avec un identifiant spécifique.
        Long sessionId = 1L;
        doNothing().when(sessionRepository).deleteById(sessionId);

        // ACT : Appelle la méthode delete de sessionService avec l'identifiant de session donné.
        sessionService.delete(sessionId);

        // ASSERT : Vérifie que deleteById du sessionRepository a été appelée une fois avec le même identifiant.
        verify(sessionRepository, times(1)).deleteById(sessionId);
    }
    
    @Test
    @DisplayName("Tenter de supprimer une session inexistante")
    public void whenDeleteNonExistingSession_thenThrowException() {
        // ARRANGE : Configure le mock de sessionRepository pour lancer une NotFoundException lorsque deleteById est appelée avec un identifiant spécifique.
        Long sessionId = 1L;
        doThrow(new NotFoundException()).when(sessionRepository).deleteById(sessionId);

        // ACT & ASSERT : Vérifie qu'une NotFoundException est levée lorsque delete est appelée sur sessionService avec cet identifiant.
        assertThrows(NotFoundException.class, () -> sessionService.delete(sessionId));
    }

    @Test
    @DisplayName("Trouver toutes les sessions")
    public void whenFindAll_thenGetAllSessions() {
        // ARRANGE : Configure le mock de sessionRepository pour renvoyer une liste contenant mockSession lorsque findAll est appelée.
        when(sessionRepository.findAll()).thenReturn(Arrays.asList(mockSession));

        // ACT : Appelle la méthode findAll du sessionService.
        List<Session> sessions = sessionService.findAll();

        // ASSERT : Vérifie que la liste retournée n'est pas vide, que son premier élément est mockSession, et que findAll a été appelée une fois sur le sessionRepository.
        assertFalse(sessions.isEmpty());
        assertEquals(mockSession, sessions.get(0));
        verify(sessionRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Trouver une session par son ID")
    public void whenGetById_thenFindSession() {
        // ARRANGE : Configure le mock de sessionRepository pour renvoyer mockSession encapsulé dans un Optional lorsque findById est appelée avec un identifiant spécifique.
        Long sessionId = 1L;
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(mockSession));

        // ACT : Appelle la méthode getById du sessionService avec l'identifiant de session donné.
        Session session = sessionService.getById(sessionId);

        // ASSERT : Vérifie que la session retournée est égale à mockSession et que findById a été appelée une fois sur le sessionRepository avec l'identifiant spécifié.
        assertEquals(mockSession, session);
        verify(sessionRepository, times(1)).findById(sessionId);
    }
    
    @Test
    @DisplayName("Échec de la recherche d'une session par un ID inexistant")
    public void whenGetByNonExistingId_thenReturnNull() {
        // ARRANGE
        // Définir l'ID de la session qui n'existe pas dans la base de données.
        Long sessionId = 8L;
    
        // Configurer le comportement du mock sessionRepository pour renvoyer un Optional vide
        // lors de la recherche d'une session avec un ID inexistant.
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.empty());
    
        // ACT
        // Appeler la méthode getById sur le service en utilisant l'ID inexistant pour récupérer une session.
        Session session = sessionService.getById(sessionId);
    
        // ASSERT
        // Vérifier que le résultat est nul, ce qui indique qu'aucune session avec cet ID n'a été trouvée.
        assertNull(session, "La session devrait être nulle car l'ID n'existe pas");
    }

    @Test
    @DisplayName("Mise à jour d'une session existante")
    public void whenUpdateSession_thenSaveUpdatedSession() {
        // ARRANGE : Configure le mock de sessionRepository pour renvoyer mockSession lorsque la méthode save est appelée avec n'importe quelle session.
        Long sessionId = 1L;
        when(sessionRepository.save(any(Session.class))).thenReturn(mockSession);

        // ACT : Appelle la méthode update du sessionService avec un identifiant de session et une nouvelle session.
        Session updatedSession = sessionService.update(sessionId, new Session());

        // ASSERT : Vérifie que la session retournée est égale à mockSession et que la méthode save a été appelée une fois.
        assertEquals(mockSession, updatedSession);
        verify(sessionRepository, times(1)).save(any(Session.class));
    }

    @Test
    @DisplayName("Test inscription réussie à une session")
    public void testParticipateInSession_Success() {
        // ARRANGE : Configure les mocks pour retourner des objets spécifiques lors de la recherche de la session et de l'utilisateur, et pour que save retourne une session mise à jour.
        Long sessionId = 1L;
        Long userId = 1L;
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(mockSession));
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(sessionRepository.save(any(Session.class))).thenReturn(mockSession);

        // ACT : Appelle la méthode participate avec l'identifiant de la session et de l'utilisateur.
        sessionService.participate(sessionId, userId);

        // ASSERT : Vérifie que les méthodes findById et save ont été appelées sur les repositories et que l'utilisateur est maintenant dans la liste des utilisateurs de la session.
        verify(sessionRepository, times(1)).findById(sessionId);
        verify(userRepository, times(1)).findById(userId);
        verify(sessionRepository, times(1)).save(mockSession);
        assertTrue(mockSession.getUsers().contains(mockUser));
    }

    @Test
    @DisplayName("Test inscription échouée à une session - Session inexistante")
    public void testParticipateInSession_SessionNotFound() {
        // ARRANGE : Configure le mock de sessionRepository pour renvoyer un Optional vide, simulant une session introuvable.
        Long sessionId = 1L;
        Long userId = 1L;
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.empty());

        // ACT & ASSERT : Vérifie qu'une NotFoundException est levée lorsque la méthode participate est appelée avec un identifiant de session inexistant.
        assertThrows(NotFoundException.class, () -> sessionService.participate(sessionId, userId));
    }

    @Test
    @DisplayName("Test inscription échouée à une session - Utilisateur déjà inscrit")
    public void testParticipateInSession_UserAlreadyParticipating() {
        // ARRANGE : Configure les mocks pour simuler un scénario où l'utilisateur est déjà inscrit à la session.
        Long sessionId = 1L;
        Long userId = 1L;
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(mockSession));
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        mockSession.getUsers().add(mockUser);

        // ACT & ASSERT : Vérifie qu'une BadRequestException est levée lorsque la méthode participate est appelée avec un utilisateur déjà inscrit.
        assertThrows(BadRequestException.class, () -> sessionService.participate(sessionId, userId));
    }

    @Test
    @DisplayName("Test désinscription réussie d'une session")
    public void testNoLongerParticipateInSession_Success() {
        // ARRANGE : Configure les mocks pour simuler une session avec un utilisateur inscrit.
        Long sessionId = 1L;
        Long userId = 1L;
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(mockSession));
        mockSession.getUsers().add(mockUser);

        // ACT : Appelle la méthode noLongerParticipate pour désinscrire l'utilisateur de la session.
        sessionService.noLongerParticipate(sessionId, userId);
        
        // ASSERT : Vérifie que les méthodes findById et save ont été appelées sur les repositories, et que l'utilisateur n'est plus dans la liste des utilisateurs de la session.
        verify(sessionRepository, times(1)).findById(sessionId);
        verify(sessionRepository, times(1)).save(mockSession);
        assertFalse(mockSession.getUsers().contains(mockUser));
    }

    @Test
    @DisplayName("Test désinscription échouée d'une session - Utilisateur non inscrit")
    public void testNoLongerParticipateInSession_UserNotParticipating() {
        // ARRANGE : Configure le mock de sessionRepository pour renvoyer une session sans que l'utilisateur spécifié y soit inscrit.
        Long sessionId = 1L;
        Long userId = 1L;
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(mockSession));

        // ACT & ASSERT : Vérifie qu'une BadRequestException est levée lorsque la méthode noLongerParticipate est appelée pour un utilisateur non inscrit.
        assertThrows(BadRequestException.class, () -> sessionService.noLongerParticipate(sessionId, userId));
    }
}