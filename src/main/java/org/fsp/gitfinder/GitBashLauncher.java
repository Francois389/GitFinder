/*
 * GitBashLauncher.java                                  21 mars 2024
 * IUT de Rodez, pas de droit d'auteur
 */

package org.fsp.gitfinder;

import org.fsp.gitfinder.model.ModelPrincipal;
import org.fsp.gitfinder.model.Repository;

import java.io.File;
import java.io.IOException;

/**
 * Classe permettant de lancer GitBash.
 *
 * @author François de Saint Palais
 */
public class GitBashLauncher {
    /**
     * Lance GitBash avec le chemin du repository sélectionné.
     */
    public static void launch() {

        ModelPrincipal modelPrincipal = ModelPrincipal.getInstance();

        String gitBashPath = modelPrincipal.getGitBashPath();
        Repository repository = modelPrincipal.getRepositorySelectionner();

        ProcessBuilder processBuilder = new ProcessBuilder(gitBashPath);
        processBuilder.directory(new File(repository.getChemin()));
        try {
            processBuilder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
