package org.fsp.gitfinder;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.fsp.gitfinder.model.ModelPrincipal;

import java.io.File;
import java.io.IOException;

public class MainController {
    @FXML
    private TextField gitPath;

    private ModelPrincipal modelPrincipal;

    @FXML
    void initialize() {
        modelPrincipal = ModelPrincipal.getInstance();
        gitPath.setText(modelPrincipal.getGitBashPath());
    }

    @FXML
    protected void onConfirmerClick() {
        String envPath = System.getenv("Path");

        String gitBashPath = getGitBashPathFromPATH(envPath);

        if (gitBashPath != null) {
            ProcessBuilder processBuilder = new ProcessBuilder(gitBashPath);
            processBuilder.directory(new File("E:\\"));
            try {
                Process process = processBuilder.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("git-bash.exe not found in PATH");
        }
    }

    /**
     * Renvoie le chemin de git-bash.exe s'il est présent dans le PATH.
     * Sinon, renvoie le chemin par défaut s'il est présent.
     * @param envPath le PATH de l'environnement
     * @return le chemin de git-bash.exe
     */
    private static String getGitBashPathFromPATH(String envPath) {
        String[] paths = envPath.split(";");
        String gitBashPath = null;
        String gitBashPathSaved = "C:\\Program Files\\Git\\git-bash.exe";


        for (String path : paths) {
            File file = new File(path, "git-bash.exe");
            if (file.exists()) {
                gitBashPath = file.getAbsolutePath();
                break;
            }
        }

        if (gitBashPath == null) {
            File file = new File(gitBashPathSaved);
            if (file.exists()) {
                gitBashPath = file.getAbsolutePath();
            }
        }
        return gitBashPath;
    }
}