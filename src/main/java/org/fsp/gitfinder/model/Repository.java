/*
 * Repositorie.java                                  20 mars 2024
 * IUT de Rodez, pas de droit d'auteur
 */

package org.fsp.gitfinder.model;

import java.io.File;
import java.util.Objects;

/**
 * @author François de Saint Palais
 */
public class Repository {

    private String chemin;
    private String nom;
    private String description;
    private String image;

    public Repository(String chemin, String nom, String description, String image) {
        cheminExiste(chemin);

        if (nom == null || nom.isEmpty()) {
            throw new IllegalArgumentException("Le nom du repositorie ne doit pas être vide");
        }

        this.chemin = chemin;
        this.nom = nom;
        this.description = description;
        this.image = image;
    }

    /**
     * Vérifie que le chemin existe.
     * Si le chemin n'existe pas, lève une exception.
     *
     * @param chemin le chemin à vérifier
     */
    private void cheminExiste(String chemin) {
        if (chemin == null) {
            throw new IllegalArgumentException("Le chemin ne peut pas être null");
        }
        File file = new File(chemin);
        if (!file.exists()) {
            throw new IllegalArgumentException("Le chemin n'existe pas");
        }

        File git = new File(chemin, ".git");
        if (!git.exists()) {
            throw new IllegalArgumentException("Le chemin ne contient pas de dossier .git");
        }
    }

    public String getChemin() {
        return chemin;
    }

    public String getNom() {
        return nom;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    @Override
    public boolean equals(Object autreObject) {
        if (this == autreObject) {
            return true;
        }
        if (autreObject == null || getClass() != autreObject.getClass()) {
            return false;
        }

        Repository autreRepo = (Repository) autreObject;

        return Objects.equals(chemin, autreRepo.chemin) && Objects.equals(nom, autreRepo.nom);
    }

    @Override
    public int hashCode() {
        int result = chemin != null ? chemin.hashCode() : 0;
        result = 31 * result + (nom != null ? nom.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (image != null ? image.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        String s = STR."\{nom} : (\{chemin})";
        return s;
    }
}
