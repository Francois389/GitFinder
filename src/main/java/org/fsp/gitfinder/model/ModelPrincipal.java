/*
 * ModelPrincipal.java                                  20 mars 2024
 * IUT de Rodez, pas de droit d'auteur
 */

package org.fsp.gitfinder.model;

import java.util.HashSet;

/**
 * @author François de Saint Palais
 */
public class ModelPrincipal {

    private static ModelPrincipal instance;

    private String gitBashPath;

    private HashSet<Repository> repositories;

    private Repository repositorySelectionner;

    private Repository repositoryAModifier;

    private ModelPrincipal() {
        repositories = new HashSet<>();
    }

    public static ModelPrincipal getInstance() {
        if (instance == null) {
            instance = new ModelPrincipal();
        }
        return instance;
    }

    public String getGitBashPath() {
        return gitBashPath;
    }

    public void setGitBashPath(String gitBashPath) {
        this.gitBashPath = gitBashPath;
    }

    public HashSet<Repository> getRepositories() {
        return repositories;
    }

    public void setRepositories(HashSet<Repository> repositories) {
        this.repositories = repositories;
    }

    public Repository getRepositorySelectionner() {
        return repositorySelectionner;
    }

    public void setRepositorySelectionner(Repository repositorySelectionner) {
        this.repositorySelectionner = repositorySelectionner;
    }

    /**
     * Ajoute un repository à la liste des repositories.
     * @param repository le repository à ajouter
     * @throws IllegalArgumentException si le repository existe déjà
     */
    public void ajouterRepository(Repository repository) throws IllegalArgumentException {
        boolean estAjouter = repositories.add(repository);
        if (!estAjouter) {
            throw new IllegalArgumentException("Le repository existe déjà");
        }
    }

    public void setRepositoryAModifier(Repository repository) {
        repositoryAModifier = repository;
    }

    public Repository getRepositoryAModifier() {
        return repositoryAModifier;
    }
}
