package org.fsp.gitfinder.controleur;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.fsp.gitfinder.GitFinderApplication;
import org.fsp.gitfinder.Notification;
import org.fsp.gitfinder.controleur.mediator.FormulaireMediator;
import org.fsp.gitfinder.controleur.mediator.FormulaireRepositoryMediator;
import org.fsp.gitfinder.model.ModelPrincipal;
import org.fsp.gitfinder.model.Repository;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Contrôleur pour la vue d'ajout et de modification de repository.
 * Utilise le pattern Mediator pour gérer les interactions entre composants.
 *
 * @author François de Saint Palais
 */
public class FormulaireRepositoryControleur {

    public static final FileChooser.ExtensionFilter EXTENSION_IMAGE_AUTORISE =
            new FileChooser.ExtensionFilter("Images du repository",
                    "*.png", "*.jpg", "*.jpeg", "*.gif");

    public static final Notification CONFIRMATION_AJOUT =
            new Notification("Le repository a été ajouté avec succès");

    private static final ArrayList<String> motsCleDescription = new ArrayList<>(
        List.of("description", "déscription", "Description", "Déscription",
                "DESCRIPTION", "desc", "Desc", "DESC", "Présentation",
                "présentation", "Presentation", "presentation")
    );

    private static final DateTimeFormatter dateFormatage =
            DateTimeFormatter.ofPattern("dd-MM-yyyy_HHhmm-ss");

    @FXML public TextField nomInput;
    @FXML public TextField cheminInput;
    @FXML public TextArea descriptionInput;
    @FXML public ImageView imageRepo;
    @FXML public ImageView statusChemin;
    @FXML public Label erreurCheminInvalide;
    @FXML public Button btnAjouter;
    @FXML public Button btnEnregistrer;
    @FXML public Label titre;
    @FXML public Button btnAjouterEtQuitter;

    private static final ModelPrincipal model = ModelPrincipal.getInstance();

    // Le médiateur centralise toutes les interactions
    private FormulaireRepositoryMediator mediator;

    public static final Logger LOGGER =
            Logger.getLogger(FormulaireRepositoryControleur.class.getName());

    @FXML
    void initialize() {
        // Créer et initialiser le médiateur
        mediator = new FormulaireRepositoryMediator();

        // Enregistrer tous les composants auprès du médiateur
        mediator.enregistrerComposants(
            nomInput, cheminInput, descriptionInput,
            imageRepo, statusChemin, erreurCheminInvalide,
            btnAjouter, btnEnregistrer, btnAjouterEtQuitter
        );

        // Configurer le mode (ajout ou modification)
        if (model.getRepositoryAModifier() != null) {
            remplirChampsReposAModifier();
            btnEnregistrer.setVisible(true);
            btnAjouter.setVisible(false);
            btnAjouterEtQuitter.setVisible(false);
        } else {
            btnEnregistrer.setVisible(false);
            btnAjouter.setVisible(true);
            btnAjouterEtQuitter.setVisible(true);
        }
    }

    /**
     * Gère le clic sur le bouton "Parcourir..." pour le dossier
     */
    public void handleParcourirDossierClick() {
        File dossierSelectionne = mediator.ouvrirSelecteurDossier();

        if (dossierSelectionne != null) {
            updateChampsEnFonctionDuChemin(dossierSelectionne);
        }
    }

    /**
     * Gère le clic sur le bouton "Parcourir" pour l'image
     */
    public void handleParcourirImageClick() {
        File image = mediator.ouvrirSelecteurImage();

        if (image != null) {
            mediator.getImageRepo().setImage(new Image(image.toURI().toString()));
        }
    }

    /**
     * Gère le clic sur le bouton "Ajouter"
     */
    public void handleAjouterClick(ActionEvent actionEvent)
            throws URISyntaxException, NoSuchFileException {
        if (ajouterRepos()) {
            CONFIRMATION_AJOUT.show();
        }
    }

    /**
     * Gère le clic sur le bouton "Ajouter et quitter"
     */
    public void handleAjouterEtQuitterClick(ActionEvent actionEvent)
            throws URISyntaxException, NoSuchFileException {
        if (ajouterRepos()) {
            CONFIRMATION_AJOUT.show();
            retourMain();
        }
    }

    /**
     * Gère le clic sur le bouton "Quitter"
     */
    public void handelQuitter(ActionEvent actionEvent) {
        retourMain();
    }

    /**
     * Gère le clic sur le bouton "Enregistrer"
     */
    public void enregistrer(ActionEvent actionEvent)
            throws NoSuchFileException, URISyntaxException {
        if (assureChampsValides()) {
            Repository repository = model.getRepositoryAModifier();
            repository.setNom(nomInput.getText());
            repository.setChemin(cheminInput.getText());
            repository.setDescription(descriptionInput.getText());

            if (mediator.isImageModifiee()) {
                repository.setCheminImage(
                    copieImage(repository.getNom(),
                            mediator.getImageRepo().getImage().getUrl())
                );
            }
            retourMain();
        }
    }

    /**
     * Remplit les champs avec les informations du repository à modifier
     */
    private void remplirChampsReposAModifier() {
        Repository repository = model.getRepositoryAModifier();
        nomInput.setText(repository.getNom());
        cheminInput.setText(repository.getChemin());
        descriptionInput.setText(repository.getDescription());

        if (repository.getURLImage() != null) {
            Image imageDuRepo = new Image(
                new File(repository.getURLImage()).toURI().toString()
            );
            imageRepo.setImage(imageDuRepo);
        }
    }

    /**
     * Met à jour les champs en fonction du chemin du dossier sélectionné
     */
    private void updateChampsEnFonctionDuChemin(File dossierSelectionner) {
        cheminInput.setText(dossierSelectionner.getAbsolutePath());

        String nomDossier = dossierSelectionner.getName();
        if (nomInput.getText().isEmpty() ||
            !nomInput.getText().equals(nomDossier)) {
            nomInput.setText(nomDossier);
        }

        FilenameFilter isReadme = (dir, name) -> name.equals("README.md");

        Optional.ofNullable(dossierSelectionner.listFiles(isReadme))
                .flatMap(listFiles -> Arrays.stream(listFiles).findFirst())
                .map(File::getAbsolutePath)
                .ifPresent(cheminReadmeStr -> {
                    File readme = new File(cheminReadmeStr);
                    if (readme.exists()) {
                        String descriptionRecupere =
                                getDescriptionDepuisREADME(readme);

                        if (descriptionInput.getText().isEmpty() ||
                            !descriptionInput.getText().equals(descriptionRecupere)) {
                            descriptionInput.setText(descriptionRecupere);
                        }

                        LOGGER.info("Contenu du README : " + descriptionRecupere);
                    }
                });
    }

    /**
     * Renvoie la description issue d'un fichier README.md
     */
    private String getDescriptionDepuisREADME(File readme) {
        StringBuilder description = new StringBuilder();
        boolean onAAtteintLaDescription = false;

        try (Stream<String> lines = Files.lines(readme.toPath())) {
            for (String ligne : lines.toList()) {
                if (onAAtteintLaDescription) {
                    if (!ligne.isEmpty()) {
                        description.append(ligne).append("\n");
                        onAAtteintLaDescription = ligne.matches("^## ");
                    }
                } else {
                    onAAtteintLaDescription = estBaliseDescription(ligne);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return description.toString();
    }

    private static boolean estBaliseDescription(String ligne) {
        boolean estDescription = false;
        for (int i = 0; i < motsCleDescription.size() && !estDescription; i++) {
            estDescription = ligne.contains("## " + motsCleDescription.get(i));
        }
        return estDescription;
    }

    /**
     * Copie un fichier d'origine vers une destination
     */
    private Path copierFichier(String origineString, String destinationString)
            throws IOException, URISyntaxException {
        Path origine = Path.of(new URI(origineString.replace("\\", "/")));
        Path destination = Path.of(new URI(destinationString.replace("\\", "/")));

        if (!origine.isAbsolute()) {
            throw new IllegalArgumentException("Le chemin d'origine doit être absolu");
        }
        if (!destination.isAbsolute()) {
            throw new IllegalArgumentException(
                "Le chemin de destination doit être absolu"
            );
        }

        return Files.copy(origine, destination, StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * Ajoute un repository à la liste
     */
    private boolean ajouterRepos() throws URISyntaxException, NoSuchFileException {
        if (!assureChampsValides()) {
            return false;
        }

        String nom = nomInput.getText();
        String chemin = cheminInput.getText();
        String description = descriptionInput.getText();
        String image = copieImage(nom, null);

        try {
            model.ajouterRepository(Repository.builder()
                    .nom(nom)
                    .chemin(chemin)
                    .description(description)
                    .cheminImage(image)
                    .build()
            );

            mediator.reinitialiserChamps();
            return true;
        } catch (IllegalArgumentException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Repository déjà existant");
            alert.setContentText(
                "Un repository avec le même nom et le même chemin existe déjà"
            );
            alert.showAndWait();
            return false;
        }
    }

    /**
     * Copie l'image du repository si elle a été modifiée
     */
    private String copieImage(String nomRepository, String cheminImageInitial)
            throws URISyntaxException, NoSuchFileException {
        if (!mediator.isImageModifiee() || mediator.getImageRepo().getImage() == null) {
            return cheminImageInitial;
        }

        String origine = mediator.getImageRepo().getImage().getUrl();
        String nomImageUnique = nomRepository +
                LocalDateTime.now().format(dateFormatage) + ".png";

        File destination = new File(
            GitFinderApplication.IMAGES_FOLDER + "\\" + nomImageUnique
        );

        if (destination.exists()) {
            return cheminImageInitial;
        }

        Path destinationFinal = null;
        try {
            destinationFinal = copierFichier(
                origine, "file:/" + destination.getAbsolutePath()
            );
        } catch (NoSuchFileException e) {
            throw e;
        } catch (IOException e) {
            LOGGER.severe(
                Arrays.stream(e.getStackTrace())
                        .map(StackTraceElement::toString)
                        .collect(Collectors.joining("\n"))
            );
        }

        return (destinationFinal != null)
                ? destinationFinal.toUri().toString()
                : destination.getAbsolutePath();
    }

    /**
     * Vérifie que les champs obligatoires sont valides
     */
    private boolean assureChampsValides() {
        if (nomInput.getText().isEmpty()) {
            afficherErreur("Nom du repository vide",
                          "Le nom du repository ne peut pas être vide");
            return false;
        }

        if (cheminInput.getText().isEmpty()) {
            afficherErreur("Chemin du repository vide",
                          "Le chemin du repository ne peut pas être vide");
            return false;
        }

        try {
            Repository.cheminRepositoryValide(cheminInput.getText());
        } catch (Exception e) {
            afficherErreur("Chemin du repository invalide",
                          "Le chemin du repository est invalide");
            return false;
        }

        return true;
    }

    /**
     * Affiche une alerte d'erreur
     */
    private void afficherErreur(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Retourne à la vue principale après confirmation
     */
    private static void retourMain() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
            "Êtes-vous sûr de vouloir retourner à la vue principale ?");
        alert.setTitle("Confirmation");
        alert.setHeaderText("Retour à la vue principale");

        var oui = new ButtonType("Oui, quitter", ButtonBar.ButtonData.YES);
        var non = new ButtonType("Non rester sur la page", ButtonBar.ButtonData.NO);
        alert.getButtonTypes().setAll(oui, non);

        alert.showAndWait().ifPresent(type -> {
            if (type == oui) {
                GitFinderApplication.loadEtChangerScene(
                    GitFinderApplication.ViewPath.MAIN
                );
            }
        });
    }
}