/*
 * RepositoryCell.java                                  21 mars 2024
 * IUT de Rodez, pas de droit d'auteur
 */

package org.fsp.gitfinder.factorie;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import org.fsp.gitfinder.GitFinderApplication;
import org.fsp.gitfinder.model.Repository;

import java.io.IOException;

/**
 * @author François de Saint Palais
 */
public class RepositoryCell {
    @FXML
    public Label nomRepo;

    @FXML
    public Label chemin;

    @FXML
    public ImageView image;

    @FXML
    public Label description;

    @FXML
    public AnchorPane pane;

    /**
     * Constructeur de la classe RepositoryCellController.
     */
    public RepositoryCell() {
        FXMLLoader fxmlLoader = new FXMLLoader(GitFinderApplication.class.getResource("repositorie-item.fxml"));
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Permet de mettre à jour les informations de la cellule.
     *
     * @param repo le dépôt à afficher
     */
    public void setInfo(Repository repo) {
        nomRepo.setText(repo.getNom());
        chemin.setText(repo.getChemin());
        description.setText(repo.getDescription());
        if (repo.getURLImage() != null) {
            image.setImage(repo.getImage());
        }
    }

    /**
     * Permet de récupérer la cellule.
     *
     * @return la cellule
     */
    public Pane getCell() {
        return pane;
    }
}
