/*
 * GitFinderApplication.java                                  20 mars 2024
 * IUT de Rodez, pas de droit d'auteur
 */

package org.fsp.gitfinder;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.LoadException;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.fsp.gitfinder.sauvegarde.GestionSauvegarde;
import org.fsp.gitfinder.service.RepositoryService;

import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Initialise les ressources de l'application charge la sauvegarde et affiche la fenêtre principale.
 * Lorsque l'application est fermée, effectuée la sauvegarde.
 *
 * @author François de Saint Palais
 */
public class GitFinderApplication extends Application {

    private static final Image logoApplication = new Image(Objects.requireNonNull(GitFinderApplication.class.getResource("logoApplication.png")).toString());

    public static final File IMAGES_FOLDER = new File("images");
    /**
     * Le nom des fichiers fxml, sans l'extension, associés à leur Scene.
     */
    private static EnumMap<ViewPath, Scene> scenes;

    /**
     * La fenêtre principale de l'application.
     */
    private static Stage fenetrePrincipale;

    public static final Logger LOGGER = Logger.getLogger(GitFinderApplication.class.getName());

    @Override
    public void start(Stage stage) throws Exception {
        fenetrePrincipale = stage;

        chargementApplication();
        chargerDonnee();

        fenetrePrincipale.setTitle("GitFinder");
        fenetrePrincipale.setResizable(false);
        fenetrePrincipale.getIcons().add(logoApplication);
        fenetrePrincipale.setOnCloseRequest(event -> quit());

        fenetrePrincipale.show();
    }

    /**
     * Charge les données de l'application.
     */
    private void chargerDonnee() throws IOException {
        boolean resultat = GestionSauvegarde.charge();
        // Si les données n'ont pas pu être chargées, on affiche la scène de configuration du chemin de GitBash
        if (!resultat) {
            LOGGER.warning("Impossible de charger les données");
            changerScene(ViewPath.CONFIG_GIT_PATH);
        } else {
            LOGGER.info("Données chargées");
            loadEtChangerScene(ViewPath.MAIN);
        }
        RepositoryService.sauvegarder();
    }

    /**
     * Enregistre les ressources et
     * charge les ressources de l'application.
     * Les ressources ne doivent dépendre de valeur initialisée par d'autre ressource.
     */
    private void chargementApplication() {
        scenes = new EnumMap<>(ViewPath.class);

        for (ViewPath ressource : ViewPath.values()) {
            loadScene(ressource);
        }
    }

    /**
     * Charge une scène et bascule la vue vers cette scène.
     *
     * @param nomFichier le nom du fichier fxml, sans l'extension
     */
    public static void loadEtChangerScene(ViewPath nomFichier) {
        loadScene(nomFichier);
        changerScene(nomFichier);
    }

    /**
     * Bascule la vue vers une scène déjà chargée.
     *
     * @param nomFichier le nom du fichier fxml, sans l'extension
     */
    public static void changerScene(ViewPath nomFichier) {
        if (!scenes.containsKey(nomFichier)) {
            throw new IllegalArgumentException("La scène " + nomFichier + " n'a pas été chargée");
        }
        fenetrePrincipale.setScene(scenes.get(nomFichier));
    }

    /**
     * Charge un fichier fxml en Scene et l'ajoute à la liste des scènes.
     *
     * @param nomFichier le nom du fichier fxml, sans l'extension
     */
    private static void loadScene(ViewPath nomFichier) {
        String nomFichierExtension = nomFichier.getPath() + ".fxml";

        // Si le chemin du fichier contient l'extension, on lève une exception
        if (nomFichier.getPath().substring(nomFichier.getPath().length() - 4).equals(".fxml")) {
            throw new IllegalArgumentException("Le nom de fichier ne doit pas contenir l'extension");
        }

        FXMLLoader loader = new FXMLLoader(GitFinderApplication.class.getResource(nomFichierExtension));

        try {
            Scene scene = new Scene(loader.load());
            scenes.put(nomFichier, scene);
            LOGGER.info("Chargement de la scène " + loader.getLocation());

        } catch (IllegalStateException e) {
            LOGGER.warning("Nom de fichier ou chemin incorrect : " + nomFichierExtension);
        } catch (LoadException e) {
            LOGGER.warning("Erreur dans le fichier fxml ou la méthode \"initialize\" du controleur : " + loader.getLocation());
            e.printStackTrace();
        } catch (IOException e) {
            LOGGER.warning("Impossible de charger la scène " + loader.getLocation());
            e.printStackTrace();
        } catch (Exception e) {
            LOGGER.warning("Erreur inconnue lors du chargement de la scène " + loader.getLocation());
            e.printStackTrace();
        }
    }

    /**
     * Fonction appelé par les controllers pour quitter l'application
     *
     * @throws InternalError si une erreur survient lors de la sauvegarde
     */
    public static void quit() throws InternalError {
        LOGGER.info("Quitting application");
        RepositoryService.sauvegarder();
        GestionSauvegarde.sauvegarde();
        Platform.exit();
    }

    public static void main(String[] args) {
        if (!IMAGES_FOLDER.exists()) {
            IMAGES_FOLDER.mkdir();
            //TODO handle if `mkdir` return false
        }
        launch(args);
    }

    public static Stage getFenetrePrincipale() {
        return fenetrePrincipale;
    }

    public enum ViewPath {
        CONFIG_GIT_PATH("configGitPath"),
        MAIN("main"),
        FORMULAIRE_REPOSITORY("formulaireRepository");

        private final String path;

        ViewPath(String fileViewPath) {
            this.path = fileViewPath;
        }

        public String getPath() {
            return this.path;
        }
    }
}