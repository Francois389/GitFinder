package org.fsp.gitfinder.controleur;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import org.fsp.gitfinder.GitFinderApplication;
import org.fsp.gitfinder.model.ModelPrincipal;

import java.io.File;
import java.nio.file.OpenOption;
import java.util.Optional;
import java.util.logging.Logger;

public class ConfigGitPathControleur {
    @FXML
    private TextField gitPath;

    private ModelPrincipal modelPrincipal;

    private static final Alert alert = new Alert(Alert.AlertType.ERROR, "Le chemin n'existe pas");

    public static final Logger LOGGER = Logger.getLogger(ConfigGitPathControleur.class.getName());

    @FXML
    void initialize() {
        modelPrincipal = ModelPrincipal.getInstance();

        tryGetGitPath().ifPresent(path -> {
            if (modelPrincipal.getGitBashPath() == null) modelPrincipal.setGitBashPath(path);
        });

        if (modelPrincipal.getGitBashPath() != null) {
            GitFinderApplication.changerScene(GitFinderApplication.ViewPath.MAIN);
        }
    }

    private static Optional<String> tryGetGitPath() {
        Optional<String> gitBashPath = Optional.empty();
        try {
            String envPath = System.getenv("Path");
            gitBashPath = Optional.of(getGitBashPathFromPATH(envPath));
        } catch (Exception e) {
            LOGGER.info("Erreur lors de la lecture du Paht : " + e.getMessage());
        }
        return gitBashPath;
    }

    @FXML
    protected void onConfirmerClick() {
        String path = gitPath.getText();

        path = cleanPath(path);

        File file = new File(path);

        if (file.exists()) {
            modelPrincipal.setGitBashPath(path);
            GitFinderApplication.changerScene(GitFinderApplication.ViewPath.MAIN);
        } else {
            alert.showAndWait();
        }
    }

    @FXML
    protected void onParcourirClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir le chemin de git-bash.exe");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichier exécutable", "*.exe"));
        File file = fileChooser.showOpenDialog(gitPath.getScene().getWindow());

        if (file != null) {
            gitPath.setText(file.getAbsolutePath());
        }
    }

    /**
     * Nettoie le chemin en supprimant les caractères inutiles.
     * Par exemple : "C:\Program Files\Git\git-bash.exe" -> C:\Program Files\Git\git-bash.exe
     *
     * @param path le chemin à nettoyer
     * @return le chemin nettoyé
     */
    private String cleanPath(String path) {
        path = path.trim();
        if (path.startsWith("\"") && path.endsWith("\"")) {
            path = path.substring(1, path.length() - 1);
        }
        return path;
    }

    /**
     * Renvoie le chemin de git-bash.exe s'il est présent dans le PATH.
     * Sinon, renvoie le chemin par défaut s'il est présent.
     *
     * @param envPath le PATH de l'environnement
     * @return le chemin de git-bash.exe
     */
    private static String getGitBashPathFromPATH(String envPath) {
        String[] paths = envPath.split(";");
        String gitBashPath = null;

        for (String path : paths) {
            File file = new File(path, "git-bash.exe");
            if (file.exists()) {
                gitBashPath = file.getAbsolutePath();
                break;
            }
        }
        return gitBashPath;
    }
}