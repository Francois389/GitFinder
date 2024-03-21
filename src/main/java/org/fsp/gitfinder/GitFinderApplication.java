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
import javafx.stage.Stage;
import org.fsp.gitfinder.sauvegarde.GestionSauvegarde;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author François de Saint Palais
 */
public class GitFinderApplication extends Application {


    public static final File IMAGES_FOLDER = new File("images");
    /**
     * Le nom des fichiers fxml, sans l'extension, associés à leur Scene.
     */
    private static HashMap<String, Scene> scenes;

    /**
     * Les noms des fichiers fxml, sans l'extension.
     */
    private static ArrayList<String> ressources;

    /**
     * La fenêtre principale de l'application.
     */
    private static Stage fenetrePrincipale;

    @Override
    public void start(Stage stage) throws Exception {
        fenetrePrincipale = stage;

        chargementApplication();
        chargerDonnee();

        fenetrePrincipale.setTitle("GitFinder");
//        fenetrePrincipale.setResizable(false);
        fenetrePrincipale.setOnCloseRequest(event -> {
            try {
                quit();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        fenetrePrincipale.show();
    }

    /**
     * Charge les données de l'application.
     */
    private void chargerDonnee() throws IOException {
        boolean resultat = GestionSauvegarde.charge();
        if (!resultat) {
            System.out.println("Impossible de charger les données");
            changerScene("configGitPath");
        } else {
            System.out.println("Données chargées");
            loadEtChangerScene("main");
        }
    }

    /**
     * Enregistre les ressources et
     * charge les ressources de l'application.
     * Les ressources ne doivent dépendre de valeur initialisée par d'autre ressource.
     */
    private void chargementApplication() {
        scenes = new HashMap<>();
        ressources = new ArrayList<String>();

        ressources.add("configGitPath");
        ressources.add("main");
        ressources.add("ajouterRepo");

        for (String ressource : ressources) {
            loadScene(ressource);
        }

    }

    /**
     * Charge une scène et bascule la vue vers cette scène.
     *
     * @param nomFichier le nom du fichier fxml, sans l'extension
     */
    public static void loadEtChangerScene(String nomFichier) {
        loadScene(nomFichier);
        changerScene(nomFichier);
    }

    /**
     * Bascule la vue vers une scène déjà chargée.
     *
     * @param nomFichier le nom du fichier fxml, sans l'extension
     */
    public static void changerScene(String nomFichier) {
        if (!scenes.containsKey(nomFichier)) {
            throw new IllegalArgumentException(STR."La scène \{nomFichier} n'a pas été chargée");
        }
        fenetrePrincipale.setScene(scenes.get(nomFichier));
    }

    /**
     * Charge un fichier fxml en Scene et l'ajoute à la liste des scènes.
     *
     * @param nomFichier le nom du fichier fxml, sans l'extension
     */
    private static void loadScene(String nomFichier) {
        String nomFichierExtension = STR."\{nomFichier}.fxml";

        // Si le chemin du fichier contient l'extension, on lève une exception
        if (nomFichier.substring(nomFichier.length() - 4).equals(".fxml")) {
            throw new IllegalArgumentException("Le nom de fichier ne doit pas contenir l'extension");
        }

        FXMLLoader loader = new FXMLLoader(GitFinderApplication.class.getResource(nomFichierExtension));

        try {
            Scene scene = new Scene(loader.load());
            scenes.put(nomFichier, scene);
            System.out.println(STR."Chargement de la scène \{loader.getLocation()}");

        } catch (IllegalStateException e) {
            System.out.println(STR."Nom de fichier ou chemin incorrect : \{nomFichierExtension}");
        } catch (LoadException e) {
            System.out.println(STR."Erreur dans le fichier fxml ou la méthode \"initialize\" du controleur : \{loader.getLocation()}");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println(STR."Impossible de charger la scène \{loader.getLocation()}");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println(STR."Erreur inconnue lors du chargement de la scène \{loader.getLocation()}");
            e.printStackTrace();
        }
    }

    /**
     * Fonction appelé par les controllers pour quitter l'application
     *
     * @throws IOException   si une erreur survient lors de la sauvegarde
     * @throws InternalError si une erreur survient lors de la sauvegarde
     */
    public static void quit() throws InternalError, IOException {
        System.out.println("Quitting application");
        GestionSauvegarde.sauvegarde();
        Platform.exit();
    }

    public static void main(String[] args) {
        if (!IMAGES_FOLDER.exists()) {
            IMAGES_FOLDER.mkdir();
        }
        launch(args);
    }
}
