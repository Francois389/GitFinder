/*
 * ModelPrincipal.java                                  20 mars 2024
 * IUT de Rodez, pas de droit d'auteur
 */

package org.fsp.gitfinder.model;

import java.util.HashSet;
import java.util.Set;

/**
 * @author François de Saint Palais
 */
public class ModelPrincipal {

    private static ModelPrincipal instance;

    private String gitBashPath;

    private HashSet<Repository> repositories;

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

    public Set<Repository> getRepositories() {
        return repositories;
    }

    public void setRepositories(HashSet<Repository> repositories) {
        this.repositories = repositories;
    }
}
