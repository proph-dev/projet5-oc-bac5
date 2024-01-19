package com.openclassrooms.starterjwt.controllers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@TestInstance(Lifecycle.PER_CLASS)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthControllerIntTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    public void setup() throws Exception {
        // Créer un utilisateur régulier pour les tests
        String regularUserJson = "{\"email\":\"yoga@studio.com\",\"password\":\"test!1234\"}";
        mockMvc.perform(post("/api/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(regularUserJson))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("La connexion à l'administration devrait réussir")
    void adminLoginSuccess() throws Exception {
        String adminJson = "{\"email\":\"yoga@studio.com\",\"password\":\"test!1234\"}";
        
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(adminJson))
                                .andExpect(status().isOk())
                                .andReturn();
        
        String responseContent = result.getResponse().getContentAsString();
        assertTrue(responseContent.contains("\"admin\":true"));
    }

    @Test
    @DisplayName("L'enregistrement de l'utilisateur devrait être réussi")
    void userRegistrationSuccess() throws Exception {
        String uniqueEmail = "user" + System.currentTimeMillis() + "@example.com";
        String newUserJson = String.format("{\"lastName\":\"tata\",\"firstName\":\"tutu\",\"email\":\"%s\",\"password\":\"test!1234\"}", uniqueEmail);

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(newUserJson))
                                .andExpect(status().isOk())
                                .andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("User registered successfully!"));
    }

    @Test
    @DisplayName("La connexion d'un utilisateur normal devrait réussir")
    void regularUserLoginSuccess() throws Exception {
        String userJson = "{\"email\":\"toto@gmail.com\",\"password\":\"test!1234\"}";
        
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(userJson))
                                .andExpect(status().isOk())
                                .andReturn();
        
        assertTrue(result.getResponse().getContentAsString().contains("\"admin\":false"));
    }

    @Test
    @DisplayName("La connexion avec un mot de passe incorrect doit échouer")
    void loginWithIncorrectPassword() throws Exception {
        String incorrectPasswordJson = "{\"email\":\"toto@gmail.com\",\"password\":\"wrongpassword\"}";
        
        mockMvc.perform(post("/api/auth/login")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(incorrectPasswordJson))
                       .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("L'enregistrement avec un courriel existant doit échouer")
    void registrationWithExistingEmail() throws Exception {
        String existingEmailJson = "{\"lastName\":\"toto\",\"firstName\":\"titi\",\"email\":\"toto@gmail.com\",\"password\":\"test!1234\"}";
        
        MvcResult result = mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(existingEmailJson))
                                .andExpect(status().isBadRequest())
                                .andReturn();
        
        assertTrue(result.getResponse().getContentAsString().contains("Error: Email is already taken!"));
    }
}