/*
 * Repository.java                                  20 mars 2024
 * IUT de Rodez, pas de droit d'auteur
 */

package org.fsp.gitfinder.model;

import javafx.scene.image.Image;

import java.io.File;
import java.io.Serializable;
import java.util.Objects;

/**
 * @author François de Saint Palais
 */
public class Repository implements Serializable {

    private String chemin;
    private String nom;
    private String description;
    private String cheminImage;

    public Repository(String chemin, String nom, String description, String cheminImage) throws IllegalArgumentException {
        cheminRepositoryValide(chemin);

        if (nom == null || nom.isEmpty()) {
            throw new IllegalArgumentException("Le nom du repositorie ne doit pas être vide");
        }

        this.chemin = chemin;
        this.nom = nom;
        this.description = description;
        this.cheminImage = cheminImage;
    }

    public Repository(String chemin, String nom, String description) throws IllegalArgumentException {
        this(chemin, nom, description, null);
    }

    public Repository(String chemin, String nom) throws IllegalArgumentException {
        this(chemin, nom, null, null);
    }

    /**
     * Vérifie que le chemin est un repository valide.<br>
     * Si le chemin n'existe pas, lève une exception.<br>
     * Si le chemin existe, mais ne contient pas de dossier <b><i>.git</i></b>, lève une exception.
     * Sinon, le chemin est valide.
     *
     * @param chemin le chemin à vérifier
     */
    public static void cheminRepositoryValide(String chemin) throws IllegalArgumentException {
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

    public String getURLImage() {
        return cheminImage;
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
        return result;
    }

    @Override
    public String toString() {
        return STR."\{nom} : (\{chemin})\n\{cheminImage}\n";
    }

    public void setNom(String nom) {
        if (nom == null || nom.isEmpty()) {
            throw new IllegalArgumentException("Le nom du repositorie ne doit pas être vide");
        }
        this.nom = nom;
    }

    public void setChemin(String chemin) {
        cheminRepositoryValide(chemin);

        this.chemin = chemin;
    }

    public void setDescription(String text) {
        this.description = text;
    }

    public void setImage(Image image) {
        if (image != null) {
            this.setCheminImage(image.getUrl());
        }
    }

    public void setCheminImage(String url) {
        this.cheminImage = url;
    }
}
