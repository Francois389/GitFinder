/*
 * MainController.java                                  20 mars 2024
 * IUT de Rodez, pas de droit d'auteur
 */

package org.fsp.gitfinder.controleur;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import org.fsp.gitfinder.GitBashLauncher;
import org.fsp.gitfinder.GitFinderApplication;
import org.fsp.gitfinder.factorie.RepositoryListViewCellFactorie;
import org.fsp.gitfinder.model.ModelPrincipal;
import org.fsp.gitfinder.model.Repository;

import java.util.logging.Logger;

/**
 * @author François de Saint Palais
 */
public class MainControleur {


    @FXML
    public ListView<Repository> listeRepos;
    @FXML
    public ToggleButton modifierButton;
    @FXML
    public AnchorPane pane;

    private final ObservableList<Repository> repositorieObservableList = FXCollections.observableArrayList();

    private static final ModelPrincipal model = ModelPrincipal.getInstance();

    public static final Logger LOGGER = Logger.getLogger(MainControleur.class.getName());

    @FXML
    void initialize() {

        LOGGER.info(model.getRepositories().toString());

        repositorieObservableList.setAll(model.getRepositories());

        listeRepos.setItems(repositorieObservableList);
        listeRepos.setCellFactory(repositoryListView -> new RepositoryListViewCellFactorie());

        listeRepos.setOnMouseClicked(event -> handleRepoClick(listeRepos.getSelectionModel().getSelectedItem()));

        listeRepos.setOnKeyPressed(event -> {
            if (event.getCode().getName().equals("Enter")
                    || event.getCode().getName().equals("Space")) {
                handleRepoClick(listeRepos.getSelectionModel().getSelectedItem());
            }
        });
    }

    private void handleRepoClick(Repository repositoryClique) {
        model.setRepositoryAModifier(null);

        if (repositoryClique != null) {
            LOGGER.info(repositoryClique.toString());

            if (!modifierButton.isSelected()) {
                model.setRepositorySelectionner(repositoryClique);
                GitBashLauncher.launch();
            } else {
                model.setRepositoryAModifier(repositoryClique);
                GitFinderApplication.loadEtChangerScene(GitFinderApplication.ViewPath.FORMULAIRE_REPOSITORY);
            }
        }
    }

    public void onModifierClick() {
        LOGGER.info(String.valueOf(modifierButton.isSelected()));
        //Désélectionne l'item sélectionné dans la liste
        //Si un element est déjà sélectionné, alors l'application empêche
        // de cliquer sur l'élément
        listeRepos.getSelectionModel().clearSelection();
    }

    public void onAjouterClick() {
        GitFinderApplication.loadEtChangerScene(GitFinderApplication.ViewPath.FORMULAIRE_REPOSITORY);
    }
}