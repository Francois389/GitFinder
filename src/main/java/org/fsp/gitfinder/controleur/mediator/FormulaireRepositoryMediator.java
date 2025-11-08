package org.fsp.gitfinder.controleur.mediator;

import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.fsp.gitfinder.GitFinderApplication;
import org.fsp.gitfinder.controleur.FormulaireRepositoryControleur;
import org.fsp.gitfinder.model.Repository;

import java.io.File;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Implémentation concrète du Mediator pour le formulaire de repository.
 * Gère toutes les interactions entre les composants de l'interface.
 *
 * @author François de Saint Palais
 */
public class FormulaireRepositoryMediator implements FormulaireMediator {

    // Composants de l'interface
    private TextField nomInput;
    private TextField cheminInput;
    private TextArea descriptionInput;
    private ImageView imageRepo;
    private ImageView statusChemin;
    private Label erreurCheminInvalide;
    private Button btnAjouter;
    private Button btnEnregistrer;
    private Button btnAjouterEtQuitter;

    // Images et ressources
    private Image statusOK;
    private Image imagePlaceholder;
    private Alert aideCheminRepository;

    // État du médiateur
    private String cheminFavorisDossier;
    private String cheminFavorisImage;
    private boolean imageRepoEstModifiee = false;

    private static final Logger LOGGER = Logger.getLogger(FormulaireRepositoryMediator.class.getName());

    public FormulaireRepositoryMediator() {
        initialiserRessources();
    }

    /**
     * Initialise les ressources (images, alertes)
     */
    private void initialiserRessources() {
        try {
            statusOK = new Image(Objects.requireNonNull(
                    GitFinderApplication.class.getResourceAsStream("icon/check-solid.png")
            ));
        } catch (NullPointerException e) {
            LOGGER.warning("Impossible de charger l'image de status");
        }

        aideCheminRepository = new Alert(Alert.AlertType.INFORMATION);
        aideCheminRepository.setTitle("Aide sur les chemins");
        aideCheminRepository.setHeaderText("Les chemins de repository");
        aideCheminRepository.setContentText(
                """
                        Un chemin de repository est un chemin vers un dossier contenant un dossier .git.
                        Ce dossier .git est un dossier caché qui contient les informations de git.
                        
                        Par exemple, si vous avez un repository dans le dossier D:\\monrepo, 
                        alors le dossier D:\\monrepo\\.git doit exister."""
        );
    }

    @Override
    public void enregistrerComposants(
            TextField nomInput,
            TextField cheminInput,
            TextArea descriptionInput,
            ImageView imageRepo,
            ImageView statusChemin,
            Label erreurCheminInvalide,
            Button btnAjouter,
            Button btnEnregistrer,
            Button btnAjouterEtQuitter
    ) {
        this.nomInput = nomInput;
        this.cheminInput = cheminInput;
        this.descriptionInput = descriptionInput;
        this.imageRepo = imageRepo;
        this.statusChemin = statusChemin;
        this.erreurCheminInvalide = erreurCheminInvalide;
        this.btnAjouter = btnAjouter;
        this.btnEnregistrer = btnEnregistrer;
        this.btnAjouterEtQuitter = btnAjouterEtQuitter;

        // Sauvegarder l'image placeholder
        imagePlaceholder = imageRepo.getImage();

        // Initialiser l'état de l'interface
        erreurCheminInvalide.setVisible(false);
        if (statusOK == null) {
            statusChemin.setVisible(false);
        }

        // Configurer les écouteurs
        configurerEcouteurs();
    }

    /**
     * Configure tous les écouteurs d'événements via le médiateur
     */
    private void configurerEcouteurs() {
        // Écouteur sur le champ chemin
        cheminInput.textProperty().addListener((observable, oldValue, newValue) ->
                notifier(cheminInput, "CHEMIN_MODIFIE")
        );

        // Écouteurs sur le label d'erreur
        erreurCheminInvalide.addEventFilter(MouseEvent.MOUSE_ENTERED,
                event -> notifier(erreurCheminInvalide, "SOURIS_ENTREE")
        );

        erreurCheminInvalide.addEventFilter(MouseEvent.MOUSE_EXITED,
                event -> notifier(erreurCheminInvalide, "SOURIS_SORTIE")
        );

        erreurCheminInvalide.addEventFilter(MouseEvent.MOUSE_CLICKED,
                event -> notifier(erreurCheminInvalide, "CLIC")
        );

        // Écouteur sur l'image
        imageRepo.addEventFilter(MouseEvent.MOUSE_CLICKED,
                event -> notifier(imageRepo, "CLIC")
        );
    }

    @Override
    public void notifier(Object composant, String evenement) {
        // Le médiateur gère toutes les interactions entre composants
        switch (evenement) {
            case "CHEMIN_MODIFIER" -> {
                if (composant == cheminInput) validerEtMettreAJourStatusChemin();
            }
            case "SOURIS_ENTREE" -> {
                if (composant == erreurCheminInvalide) erreurCheminInvalide.setStyle("-fx-underline: true;");
            }
            case "SOURIS_SORTIE" -> {
                if (composant == erreurCheminInvalide) erreurCheminInvalide.setStyle("-fx-underline: false;");
            }
            case "CLIC" -> {
                if (composant == erreurCheminInvalide) {
                    aideCheminRepository.showAndWait();
                } else if (composant == imageRepo) {
                    afficherImageEnGrand();
                }
            }
        }
    }

    /**
     * Valide le chemin saisi et met à jour l'indicateur visuel
     */
    private void validerEtMettreAJourStatusChemin() {
        String cheminSaisie = cheminInput.getText();

        if (cheminSaisie != null && !cheminSaisie.isEmpty()) {
            try {
                Repository.cheminRepositoryValide(cheminSaisie);
                statusChemin.setImage(statusOK);
                erreurCheminInvalide.setVisible(false);
            } catch (Exception e) {
                statusChemin.setImage(null);
                erreurCheminInvalide.setVisible(true);
            }
        } else {
            statusChemin.setImage(null);
            erreurCheminInvalide.setVisible(false);
        }
    }

    /**
     * Affiche l'image du repository en taille réelle
     */
    private void afficherImageEnGrand() {
        if (imageRepo.getImage() != null && imageRepoEstModifiee) {
            Dialog<Objects> affichage = new Dialog<>();
            affichage.setTitle("Image du repository");
            affichage.getDialogPane().getButtonTypes().add(ButtonType.OK);

            ImageView imageView = new ImageView(imageRepo.getImage());
            if (imageRepo.getImage().getWidth() > 1000) {
                imageView.setFitWidth(1000);
            }
            imageView.setPreserveRatio(true);
            imageView.addEventHandler(MouseEvent.MOUSE_CLICKED,
                    event -> affichage.close()
            );

            affichage.getDialogPane().setContent(imageView);
            affichage.showAndWait();
        }
    }

    /**
     * Ouvre le sélecteur de dossier
     *
     * @return le dossier sélectionné ou null
     */
    public File ouvrirSelecteurDossier() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choisir un dossier");

        if (cheminFavorisDossier != null) {
            directoryChooser.setInitialDirectory(new File(cheminFavorisDossier));
        }

        File dossierSelectionne = directoryChooser.showDialog(null);

        if (dossierSelectionne != null) {
            cheminFavorisDossier = dossierSelectionne.getParent();
        }

        return dossierSelectionne;
    }

    /**
     * Ouvre le sélecteur d'image
     *
     * @return l'image sélectionnée ou null
     */
    public File ouvrirSelecteurImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().add(
                FormulaireRepositoryControleur.EXTENSION_IMAGE_AUTORISE
        );

        if (cheminFavorisImage != null) {
            fileChooser.setInitialDirectory(new File(cheminFavorisImage));
        }

        File image = fileChooser.showOpenDialog(null);

        if (image != null) {
            cheminFavorisImage = image.getParent();
            imageRepoEstModifiee = true;
        }

        return image;
    }

    /**
     * Réinitialise tous les champs du formulaire
     */
    public void reinitialiserChamps() {
        nomInput.clear();
        cheminInput.clear();
        descriptionInput.clear();
        imageRepo.setImage(imagePlaceholder);
        imageRepoEstModifiee = false;
    }

    // Getters pour l'état

    public boolean isImageModifiee() {
        return imageRepoEstModifiee;
    }

    public void setImageModifiee(boolean modifiee) {
        this.imageRepoEstModifiee = modifiee;
    }

    public ImageView getImageRepo() {
        return imageRepo;
    }

    public TextField getNomInput() {
        return nomInput;
    }

    public TextField getCheminInput() {
        return cheminInput;
    }

    public TextArea getDescriptionInput() {
        return descriptionInput;
    }
}