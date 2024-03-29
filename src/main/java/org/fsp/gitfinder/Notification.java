/*
 * CustomAlert.java                                  27 Mar 2024
 * IUT de Rodez, pas de droit d'auteur
 */

package org.fsp.gitfinder;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;


/**
 * Permet de créer une alerte qui ne nécessite pas de réponse
 * de la part de l'utilisateur.
 * Inspirée de <a
 * href="https://github.com/Chaipo-arch/SAE-3.01-Gestion-des-notes-d-un-semestre-de-BUT/blob/main/GestionNoteApplication/src/main/controller/NotificationController.java">
 * SAE-3.01-Gestion-des-notes-d-un-semestre-de-BUT
 * </a>
 *
 * @author François de Saint Palais
 */
public class Notification {

    private final String message;

    private final Stage notificationStage; // La fenêtre de notification
    private final Label notificationLabel; // Le label contenant le message de la notification
    private Timeline timelineFadeAway;

    public Notification(String message) {
        this.message = message;

        notificationStage = new Stage();
        notificationStage.initStyle(StageStyle.UNDECORATED);
        notificationStage.setAlwaysOnTop(true);


        VBox rootNotif = new VBox();
        rootNotif.setAlignment(Pos.CENTER);
        notificationLabel = new Label();
        rootNotif.getChildren().add(notificationLabel);


        rootNotif.setStyle(
                "-fx-background-color: #7EEC93;" + // Couleur de fond blanche
                        "-fx-background-radius: 10;" + // Coins arrondis de 20 pixels
                        "-fx-font-size: 15px;"
        );

        // Création de la scène pour la fenêtre de notification
        Scene scene = new Scene(rootNotif, 270, 55);
        scene.setFill(Color.TRANSPARENT); // Rend la scène transparente


        // Attribution de la scène à la fenêtre de notification
        notificationStage.setScene(scene);
        notificationStage.initStyle(StageStyle.TRANSPARENT);

        // On ferme la fenêtre de notification si l'utilisateur clique dessus
        notificationStage.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> notificationStage.close());
    }

    public void show() {
        show(5);
    }

    public void show(int dureeApparition) {
        notificationLabel.setText(message);

        // Positionnement de la fenêtre de notification
        Stage fenetrePrincipale = GitFinderApplication.getFenetrePrincipale();
        double centre = fenetrePrincipale.getX() + fenetrePrincipale.getWidth() / 2 - notificationStage.getScene().getWidth() / 2;
        notificationStage.setX(centre);
        notificationStage.setY(fenetrePrincipale.getY() + 34);
        notificationStage.setOpacity(1);

        // Affichage de la fenêtre de notification
        notificationStage.show();
        // On remet le focus sur la fenêtre principale
        fenetrePrincipale.requestFocus();

        // Définition de la durée d'affichage de la notification avant de la fermer
        timelineFadeAway = new Timeline(
                new KeyFrame(Duration.seconds(dureeApparition),
                        new KeyValue(notificationStage.opacityProperty(), 0)) // Réduction de l'opacité jusqu'à 0
        );

        // Action à effectuer lorsque la durée d'affichage est terminée
        timelineFadeAway.setOnFinished(event -> notificationStage.close()); // Fermeture de la fenêtre de notification
        timelineFadeAway.play(); // Lancement de l'animation
    }

}
