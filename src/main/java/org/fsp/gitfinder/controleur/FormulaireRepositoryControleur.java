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
import org.fsp.gitfinder.Notification;
import org.fsp.gitfinder.model.ModelPrincipal;
import org.fsp.gitfinder.model.Repository;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * Contrôleur pour la vue d'ajout et de modification de repository.
 *
 * @author François de Saint Palais
 */
public class FormulaireRepositoryControleur {

    /**
     * Les extensions de fichiers autorisées pour les images de repository
     */
    public static final FileChooser.ExtensionFilter EXTENSION_IMAGE_AUTORISE =
            new FileChooser.ExtensionFilter("Images du repository",
                    "*.png", "*.jpg", "*.jpeg", "*.gif");

    public static final Notification CONFIRMATION_AJOUT = new Notification("Le repository a été ajouté avec succès");

    //Liste des potentiels mots clé pour la description dans un README
    private static final ArrayList<String> motsCleDescription = new ArrayList<>(List.of("description", "déscription", "Description", "Déscription",
            "DESCRIPTION", "desc", "Desc", "DESC", "Présentation",
            "présentation", "Presentation", "presentation"));

    private static final DateTimeFormatter dateFormatage =
            DateTimeFormatter.ofPattern("dd-MM-yyyy_HHhmm-ss");

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
    @FXML
    public Button btnAjouter;
    @FXML
    public Button btnEnregistrer;
    @FXML
    public Label titre;
    @FXML
    public Button btnAjouterEtQuitter;
    /**
     * L'image pour indiquer que le chemin est valide
     */
    private static Image statusOK;

    /**
     * Une image de placeholder pour l'image du repository
     */
    private static Image imagePlaceholder;

    /**
     * Une alerte pour afficher de l'aide sur les chemins de repository
     */
    private static final Alert aideCheminRepository = new Alert(Alert.AlertType.INFORMATION);

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

    public static final Logger LOGGER = Logger.getLogger(FormulaireRepositoryControleur.class.getName());

    @FXML
    void initialize() {
        // On charge l'image de status
        try {
            statusOK = new Image(Objects.requireNonNull(GitFinderApplication.class.getResourceAsStream("icon/check-solid.png")));
        } catch (NullPointerException e) {
            //Si l'image n'a pas pu être chargée, on n'affiche pas le status
            statusChemin.setVisible(false);
            LOGGER.warning("Impossible de charger les images de status");
        }

        // On charge l'image de placeholder
        imagePlaceholder = imageRepo.getImage();

        // On initialise l'alerte d'aide sur les chemins
        aideCheminRepository.setTitle("Aide sur les chemins");
        aideCheminRepository.setHeaderText("Les chemins de repository");
        aideCheminRepository.setContentText(
                """
                        Un chemin de repository est un chemin vers un dossier contenant un dossier .git.
                        Ce dossier .git est un dossier caché qui contient les informations de git.
                        
                        Par exemple, si vous avez un repository dans le dossier D:\\monrepo, alors le dossier D:\\monrepo\\.git doit exister.""");

        // On cache l'erreur de chemin invalide
        erreurCheminInvalide.setVisible(false);

        initEventListener();

        if (model.getRepositoryAModifier() != null) {
            // Si on est en mode modification, on remplit les champs
            // avec les informations du repository à modifier
            remplirChampsReposAModifier();

            // Et on affiche le bouton "Enregistrer" à la place des boutons "Ajouter"
            btnEnregistrer.setVisible(true);
            btnAjouter.setVisible(false);
            btnAjouterEtQuitter.setVisible(false);
        } else {
            // Sinon, on affiche les boutons "Ajouter" et "Ajouter et quitter"
            btnEnregistrer.setVisible(false);
            btnAjouter.setVisible(true);
            btnAjouterEtQuitter.setVisible(true);
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
            updateChampsEnFonctionDuChemin(dossierSelectionner);
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
        fileChooser.getExtensionFilters().add(EXTENSION_IMAGE_AUTORISE);

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
        if (estAjouter) {
            CONFIRMATION_AJOUT.show();
        }
    }

    /**
     * Gère le clic sur le bouton "Ajouter et quitter"
     * Ajoute un repository à la liste des repositories
     * et retourne à la vue principale si le repository est ajouté
     *
     * @param actionEvent l'événement de clic
     */
    public void handleAjouterEtQuitterClick(ActionEvent actionEvent) throws URISyntaxException, NoSuchFileException {
        boolean estAjouter = ajouterRepos();
        if (estAjouter) {
            CONFIRMATION_AJOUT.show();
            retourMain();
        }
    }

    /**
     * Gère le clic sur le bouton "Quitter"
     * Retourne à la vue principale
     */
    public void handelQuitter(ActionEvent actionEvent) {
        retourMain();
    }

    /**
     * Gère le clic sur le bouton "Enregistrer"
     * Enregistre les modifications apportées à un repository
     */
    public void enregistrer(ActionEvent actionEvent) throws NoSuchFileException, URISyntaxException {
        if (assureChampsValides()) {
            Repository repository = model.getRepositoryAModifier();
            repository.setNom(nomInput.getText());
            repository.setChemin(cheminInput.getText());
            repository.setDescription(descriptionInput.getText());
            if (imageRepoEstModifiee) {
                repository.setCheminImage(
                        copieImage(repository.getNom(),
                                imageRepo.getImage().getUrl())
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
            //On récupère l'image sans la charger
            Image imageDuRepo = new Image(new File(repository.getURLImage()).toURI().toString());
            imageRepo.setImage(imageDuRepo);
        }
    }

    /**
     * Initialise les écouteurs d'événements
     */
    private void initEventListener() {
        cheminInput.textProperty().addListener((observable, oldValue, newValue) -> updateStatusChemin());

        erreurCheminInvalide.addEventFilter(MouseEvent.MOUSE_ENTERED, event -> erreurCheminInvalide.setStyle("-fx-underline: true;"));

        erreurCheminInvalide.addEventFilter(MouseEvent.MOUSE_EXITED, event -> erreurCheminInvalide.setStyle("-fx-underline: false;"));

        erreurCheminInvalide.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> aideCheminRepository.showAndWait());

        imageRepo.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> afficherImageGrand());
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
            imageView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> affichage.close());
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
     * Met à jour les champs en fonction du chemin du dossier sélectionné<br>
     * On récupère le maximum d'informations
     * possibles à partir du dossier sélectionné.<br>
     * On peut récupérer :
     * <ul>
     *     <li>Le chemin du repository</li>
     *     <li>Le nom du repository</li>
     *     <li>La description du repository</li>
     * </ul>
     *
     * @param dossierSelectionner le dossier sélectionné
     */
    private void updateChampsEnFonctionDuChemin(File dossierSelectionner) {
        //On met à jour le chemin du repository
        cheminInput.setText(dossierSelectionner.getAbsolutePath());

        //On met à jour le nom du repository si celui-ci est vide ou différent du nom du dossier sélectionné
        String nomDossier = dossierSelectionner.getName();
        if (nomInput.getText().isEmpty() || !nomInput.getText().equals(nomDossier)) {
            nomInput.setText(nomDossier);
        }

        //On met à jour la description du repository si celle-ci est vide
        String cheminReadme = dossierSelectionner.getAbsolutePath() + "\\README.md";
        File readme = new File(cheminReadme);
        if (readme.exists()) {
            String descriptionRecupere = getDescriptionDepuisREADME(readme);

            if (descriptionInput.getText().isEmpty() || !descriptionInput.getText().equals(descriptionRecupere)) {
                descriptionInput.setText(descriptionRecupere);
            }

            LOGGER.info("Contenu du README :");
            LOGGER.info(descriptionRecupere);
        }
    }

    /**
     * Renvoie la description issue d'un fichier README.md<\br>
     * On recherche pour le debut d'une section de déscription.<br>
     * Une section de description commence par un titre 2 MarkDown
     * suivie de 1 des mots clé présent dans
     * {@link FormulaireRepositoryControleur#motsCleDescription}
     *
     * @param readme Le fichier README.md à analyser
     * @return la description récupèré ou sinon une string vide
     */
    private String getDescriptionDepuisREADME(File readme) {
        StringBuilder description = new StringBuilder();

        boolean onAAtteintLaDescription = false;

        try (Stream<String> lines = Files.lines(readme.toPath())) {
            for (String ligne : lines.toList()) {
                if (onAAtteintLaDescription) {
                    if (!ligne.isEmpty()) {
                        description.append(ligne).append("\n");
                        // On s'arrête quand on a atteint une autre section
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

    /**
     * Retourne true si la ligne est identifié comme étant un entête pour une
     * description, false sinon
     *
     * @param ligne La ligne analyser
     * @return true si la ligne est identifié comme étant un entête
     * pour une description, false sinon
     */
    private static boolean estBaliseDescription(String ligne) {
        boolean estDescription = false;
        for (int i = 0; i < motsCleDescription.size() && !estDescription; i++) {
            estDescription = ligne.contains("## " + motsCleDescription.get(i));
        }
        return estDescription;
    }


    /**
     * Copie un fichier "origine" vers le fichier "destination" en remplaçant si le fichier existe déjà
     *
     * @param origineString     le chemin absolu du fichier d'origine
     * @param destinationString le chemin absolu du fichier de destination
     * @return le chemin absolu du fichier de destination
     * @throws IOException        si une erreur d'entrée/sortie survient
     * @throws URISyntaxException si une erreur survient lors de la conversion des chemins en URI
     */
    private Path copierFichier(String origineString, String destinationString) throws IOException, URISyntaxException {
        Path origine = Path.of(new URI(origineString.replace("\\", "/")));
        Path destination = Path.of(new URI(destinationString.replace("\\", "/")));

        //Les chemins doivent être absolus
        //Si ce n'est pas le cas, on lève une exception
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
            String image = copieImage(nom, null);

            // On ajoute le repository à la liste des repositories enregistrés
            try {
                LOGGER.info(chemin);
                LOGGER.info(nom);
                LOGGER.info(description);
                LOGGER.info(image);
                Repository repository = new Repository(chemin, nom, description, image);
                model.ajouterRepository(repository);
                estAjouter = true;

                reinitialiseLesChamps();
            } catch (IllegalArgumentException e) {
                //Si le repository existe déjà, on affiche une alerte
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
     * Si l'image à été modifier, et que l'ImageView n'est pas null,
     * on copie l'image saisie dans le dossier des images.
     *
     * @param nomRepository      Le nom du repository qui utilise l'image
     * @param cheminImageInitial Le chemin initial de l'image
     * @return le chemin de l'image si elle a été copiée ou le chemin initial sinon
     * @throws URISyntaxException  si une erreur survient lors de la conversion des chemins en URI
     * @throws NoSuchFileException si l'image saisie n'existe pas
     */
    private String copieImage(String nomRepository, String cheminImageInitial) throws URISyntaxException, NoSuchFileException {
        if (imageRepoEstModifiee && imageRepo.getImage() != null) {
            //Copier l'image dans le dossier des images

            String origine = imageRepo.getImage().getUrl();
            //Le chemin de l'image copié doit être unique
            String nomImageUnique = nomRepository + LocalDateTime.now().format(dateFormatage) + ".png";

            File destination =
                    new File(GitFinderApplication.IMAGES_FOLDER + "\\" + nomImageUnique);

            /*
             * Si l'image n'existe pas déjà, on la copie
             * Si l'image existe déjà cela veut dire qu'il existe un homonyme et que le repository en cours de création
             * ne sera pas ajouté à la liste des repositories (cf. Repository.equals et ModelPrincipal.repositories)
             */
            Path destinationFinal = null;
            if (!destination.exists()) {
                try {
                    destinationFinal = copierFichier(origine, "file:/" + destination.getAbsolutePath());
                } catch (NoSuchFileException e) {
                    throw e;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            cheminImageInitial = (destinationFinal != null)
                    ? destinationFinal.toUri().toString()
                    : destination.getAbsolutePath();
        }
        return cheminImageInitial;
    }

    /**
     * Réinitialise les champs de saisie
     */
    private void reinitialiseLesChamps() {
        //On réinitialise les champs
        nomInput.clear();
        cheminInput.clear();
        descriptionInput.clear();
        imageRepo.setImage(imagePlaceholder);
        imageRepoEstModifiee = false;
    }

    /**
     * Vérifie que les champs obligatoire sont valides et saisies.
     * Si ce n'est pas le cas, on affiche une alerte.
     *
     * @return true si les champs sont valides, false sinon
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
     * Retourne à la vue principale après confirmation de l'utilisateur
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
                GitFinderApplication.loadEtChangerScene(GitFinderApplication.ViewPath.MAIN);
            }
        });
    }
}