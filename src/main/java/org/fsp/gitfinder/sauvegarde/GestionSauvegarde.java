/*
 * GestionSauvegarde.java                                  21 mars 2024
 * IUT de Rodez, pas de droit d'auteur
 */

package org.fsp.gitfinder.sauvegarde;

import org.fsp.gitfinder.model.ModelPrincipal;
import org.fsp.gitfinder.model.Repository;

import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Gére la sauvegarde des données de l'application.
 *
 * @author François de Saint Palais
 */
public class GestionSauvegarde {

    private static final String FICHIER_SAUVEGARDE = "data.ser";

    private static final ModelPrincipal model = ModelPrincipal.getInstance();

    public static final Logger LOGGER = Logger.getLogger(GestionSauvegarde.class.getName());

    /**
     * Sauvegarde les données du ModelPrincipal.
     */
    public static void sauvegarde() {
        String gitPath = model.getGitBashPath();
        Set<Repository> repositories = model.getRepositories();

        // Sauvegarde des données
        try (
                FileOutputStream fileOut = new FileOutputStream(FICHIER_SAUVEGARDE);
                ObjectOutputStream out = new ObjectOutputStream(fileOut);
        ) {
            out.writeObject(repositories);
            out.writeObject(gitPath);
        } catch (IOException i) {
            i.printStackTrace();
        }
        LOGGER.info("Sauvegarde sérialisable effectuée");
    }

    /**
     * Charge les données de l'application dans le ModelPrincipal.
     * Si les données n'existent pas, renvoie false
     */
    public static boolean charge() throws IOException {
        String gitPath;
        HashSet<Repository> repositories;
        boolean success = false;

        File fichierSauvegarde = new File(FICHIER_SAUVEGARDE);

        if (!fichierSauvegarde.exists()) {
            fichierSauvegarde.createNewFile();
        } else {
            // Chargement des données
            try (
                    FileInputStream fileIn = new FileInputStream(FICHIER_SAUVEGARDE);
                    ObjectInputStream in = new ObjectInputStream(fileIn);
            ) {
                repositories = (HashSet<Repository>) in.readObject();
                gitPath = (String) in.readObject();

                LOGGER.info("Git path : " + gitPath);
                LOGGER.info("Repositories : ");
                for (Repository r : repositories) {
                    LOGGER.info(r.toString());
                }

                model.setRepositories(repositories);
                model.setGitBashPath(gitPath);

                success = true;
            } catch (EOFException e) {
                LOGGER.warning("Le fichier est vide");
            } catch (ClassNotFoundException c) {
                c.printStackTrace();
            }
        }

        return success;
    }
}
