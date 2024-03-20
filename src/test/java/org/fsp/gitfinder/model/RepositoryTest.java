package org.fsp.gitfinder.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RepositoryTest {

    @Test
    @DisplayName("Le chemin spécifié n'existe pas")
    void testConstructeurCheminInvalide() {
        //Given un chemin invalide
        String chemin = "Q:\\Projet\\";
        String nom = "Projet";

        //When, on crée un Repository avec ce chemin
        //Then une exception est levée
        assertThrows(IllegalArgumentException.class, () -> new Repository(chemin, nom, "", ""));
    }

    @Test
    @DisplayName("Le nom du repository est invalide")
    void testConstructeurNomInvalide() {
        //Given un nom vide ou null
        String chemin = "E:\\GitFinder\\";

        String nomVide = "";
        String nomNull = null;

        //When, on crée un Repository avec ce nom
        //Then une exception est levée
        assertThrows(IllegalArgumentException.class, () -> new Repository(chemin, nomVide, "", ""));
        assertThrows(IllegalArgumentException.class, () -> new Repository(chemin, nomNull, "", ""));
    }

    @Test
    @DisplayName("Le chemin ne contient pas de dossier .git")
    void testConstructeurCheminSansDossierGit() {
        //Given un chemin sans dossier .git
        String chemin = "E:\\GitFinder\\src\\";
        String nom = "GitFinder";

        //When, on crée un Repository avec ce chemin
        //Then une exception est levée
        assertThrows(IllegalArgumentException.class, () -> new Repository(chemin, nom, "", ""));
    }

    @Test
    @DisplayName("Les paramètres sont valides")
    void testConstructeurParametresValides() {
        //Given des paramètres valides
        String chemin = "E:\\GitFinder";
        String nom = "GitFinder";

        //When, on crée un Repository avec ces paramètres
        //Then le Repository est créé, sans exception
        assertDoesNotThrow(() -> new Repository(chemin, nom, "", ""));
    }





    @Test
    void testEquals() {
    }
}