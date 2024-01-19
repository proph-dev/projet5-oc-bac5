package com.openclassrooms.starterjwt.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.mapper.TeacherMapper;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.services.TeacherService;

@ExtendWith(MockitoExtension.class)
public class TeacherControllerUnitTest {

    @Mock
    private TeacherService teacherService;

    @Mock
    private TeacherMapper teacherMapper;

    @InjectMocks
    private TeacherController teacherController;

    private Teacher exampleTeacher;
    private TeacherDto exampleTeacherDto;

    @BeforeEach
    public void setup() {
        exampleTeacher = new Teacher(1L, "admin", "admin", LocalDateTime.now(), LocalDateTime.now());
        exampleTeacherDto = new TeacherDto(1L, "admin", "admin", LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    @DisplayName("Trouver par ID devrait retourner un enseignant")
    void findByIdShouldReturnTeacher() {
        // ARRANGE : Configurer les mocks pour retourner un enseignant spécifique
        when(teacherService.findById(1L)).thenReturn(exampleTeacher);
        when(teacherMapper.toDto(exampleTeacher)).thenReturn(exampleTeacherDto);
    
        // ACT : Appeler la méthode findById du contrôleur
        ResponseEntity<?> response = teacherController.findById("1");
    
        // ASSERT : Vérifier que la réponse est correcte et contient les informations attendues
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        TeacherDto responseBody = (TeacherDto) response.getBody();
        assertEquals(exampleTeacher.getId(), responseBody.getId());
        // ... Reste des assertions
    }

    @Test
    @DisplayName("Trouver par ID devrait retourner un statut 'non trouvé' si l'enseignant n'existe pas")
    void findByIdShouldReturnNotFound() {
        // ARRANGE : Configurer le service pour retourner null (enseignant non trouvé)
        when(teacherService.findById(1L)).thenReturn(null);

        // ACT : Appeler la méthode findById avec un ID qui n'existe pas
        ResponseEntity<?> response = teacherController.findById("1");

        // ASSERT : Vérifier que la réponse est NOT_FOUND
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("Trouver par ID avec un ID invalide devrait retourner un statut 'mauvaise requête'")
    void findByIdWithInvalidIdShouldReturnBadRequest() {
        // ACT : Appeler la méthode findById avec un ID invalide
        ResponseEntity<?> response = teacherController.findById("invalid");

        // ASSERT : Vérifier que la réponse est BAD_REQUEST
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("findAll devrait retourner une liste d'enseignants")
    void findAllShouldReturnListOfTeachers() {
        // ARRANGE : Configurer les mocks pour retourner une liste d'enseignants
        List<Teacher> allTeachers = Arrays.asList(exampleTeacher, new Teacher(2L, "Doe", "Jane", LocalDateTime.now(), LocalDateTime.now()));
        List<TeacherDto> allTeacherDtos = Arrays.asList(exampleTeacherDto, new TeacherDto(2L, "Doe", "Jane", LocalDateTime.now(), LocalDateTime.now()));
        when(teacherService.findAll()).thenReturn(allTeachers);
        when(teacherMapper.toDto(allTeachers)).thenReturn(allTeacherDtos);

        // ACT : Appeler la méthode findAll du contrôleur
        ResponseEntity<?> response = teacherController.findAll();

        // ASSERT : Vérifier que la réponse contient la liste attendue
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<TeacherDto> responseBody = (List<TeacherDto>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(2, responseBody.size());
    }
}