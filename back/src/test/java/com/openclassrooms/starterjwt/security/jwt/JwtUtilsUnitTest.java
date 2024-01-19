package com.openclassrooms.starterjwt.security.jwt;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@SpringBootTest
public class JwtUtilsUnitTest {

    @Autowired
    private JwtUtils jwtUtils;

    private UserDetailsImpl user;

    @BeforeEach
    void setUp(TestInfo testInfo) {
        user = new UserDetailsImpl(1L, "yoga@studio.com", "admin", "User admin", true, "password");
        System.out.println("Running: " + testInfo.getDisplayName());
    }

    @Test
    void shouldGenerateValidJwtToken() {
        // ARRANGE : Préparation des données pour le test.
        // Ici, on crée un token JWT pour l'utilisateur 'user'.
        String token = jwtUtils.generateJwtToken(new UsernamePasswordAuthenticationToken(user, null));

        // ASSERT : Vérification des résultats.
        // On s'attend à ce que la méthode 'validateJwtToken' renvoie 'true' car le token devrait être valide.
        assertTrue(jwtUtils.validateJwtToken(token), "Le token JWT devrait être valide");
    }

    @Test
    void shouldDetectInvalidSignatureInJwtToken() {
        // ARRANGE : Création d'un token avec une signature incorrecte.
        String fakeToken = Jwts.builder()
                .setSubject("yoga@studio.com")
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + 86400000))
                .signWith(SignatureAlgorithm.HS512, "incorrectSecret")
                .compact();

        // ASSERT : On s'attend à ce que la méthode 'validateJwtToken' renvoie 'false' car la signature du token est incorrecte.
        assertFalse(jwtUtils.validateJwtToken(fakeToken), "Le token avec une signature invalide devrait être rejeté");
    }

    @Test
    void shouldDetectExpiredJwtToken() {
        // ARRANGE : Création d'un token JWT expiré.
        String expiredToken = Jwts.builder()
                .setSubject("yoga@studio.com")
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() - 1000))
                .signWith(SignatureAlgorithm.HS512, "openclassrooms")
                .compact();

        // ASSERT : On s'attend à ce que la méthode 'validateJwtToken' renvoie 'false' car le token est expiré.
        assertFalse(jwtUtils.validateJwtToken(expiredToken), "Le token expiré devrait être rejeté");
    }

    @Test
    void shouldRejectMalformedJwtToken() {
        // ASSERT : On s'attend à ce que la méthode 'validateJwtToken' renvoie 'false' pour un token malformé.
        assertFalse(jwtUtils.validateJwtToken("invalidTokenFormat"), "Un token JWT malformé devrait être rejeté");
    }

    @Test
    void shouldRejectEmptyJwtToken() {
        // ASSERT : On s'attend à ce que la méthode 'validateJwtToken' renvoie 'false' pour un token vide.
        assertFalse(jwtUtils.validateJwtToken(""), "Un token JWT vide devrait être rejeté");
    }
}