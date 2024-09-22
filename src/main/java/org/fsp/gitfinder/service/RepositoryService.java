/*
 * RepositoryService.java                                  17 Jun 2024
 * IUT de Rodez, pas de droit d'auteur
 */

package org.fsp.gitfinder.service;

import org.fsp.gitfinder.model.ModelPrincipal;
import org.fsp.gitfinder.model.Repository;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Set;

/**
 * @author François de Saint Palais
 */
public class RepositoryService {

    private static final ModelPrincipal model = ModelPrincipal.getInstance();
    public static final String GIT_PATH_KEY = "gitPath";
    public static final String REPOSITORIES_KEY = "repositories";
    public static final String JSON_SAVE = "save.json";

    public static void sauvegarder() {
        JSONObject json = new JSONObject();
        JSONArray jsonRepositories = new JSONArray();

        String gitConsolPath = model.getGitBashPath();
        Set<Repository> repositories = model.getRepositories();

        for (Repository repo : repositories) {
            jsonRepositories.put(new JSONObject(repo));
        }

        json.put(GIT_PATH_KEY, gitConsolPath);
        json.put(REPOSITORIES_KEY, jsonRepositories);
        sauvegarderInFile(json, JSON_SAVE);
    }

    public static void sauvegarderInFile(JSONObject jsonObject, String fileSaveName) {
        try (
            FileOutputStream fileOut = new FileOutputStream(fileSaveName);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
        ) {
            System.out.printf(jsonObject.toString());

            out.writeObject(jsonObject.toString());
        } catch (IOException i) {
            i.printStackTrace();
        }

        System.out.println("Sauvegarde effectuée");
    }
}
