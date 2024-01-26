package com.openclassrooms.starterjwt.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.jayway.jsonpath.JsonPath;

import lombok.experimental.var;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public class SessionControllerIntTest {

    @Autowired
    private MockMvc mockMvc;

    private String token;
    private int userId;

    private static final String SESSION_ENDPOINT = "/api/session/";
    private static final String AUTH_ENDPOINT = "/api/auth/login";
    private static final String TEST_EMAIL = "toto@gmail.com";
    private static final String TEST_PASSWORD = "test!1234";

    @BeforeEach
    public void setup() throws Exception {
        String loginPayload = String.format("{\"email\": \"%s\", \"password\": \"%s\"}", TEST_EMAIL, TEST_PASSWORD);
        var loginResult = mockMvc.perform(post(AUTH_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginPayload))
                .andReturn();
        token = "Bearer " + JsonPath.read(loginResult.getResponse().getContentAsString(), "$.token");
        userId = JsonPath.read(loginResult.getResponse().getContentAsString(), "$.id");
    }

    @Test
    @DisplayName("Créer une session et vérifier")
    public void createSessionAndVerify() throws Exception {
        String sessionJson = "{\"name\": \"Session pour les nouveaux\", \"date\": \"2012-01-01\", \"teacher_id\": 1, \"users\": null, \"description\": \"Session for kids\"}";
        mockMvc.perform(post(SESSION_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token)
                .content(sessionJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Session pour les nouveaux")));
    }

    @Test
    @DisplayName("Récupérer la session par identifiant")
    public void retrieveSessionById() throws Exception {
        String sessionJson = "{\"name\": \"Specific Session\", \"date\": \"2023-01-01\", \"teacher_id\": 1, \"users\": null, \"description\": \"Specific session for test\"}";
        MvcResult result = mockMvc.perform(post(SESSION_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token)
                .content(sessionJson))
                .andExpect(status().isOk())
                .andReturn();

        String responseString = result.getResponse().getContentAsString();
        int createdSessionId = JsonPath.parse(responseString).read("$.id", Integer.class);

        mockMvc.perform(get(SESSION_ENDPOINT + createdSessionId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Specific Session")));
    }

    @Test
    @DisplayName("Rechercher une session par identifiant - Non trouvé")
    public void findSessionByIdNotFound() throws Exception {
        mockMvc.perform(get(SESSION_ENDPOINT + "99999")
                .header("Authorization", token))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Rechercher une session par ID - Mauvaise requête")
    public void findSessionByIdBadRequest() throws Exception {
        mockMvc.perform(get(SESSION_ENDPOINT + "invalidId")
                .header("Authorization", token))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Récupérer toutes les sessions - Ajuster les attentes")
    public void retrieveAllSessions() throws Exception {
        mockMvc.perform(get(SESSION_ENDPOINT)
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is("Session pour les nouveaux")));
    }

    @Test
    @DisplayName("Mise à jour de la session et vérification")
    public void updateSessionAndVerify() throws Exception {
        String updatedSessionJson = "{\"name\": \"Advanced Session\", \"date\": \"2023-12-01\", \"teacher_id\": 2, \"users\": null, \"description\": \"Session for advanced learners\"}";
        mockMvc.perform(put(SESSION_ENDPOINT + "2")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token)
                .content(updatedSessionJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Advanced Session")));
    }

    @Test
    @DisplayName("Session de mise à jour - Mauvaise demande")
    public void updateSessionBadRequest() throws Exception {
        mockMvc.perform(put(SESSION_ENDPOINT + "invalidId")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Supprimer la session - S'assurer que la session existe")
    public void deleteSession() throws Exception {
        String sessionJson = "{\"name\": \"Temp Session\", \"date\": \"2023-01-01\", \"teacher_id\": 1, \"users\": null, \"description\": \"Temporary session\"}";
        MvcResult result = mockMvc.perform(post(SESSION_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token)
                .content(sessionJson))
                .andExpect(status().isOk())
                .andReturn();

        String responseString = result.getResponse().getContentAsString();
        int createdSessionId = JsonPath.parse(responseString).read("$.id", Integer.class);

        mockMvc.perform(delete(SESSION_ENDPOINT + createdSessionId)
                .header("Authorization", token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Supprimer la session - Non trouvé")
    public void deleteSessionNotFound() throws Exception {
        mockMvc.perform(delete(SESSION_ENDPOINT + "99999")
                .header("Authorization", token))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Supprimer la session - Mauvaise demande")
    public void deleteSessionBadRequest() throws Exception {
        mockMvc.perform(delete(SESSION_ENDPOINT + "invalidId")
                .header("Authorization", token))
                .andExpect(status().isBadRequest());
    }

    @Test
        @DisplayName("Participer à la session - S'assurer que la session et l'utilisateur existent")
        public void participateInSession() throws Exception {
        // Créer une nouvelle session et récupérer son ID
        String sessionJson = "{\"name\": \"Nouvelle sessions\", \"date\": \"2023-01-01\", \"teacher_id\": 1, \"users\": null, \"description\": \"Nouvelle session pour test\"}";
        MvcResult result = mockMvc.perform(post(SESSION_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token)
                .content(sessionJson))
                .andExpect(status().isOk())
                .andReturn();

        String responseString = result.getResponse().getContentAsString();
        int createdSessionId = JsonPath.parse(responseString).read("$.id", Integer.class);

        // Participer à la session créée
        mockMvc.perform(post(SESSION_ENDPOINT + createdSessionId + "/participate/" + userId)
                .header("Authorization", token))
                .andExpect(status().isOk());
        }

    @Test
    @DisplayName("NNe participe plus à la session - S'assurer de l'existence de la session et de l'utilisateur")
    public void noLongerParticipateInSession() throws Exception {
        mockMvc.perform(delete(SESSION_ENDPOINT + "5/participate/" + userId)
                .header("Authorization", token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Participer à la session - Mauvaise demande")
    public void participateInSessionBadRequest() throws Exception {
        mockMvc.perform(post(SESSION_ENDPOINT + "invalidId/participate/invalidUserId")
                .header("Authorization", token))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Ne plus participer à la session - Mauvaise demande")
    public void noLongerParticipateInSessionBadRequest() throws Exception {
        mockMvc.perform(delete(SESSION_ENDPOINT + "invalidId/participate/invalidUserId")
                .header("Authorization", token))
                .andExpect(status().isBadRequest());
    }
}