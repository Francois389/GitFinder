package org.fsp.gitfinder.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RepositoryTest {

    private Repository repository;

    @BeforeEach
    void setUp() {
        repository = new Repository("E:\\GitFinder", "GitFinder", "", "");
    }

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
    @DisplayName("Les deux Repository ont le même nom et le même chemin")
    void testEqualsMemeAttribut() {
        //Given un Repository avec les mêmes attributs
        Repository repository2 = new Repository(repository.getChemin(), repository.getNom(), "", "");

        //When, on compare les deux Repository
        //Then les deux Repository sont égaux
        assertEquals(repository, repository2);
    }

    @Test
    @DisplayName("Un Repository est égal à lui-même")
    void testEqualsMemeObjet() {
        //Given un Repository
        //When, on compare le Repository à lui-même
        //Then le Repository est égal à lui-même
        assertEquals(repository, repository);
    }
}