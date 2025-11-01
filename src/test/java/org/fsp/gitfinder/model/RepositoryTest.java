package org.fsp.gitfinder.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings({"ConstantValue", "EqualsWithItself"})
class RepositoryTest {

    private Repository repository;
    File selfPath = new File("").getAbsoluteFile();

    @BeforeEach
    void setUp() {
        repository = new Repository(selfPath.getAbsolutePath(), "GitFinder", "", "");
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
        assertThrows(IllegalArgumentException.class, () -> new Repository(chemin, nom, ""));
        assertThrows(IllegalArgumentException.class, () -> new Repository(chemin, nom));
    }

    @Test
    @DisplayName("Le nom du repository est invalide")
    void testConstructeurNomInvalide() {
        //Given un nom vide ou null
        String chemin = selfPath.getAbsolutePath();

        String nomVide = "";
        String nomNull = null;

        //When, on crée un Repository avec ce nom
        //Then une exception est levée
        assertThrows(IllegalArgumentException.class, () -> new Repository(chemin, nomVide, "", ""));
        assertThrows(IllegalArgumentException.class, () -> new Repository(chemin, nomVide, ""));
        assertThrows(IllegalArgumentException.class, () -> new Repository(chemin, nomVide));

        assertThrows(IllegalArgumentException.class, () -> new Repository(chemin, nomNull, "", ""));
        assertThrows(IllegalArgumentException.class, () -> new Repository(chemin, nomNull, ""));
        assertThrows(IllegalArgumentException.class, () -> new Repository(chemin, nomNull));
    }

    @Test
    @DisplayName("Le chemin ne contient pas de dossier .git")
    void testConstructeurCheminSansDossierGit() {
        //Given un chemin sans dossier .git
        String chemin = new File("/src").getAbsolutePath();
        String nom = "GitFinder";

        //When, on crée un Repository avec ce chemin
        //Then une exception est levée
        assertThrows(IllegalArgumentException.class, () -> new Repository(chemin, nom, "", ""));
        assertThrows(IllegalArgumentException.class, () -> new Repository(chemin, nom, ""));
        assertThrows(IllegalArgumentException.class, () -> new Repository(chemin, nom));
    }

    @Test
    @DisplayName("Les paramètres sont valides")
    void testConstructeurParametresValides() {
        //Given des paramètres valides
        String chemin = selfPath.getAbsolutePath();
        String nom = "GitFinder";

        //When, on crée un Repository avec ces paramètres
        //Then le Repository est créé, sans exception
        assertDoesNotThrow(() -> new Repository(chemin, nom, "", ""));
        assertDoesNotThrow(() -> new Repository(chemin, nom, ""));
        assertDoesNotThrow(() -> new Repository(chemin, nom));
    }


    @Test
    @DisplayName("Les deux Repository ont le même nom et le même chemin")
    void testEqualsMemeAttribut() {
        //Given un Repository avec les mêmes attributs
        Repository repository2 = new Repository(repository.getChemin(), repository.getNom(), repository.getDescription(), repository.getURLImage());

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

    @Test
    @DisplayName("Deux Repository qui ont des descriptions différentes sont identiques")
    void testEqualsDescriptionDifferentes() {
        //Given un Repository avec une description
        Repository repository2 = new Repository(repository.getChemin(), repository.getNom(), "Description");

        //When, on compare les deux Repository
        //Then les deux Repository sont égaux
        assertNotEquals(repository.getDescription(), repository2.getDescription());
        assertEquals(repository, repository2);
    }

    @Test
    @DisplayName("Deux Repository qui ont des images différentes sont identiques")
    void testEqualsImagesDifferentes() {
        //Given un Repository avec une image
        Repository repository2 = new Repository(repository.getChemin(), repository.getNom(), "", "image");

        //When, on compare les deux Repository
        //Then les deux Repository sont égaux
        assertNotEquals(repository.getURLImage(), repository2.getURLImage());
        assertEquals(repository, repository2);
    }
}