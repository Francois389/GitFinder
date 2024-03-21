/*
 * AjouterRepoControleur.java                                  21 mars 2024
 * IUT de Rodez, pas de droit d'auteur
 */

package org.fsp.gitfinder.controleur;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.fsp.gitfinder.GitFinderApplication;
import org.fsp.gitfinder.model.ModelPrincipal;
import org.fsp.gitfinder.model.Repository;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.Objects;

/**
 * @author François de Saint Palais
 */
public class AjouterRepoControleur {
    public static final String[] EXTENSION_IMAGE_POSSIBLE = {"*.png", "*.jpg", "*.jpeg", "*.gif"};
    @FXML
    public TextField nomInput;
    @FXML
    public TextField cheminInput;
    @FXML
    public TextArea descriptionInput;
    @FXML
    public ImageView imageRepo;
    @FXML
    public ImageView statusChemin;
    @FXML
    public Label erreurCheminInvalide;

    /**
     * L'image pour indiquer que le chemin est valide
     */
    private static Image statusOK;

    /**
     * Une alerte pour afficher de l'aide sur les chemins de repository
     */
    private static final Alert alert = new Alert(Alert.AlertType.INFORMATION);

    /**
     * Lors de la selection d'un dossier, le dossier parent est enregistré ici.
     * Cela permet de ne pas avoir à parcourir l'arborescence à chaque fois.
     */
    private String cheminFavorisDossier;
    private String cheminFavorisImage;

    private static final ModelPrincipal model = ModelPrincipal.getInstance();

    /**
     * Indique si l'image du repository a été modifiée.
     */
    private boolean imageRepoEstModifiee = false;

    @FXML
    void initialize() {
        try {
            statusOK = new Image(Objects.requireNonNull(GitFinderApplication.class.getResourceAsStream("icon/check-solid.png")));
        } catch (NullPointerException e) {
            //Si une des images n'a pas pu être chargée, on n'affiche pas le status
            statusChemin.setVisible(false);
            System.err.println("Impossible de charger les images de status");
        }
        alert.setTitle("Aide sur les chemins");
        alert.setHeaderText("Les chemins de repository");
        alert.setContentText(
                """
                        Un chemin de repository est un chemin vers un dossier contenant un dossier .git.
                        Ce dossier .git est un dossier caché qui contient les informations de git.
                                                
                        Par exemple, si vous avez un repository dans le dossier D:\\monrepo, alors le dossier D:\\monrepo\\.git doit exister."""
        );

        erreurCheminInvalide.setVisible(false);

        initEventListener();
    }

    /**
     * Initialise les écouteurs d'événements
     */
    private void initEventListener() {
        cheminInput.textProperty().addListener((observable, oldValue, newValue) -> {
            updateStatusChemin();
        });

        erreurCheminInvalide.addEventFilter(MouseEvent.MOUSE_ENTERED, event -> {
            erreurCheminInvalide.setStyle("-fx-underline: true;");
        });

        erreurCheminInvalide.addEventFilter(MouseEvent.MOUSE_EXITED, event -> {
            erreurCheminInvalide.setStyle("-fx-underline: false;");
        });

        erreurCheminInvalide.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            alert.showAndWait();
        });

        imageRepo.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            afficherImageGrand();
        });
    }

    /**
     * Affiche l'image du repository en grand dans une nouvelle fenêtre
     */
    private void afficherImageGrand() {
        if (imageRepo.getImage() != null && imageRepoEstModifiee) {
            Dialog<Objects> affichage = new Dialog<>();
            affichage.setTitle("Image du repository");
            affichage.getDialogPane().getButtonTypes().addAll(ButtonType.OK);

            ImageView imageView = new ImageView(imageRepo.getImage());
            //On limite la taille de l'image à 1000px de large
            if (imageRepo.getImage().getWidth() > 1000) {
                imageView.setFitWidth(1000);
            }
            imageView.setPreserveRatio(true);
            affichage.getDialogPane().setContent(imageView);
            affichage.showAndWait();
        }
    }

    /**
     * Met à jour l'image de status en fonction de la validité du chemin
     * Un chemin est valide s'il n'est pas null ou vide
     * et s'il est valide.
     * Pour plus de détails sur la validité, voir {@link Repository#cheminRepositoryValide(String)}
     */
    private void updateStatusChemin() {
        String cheminSaisie = cheminInput.getText();
        if (cheminSaisie != null && !cheminSaisie.isEmpty()) {
            try {
                Repository.cheminRepositoryValide(cheminSaisie);
                statusChemin.setImage(statusOK);
                erreurCheminInvalide.setVisible(false);
            } catch (Exception e) {
                //Une exception peut être levée si le chemin est invalide
                statusChemin.setImage(null);
                erreurCheminInvalide.setVisible(true);
            }
        } else {
            statusChemin.setImage(null);
            erreurCheminInvalide.setVisible(false);
        }
    }

    /**
     * Gère le clic sur le bouton "Parcourir..."
     * Ouvre une fenêtre de sélection de dossier.
     * Si un dossier est sélectionné, le chemin est mis à jour.
     * De plus on met à jour le nom du repository si celui-ci est vide ou différent du nom du dossier sélectionné.
     */
    public void handleParcourirDossierClick() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choisir un dossier");
        if (cheminFavorisDossier != null) {
            directoryChooser.setInitialDirectory(new File(cheminFavorisDossier));
        }

        File dossierSelectionner = directoryChooser.showDialog(null);

        if (dossierSelectionner != null) {
            //On enregistre le dossier parent pour ne pas avoir à parcourir l'arborescence à chaque fois
            cheminFavorisDossier = dossierSelectionner.getParent();

            //On met à jour le chemin du repository
            cheminInput.setText(dossierSelectionner.getAbsolutePath());


            //On met à jour le nom du repository si celui-ci est vide ou différent du nom du dossier sélectionné
            String nomDossier = dossierSelectionner.getName();
            if (nomInput.getText().isEmpty() || !nomInput.getText().equals(nomDossier)) {
                nomInput.setText(nomDossier);
            }

            //On met à jour la description du repository si celle-ci est vide
            //TODO: on peut utiliser le README si il existe
        } else {
            System.out.println("Aucun dossier sélectionné");
        }
    }

    /**
     * Gère le clic sur le bouton "Parcourir" pour l'image du repository
     * Ouvre une fenêtre de sélection d'image.
     */
    public void handleParcourirImageClick() {
        FileChooser fileChooser = new FileChooser();
        if (cheminFavorisImage != null) {
            fileChooser.setInitialDirectory(new File(cheminFavorisImage));
        }

        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images du repository", EXTENSION_IMAGE_POSSIBLE));

        File image = fileChooser.showOpenDialog(null);

        if (image != null) {
            //On enregistre le dossier parent pour ne pas avoir à parcourir l'arborescence à chaque fois
            cheminFavorisImage = image.getParent();

            imageRepo.setImage(new Image(image.toURI().toString()));
            imageRepoEstModifiee = true;
        }
    }

    /**
     * Gère le clic sur le bouton "Ajouter"
     * Ajoute un repository à la liste des repositories
     *
     * @param actionEvent l'événement de clic
     */
    public void handleAjouterClick(ActionEvent actionEvent) throws URISyntaxException, NoSuchFileException {
        boolean estAjouter = ajouterRepos();
        //TODO afficher une confirmation si le repository est ajouté
    }

    /**
     * Gère le clic sur le bouton "Ajouter et quitter"
     *
     * @param actionEvent l'événement de clic
     */
    public void handleAjouterEtQuitterClick(ActionEvent actionEvent) throws URISyntaxException, NoSuchFileException {
        boolean estAjouter = ajouterRepos();
        if (estAjouter) {
            retourMain();
        }
    }

    /**
     * Copie un fichier "origine" vers le fichier "destination" en remplaçant si le fichier existe déjà
     *
     * @param origineString     le chemin absolu du fichier d'origine
     * @param destinationString le chemin absolu du fichier de destination
     * @throws IOException        si une erreur d'entrée/sortie survient
     * @throws URISyntaxException si une erreur survient lors de la conversion des chemins en URI
     */
    private Path copierFichier(String origineString, String destinationString) throws IOException, URISyntaxException {
        Path origine = Path.of(new URI(origineString.replace("\\", "/")));
        Path destination = Path.of(new URI(destinationString.replace("\\", "/")));

        if (!origine.isAbsolute()) {
            throw new IllegalArgumentException("Le chemin d'origine doit être absolu");
        }
        if (!destination.isAbsolute()) {
            throw new IllegalArgumentException("Le chemin de destination doit être absolu");
        }
        return Files.copy(origine, destination, StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * Ajoute un repository à la liste des repositories
     * Si les champs obligatoires ne sont pas valides, on affiche une alerte.
     * Si le repository existe déjà, on ne l'ajoute pas et on affiche une alerte.
     */
    private boolean ajouterRepos() throws URISyntaxException, NoSuchFileException {
        boolean estAjouter = false;

        if (assureChampsValides()) {
            String nom = nomInput.getText();
            String chemin = cheminInput.getText();
            String description = descriptionInput.getText();
            String image = null;

            //Copier l'image dans le dossier des images
            if (imageRepo.getImage() != null && imageRepoEstModifiee) {
                String origine = imageRepo.getImage().getUrl();
                File destination = new File(STR."\{GitFinderApplication.IMAGES_FOLDER}\\repository-\{nom}.png");

                /*
                 * Si l'image n'existe pas déjà, on la copie
                 * Si l'image existe déjà cela veut dire qu'il existe un homonyme et que le repository en cours de création
                 * ne sera pas ajouté à la liste des repositories (cf Repository.equals et ModelPrincipal.repositories)
                 */
                if (!destination.exists()) {
                    try {
                        Path destinationFinal = copierFichier(origine, STR."file:/\{destination.getAbsolutePath()}");
                        image = destinationFinal.toUri().toString();
                    } catch (NoSuchFileException e) {
                        throw e;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            try {
                Repository repository = new Repository(chemin, nom, description, image);
                model.ajouterRepository(repository);

                estAjouter = true;

                reinitialiseLesChamps();
            } catch (IllegalArgumentException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText("Repository déjà existant");
                alert.setContentText("Un repository avec le même nom et le même chemin existe déjà");
                alert.showAndWait();
            }
        }

        return estAjouter;
    }

    /**
     * Réinitialise les champs de saisie
     */
    private void reinitialiseLesChamps() {
        //On réinitialise les champs
        nomInput.clear();
        cheminInput.clear();
        descriptionInput.clear();
        imageRepo.setImage(null);
        imageRepoEstModifiee = false;
    }

    /**
     * Vérifie que les champs obligatoire sont valides et saisies.
     * Si ce n'est pas le cas, on affiche une alerte.
     */
    private boolean assureChampsValides() {
        boolean valide = true;
        if (nomInput.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Nom du repository vide");
            alert.setContentText("Le nom du repository ne peut pas être vide");
            alert.showAndWait();

            valide = false;
        } else if (cheminInput.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Chemin du repository vide");
            alert.setContentText("Le chemin du repository ne peut pas être vide");
            alert.showAndWait();

            valide = false;
        } else {
            try {
                Repository.cheminRepositoryValide(cheminInput.getText());
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText("Chemin du repository invalide");
                alert.setContentText("Le chemin du repository est invalide");
                alert.showAndWait();

                valide = false;
            }
        }

        return valide;
    }

    /**
     * Retourne à la vue principale
     */
    private static void retourMain() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Êtes-vous sûr de vouloir retourner à la vue principale ?");
        alert.setTitle("Confirmation");
        alert.setHeaderText("Retour à la vue principale");
        ButtonType oui = new ButtonType("Oui, quitter", ButtonBar.ButtonData.YES);
        ButtonType non = new ButtonType("Non rester sur la page", ButtonBar.ButtonData.NO);
        alert.getButtonTypes().setAll(oui, non);

        alert.showAndWait().ifPresent(type -> {
            if (type == oui) {
                GitFinderApplication.loadEtChangerScene("main");
            }
        });
    }

    public void handelQuitter(ActionEvent actionEvent) {
        retourMain();
    }
}
