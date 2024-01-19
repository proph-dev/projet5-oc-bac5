package com.openclassrooms.starterjwt.controllers;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

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
public class UserControllerIntTest {
        @Autowired
        MockMvc mockMvc;

        String token;

        @BeforeAll
        public void setup() throws Exception {
        // Connexion en tant qu'administrateur pour obtenir un token
        String adminLoginJson = "{\"email\": \"yoga@studio.com\", \"password\": \"test!1234\"}";
        MvcResult adminLoginResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(adminLoginJson))
                .andExpect(status().isOk())
                .andReturn();
        String adminToken = "Bearer " + JsonPath.read(adminLoginResult.getResponse().getContentAsString(), "$.token");

        // Utiliser le token admin pour créer un utilisateur
        String regularUserJson = "{\"email\":\"yoga@studio.com\", \"password\":\"test!1234\"}";
        mockMvc.perform(post("/api/user")
                .header("Authorization", adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(regularUserJson))
                .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Test find user by Id")
        public void testFindUserById() throws Exception {
            mockMvc.perform(get("/api/user/1")
                    .header("Authorization", token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("lastName", is("tata")))
                    .andDo(print());
        }

        @Test
        @DisplayName("Test findById et retourne Not Found")
        public void testUserFindByIdNotFound() throws Exception {
                mockMvc.perform(get("/api/user/99999")
                                .header("Authorization", token))
                                .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Test findById et retourne Bad Request")
        public void testUserFindByIdBadRequest() throws Exception {
                mockMvc.perform(get("/api/user/tata")
                                .header("Authorization", token))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Test findById et retourne Unauthorized")
        public void testUserFindByIdUnauthorized() throws Exception {
                mockMvc.perform(get("/api/user/1")
                                .header("Authorization", "WrongToken"))
                                .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Test delete OK")
        public void testDeleteUserOK() throws Exception {
                String requestBodyLoginUser = "{" +
                                "    \"email\": \"tutu@gmail.com\"," +
                                "    \"password\": \"test!1234\"" +
                                "}";
                MvcResult resultLogin = mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBodyLoginUser))
                                .andReturn();
                String tokenForUserToDelete = "Bearer "
                                + JsonPath.read(resultLogin.getResponse().getContentAsString(), "$.token");
                mockMvc.perform(delete("/api/user/2")
                                .header("Authorization", tokenForUserToDelete))
                                .andExpect(status().isOk())
                                .andDo(print());
        }

        @Test
        @DisplayName("Test delete un user inexistant et retourne Not Found")
        public void testDeleteNotFound() throws Exception {
                mockMvc.perform(delete("/api/user/99999")
                                .header("Authorization", this.token))
                                .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Test delete un user et retourne Bad Request")
        public void testDeleteBadRequest() throws Exception {
                mockMvc.perform(delete("/api/user/tata")
                                .header("Authorization", this.token))
                                .andExpect(status().isBadRequest());
        }
}