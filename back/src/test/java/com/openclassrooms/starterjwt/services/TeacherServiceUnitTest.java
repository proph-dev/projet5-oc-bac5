package com.openclassrooms.starterjwt.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootTest
public class TeacherServiceUnitTest {

    @Mock
    private TeacherRepository teacherRepository;

    @InjectMocks
    private TeacherService teacherService;

    private Teacher mockTeacher;

    @BeforeEach
    public void setUp() {
        mockTeacher = new Teacher();
        mockTeacher.setId(1L);
        mockTeacher.setLastName("DELAHAYE");
        mockTeacher.setFirstName("Margot");

        List<Teacher> teachersList = new ArrayList<>();
        teachersList.add(mockTeacher);
        
        when(teacherRepository.findAll()).thenReturn(teachersList);
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(mockTeacher));
    }

    @Test
    @DisplayName("Récupérer la liste des teachers")
    public void testFindAll() {
        // ACT : Appelle findAll sur teacherService pour obtenir la liste des enseignants.
        List<Teacher> result = teacherService.findAll();

        // ASSERT : Appelle findAll sur teacherService pour obtenir la liste des enseignants.
        assertFalse(result.isEmpty(), "La liste des enseignants ne devrait pas être vide");
        assertEquals(mockTeacher, result.get(0), "Le premier enseignant devrait correspondre au mockTeacher");
        verify(teacherRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Récupérer un teacher par son Id")
    public void testFindById() {
        // ACT : Appelle findById sur teacherService avec un ID valide pour obtenir un enseignant.
        Teacher resultTeacher = teacherService.findById(1L);

        // ASSERT : Vérifie qu'un enseignant est retourné, qu'il correspond au mockTeacher, et que findById du teacherRepository a été appelée une fois avec l'ID spécifié.
        assertNotNull(resultTeacher, "Un enseignant devrait être trouvé");
        assertEquals(mockTeacher, resultTeacher, "L'enseignant trouvé devrait correspondre au mockTeacher");
        verify(teacherRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Récupérer un teacher par un Id inexistant")
    public void testFindById_NotFound() {
        // ARRANGE : Configure le teacherRepository pour renvoyer un résultat vide pour un ID non existant.
        when(teacherRepository.findById(2L)).thenReturn(Optional.empty());

        // ACT : Appelle findById sur teacherService avec un ID inexistant.
        Teacher resultTeacher = teacherService.findById(2L);

        // ASSERT : Vérifie qu'aucun enseignant n'est retourné pour l'ID inexistant et que findById du teacherRepository a été appelée une fois avec cet ID.
        assertNull(resultTeacher, "Aucun enseignant ne devrait être trouvé avec un ID inexistant");
        verify(teacherRepository, times(1)).findById(2L);
    }
}