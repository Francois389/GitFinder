/*
 * MainController.java                                  20 mars 2024
 * IUT de Rodez, pas de droit d'auteur
 */

package org.fsp.gitfinder.controleur;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;
import org.fsp.gitfinder.factorie.RepositoryListViewCellFactorie;
import org.fsp.gitfinder.model.ModelPrincipal;
import org.fsp.gitfinder.model.Repository;

/**
 * @author François de Saint Palais
 */
public class MainControleur {


    @FXML
    public ListView<Repository> listeRepos;

    @FXML
    public ToggleButton modifierButton;

    private final ObservableList<Repository> repositorieObservableList = FXCollections.observableArrayList();

    private static final ModelPrincipal model = ModelPrincipal.getInstance();

    @FXML
    void initialize() {
        //<--STUB
        Repository repo = new Repository("E:\\FilmeOK", "FilmOK", "Un projet de gestion de dépôts git");
        model.getRepositories().add(repo);
        //-->

        repositorieObservableList.setAll(model.getRepositories());

        listeRepos.setItems(repositorieObservableList);
        listeRepos.setCellFactory(new Callback<ListView<Repository>, ListCell<Repository>>() {
            @Override
            public ListCell<Repository> call(ListView<Repository> repositoryListView) {
                return new RepositoryListViewCellFactorie();
            }
        });

        // Ajout d'un ChangeListener pour détecter la sélection d'un élément
        listeRepos.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                System.out.println(newValue);

            }
        });
    }

    public void onModifierClick() {
        System.out.println(modifierButton.isSelected());
    }



}