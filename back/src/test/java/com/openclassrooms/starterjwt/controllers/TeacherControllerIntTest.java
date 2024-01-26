package com.openclassrooms.starterjwt.controllers;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.jayway.jsonpath.JsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public class TeacherControllerIntTest {
        @Autowired
        MockMvc mockMvc;

        String token;

        @BeforeAll
        public void setup() throws Exception {
                // Connexion pour obtenir un token valide
                String requestBodyLoginUser = "{\"email\":\"toto@gmail.com\", \"password\":\"test!1234\"}";
                MvcResult resultLogin = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyLoginUser))
                        .andReturn();

                token = "Bearer " + JsonPath.read(resultLogin.getResponse().getContentAsString(), "$.token");
        }

        @Test
        @DisplayName("Test de recherche d'un enseignant par Id")
        public void testRetrieveTeacherById() throws Exception {
                mockMvc.perform(get("/api/teacher/1")
                                .header("Authorization", token))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("lastName", is("DELAHAYE")));
        }

        @Test
        @DisplayName("Test de recherche d'enseignant par Id Not Found")
        public void testTeacherNotFoundById() throws Exception {
                mockMvc.perform(get("/api/teacher/999")
                                .header("Authorization", token))
                                .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Test de recherche d'enseignant par Id Bad Request")
        public void testBadRequestForInvalidTeacherId() throws Exception {
                mockMvc.perform(get("/api/teacher/toto")
                                .header("Authorization", token))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Test pour trouver tous les enseignants")
        public void testListAllTeachers() throws Exception {
                mockMvc.perform(get("/api/teacher/")
                                .header("Authorization", token))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].id").value(1))
                                .andExpect(jsonPath("$[0].lastName", is("DELAHAYE")))
                                .andExpect(jsonPath("$[1].id").value(2))
                                .andExpect(jsonPath("$[1].lastName", is("THIERCELIN")));
        }

}