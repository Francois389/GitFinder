package org.fsp.gitfinder.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class ModelPrincipalTest {

    ModelPrincipal model;

    @BeforeEach
    public void setUp() {
        model = ModelPrincipal.getInstance();
        model.setRepositories(new HashSet<>());
    }

    @Test
    @DisplayName("Ajouter un repository dans la liste vide")
    void ajouterRepository() {
        //Given un ModelPrincipal avec une liste vide et un repository
        Repository repository = new Repository("E:\\GitFinder", "GitFinder", "", "");

        //When on ajoute un repository
        model.ajouterRepository(repository);

        //Then le repository est bien ajouté
        assertTrue(model.getRepositories().contains(repository));
    }

    @Test
    @DisplayName("Ajouter un repository déjà existant et identique")
    void ajouterRepositoryDejaExistant() {
        //Given un ModelPrincipal avec une liste contenant un repository et un autre repository
        Repository repository = new Repository("E:\\GitFinder", "GitFinder", "", "");
        model.ajouterRepository(repository);
        Repository repository2 = new Repository(repository.getChemin(), repository.getNom(), repository.getDescription(), repository.getURLImage());

        //When, on ajoute un repository déjà existant
        //Then une exception est levée
        assertThrows(IllegalArgumentException.class, () -> model.ajouterRepository(repository2));
        assertEquals(repository, repository2);
    }

    @Test
    @DisplayName("Ajouter un repository déjà existant mais dont la description est différente léve une exception")
    void ajouterRepositoryDejaExistantDescriptionDifferent() {
        //Given un ModelPrincipal avec une liste contenant un repository et un autre repository
        Repository repository = new Repository("E:\\GitFinder", "GitFinder", "", "");
        model.ajouterRepository(repository);
        Repository repository2 = new Repository(repository.getChemin(), repository.getNom(), "Description différente", repository.getURLImage());

        //When, on ajoute un repository déjà existant
        //Then une exception est levée
        assertThrows(IllegalArgumentException.class, () -> model.ajouterRepository(repository2));
        assertEquals(repository, repository2);
    }


}