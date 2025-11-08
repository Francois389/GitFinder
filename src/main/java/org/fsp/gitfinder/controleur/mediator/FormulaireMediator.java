package org.fsp.gitfinder.controleur.mediator;

import javafx.scene.control.*;
import javafx.scene.image.ImageView;

/**
 * Interface définissant le Mediator pour le formulaire de repository.
 * Centralise la communication entre les composants de l'interface.
 *
 * @author François de Saint Palais
 */
public interface FormulaireMediator {

    /**
     * Notifie le médiateur qu'un composant a changé
     *
     * @param composant le composant qui a déclenché l'événement
     * @param evenement le type d'événement
     */
    void notifier(Object composant, String evenement);

    /**
     * Enregistre les composants auprès du médiateur
     */
    void enregistrerComposants(
        TextField nomInput,
        TextField cheminInput,
        TextArea descriptionInput,
        ImageView imageRepo,
        ImageView statusChemin,
        Label erreurCheminInvalide,
        Button btnAjouter,
        Button btnEnregistrer,
        Button btnAjouterEtQuitter
    );
}