package com.openclassrooms.starterjwt.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.mapper.SessionMapper;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.services.SessionService;

@ExtendWith(MockitoExtension.class)
public class SessionControllerUnitTest  {

    @Mock
    private SessionService mockSessionService;

    @Mock
    private SessionMapper mockSessionMapper;

    @InjectMocks
    private SessionController sessionController;

    private Session sessionExample;
    private SessionDto sessionDtoExample;

    @BeforeEach
    public void setup() {
        sessionExample = new Session(1L, "Session Test", null, null, null, null, null, null);
        sessionDtoExample = new SessionDto(1L, "Session Test", null, null, null, null, null, null);
    }

    @Test
    @DisplayName("Devrait retourner une session lorsque l'ID est valide")
    void shouldReturnSessionWhenIdIsValid() {
        // ARRANGE: Préparation des mocks pour une session valide
        when(mockSessionService.getById(1L)).thenReturn(sessionExample);
        when(mockSessionMapper.toDto(sessionExample)).thenReturn(sessionDtoExample);
    
        // ACT: Appel de la méthode à tester
        ResponseEntity<?> result = sessionController.findById("1");
        
        // ASSERT: Vérification des résultats attendus
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        SessionDto responseBody = (SessionDto) result.getBody();
        assertEquals(1L, responseBody.getId());
        assertEquals("Session Test", responseBody.getName());
    }

    @Test
    @DisplayName("Devrait retourner un statut 'non trouvé' lorsque la session n'existe pas")
    void shouldReturnNotFoundWhenSessionDoesNotExist() {
        // ARRANGE: Préparation des mocks pour une session inexistante
        when(mockSessionService.getById(1L)).thenReturn(null);
    
        // ACT: Appel de la méthode à tester
        ResponseEntity<?> result = sessionController.findById("1");
        
        // ASSERT: Vérification que le résultat est bien NOT_FOUND
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

    @Test
    @DisplayName("Devrait retourner un statut 'mauvaise requête' lorsque l'ID est invalide")
    void shouldReturnBadRequestWhenIdIsInvalid() {
        // ACT: Appel de la méthode à tester avec un ID invalide
        ResponseEntity<?> result = sessionController.findById("abc");
        
        // ASSERT: Vérification que le résultat est bien BAD_REQUEST
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    @DisplayName("Devrait retourner toutes les sessions")
    void shouldReturnAllSessions() {
        // ARRANGE: Préparation des mocks pour la liste des sessions
        List<Session> sessions = Arrays.asList(sessionExample, new Session(2L, "Another Session", null, null, null, null, null, null));
        when(mockSessionService.findAll()).thenReturn(sessions);
        when(mockSessionMapper.toDto(sessions)).thenReturn(Arrays.asList(sessionDtoExample, new SessionDto(2L, "Another Session", null, null, null, null, null, null)));

        // ACT: Appel de la méthode à tester
        ResponseEntity<?> result = sessionController.findAll();
        
        // ASSERT: Vérification des résultats attendus
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        List<SessionDto> responseBody = (List<SessionDto>) result.getBody();
        assertEquals(2, responseBody.size());
    }

    @Test
    @DisplayName("Devrait créer une session avec succès")
    void shouldCreateSessionSuccessfully() {
        // ARRANGE: Préparation des mocks pour la création d'une session
        SessionDto newSessionDto = new SessionDto(null, "New Session", null, null, null, null, null, null);
        Session newSession = new Session(null, "New Session", null, null, null, null, null, null);
    
        when(mockSessionMapper.toEntity(newSessionDto)).thenReturn(newSession);
        when(mockSessionService.create(newSession)).thenReturn(sessionExample);
        when(mockSessionMapper.toDto(sessionExample)).thenReturn(sessionDtoExample);
    
        // ACT: Appel de la méthode à tester
        ResponseEntity<?> result = sessionController.create(newSessionDto);
    
        // ASSERT: Vérification des résultats attendus
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        SessionDto responseBody = (SessionDto) result.getBody();
        assertEquals(sessionDtoExample.getId(), responseBody.getId());
        assertEquals(sessionDtoExample.getName(), responseBody.getName());
    }

    @Test
    @DisplayName("Devrait mettre à jour une session avec succès")
    void shouldUpdateSessionSuccessfully() {
        // ARRANGE: Préparation des mocks pour la mise à jour d'une session
        SessionDto updatedSessionDto = new SessionDto(1L, "Updated Session", null, null, null, null, null, null);
        Session updatedSession = new Session(1L, "Updated Session", null, null, null, null, null, null);
        when(mockSessionMapper.toEntity(updatedSessionDto)).thenReturn(updatedSession);
        when(mockSessionService.update(1L, updatedSession)).thenReturn(updatedSession);
        when(mockSessionMapper.toDto(updatedSession)).thenReturn(updatedSessionDto);

        // ACT: Appel de la méthode à tester
        ResponseEntity<?> result = sessionController.update("1", updatedSessionDto);

        // ASSERT: Vérification des résultats attendus
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        SessionDto responseBody = (SessionDto) result.getBody();
        assertEquals(updatedSessionDto.getId(), responseBody.getId());
        assertEquals(updatedSessionDto.getName(), responseBody.getName());
    }


    @Test
    @DisplayName("Devrait supprimer une session avec succès")
    void shouldDeleteSessionSuccessfully() {
        // ARRANGE: Préparation des mocks pour la suppression d'une session
        when(mockSessionService.getById(1L)).thenReturn(sessionExample);
        doNothing().when(mockSessionService).delete(1L);

        // ACT: Appel de la méthode à tester
        ResponseEntity<?> result = sessionController.save("1");

        // ASSERT: Vérification que la session a été supprimée avec succès
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    @DisplayName("Devrait réussir à participer à une session")
    void shouldSuccessfullyParticipate() {
        // ARRANGE: Préparation des mocks pour la participation à une session
        Long sessionId = 1L;
        Long userId = 1L;
        doNothing().when(mockSessionService).participate(sessionId, userId);

        // ACT: Appel de la méthode à tester
        ResponseEntity<?> result = sessionController.participate(sessionId.toString(), userId.toString());

        // ASSERT: Vérification que la participation a été effectuée avec succès
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    @DisplayName("Devrait réussir à ne plus participer à une session")
    void shouldSuccessfullyNoLongerParticipate() {
        // ARRANGE: Préparation des mocks pour annuler la participation à une session
        Long sessionId = 1L;
        Long userId = 1L;
        doNothing().when(mockSessionService).noLongerParticipate(sessionId, userId);
    
        // ACT: Appel de la méthode à tester
        ResponseEntity<?> result = sessionController.noLongerParticipate(sessionId.toString(), userId.toString());
    
        // ASSERT: Vérification que l'annulation de la participation a été effectuée avec succès
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }
}