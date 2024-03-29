/*
 * RepositoryListViewCell.java                                  21 mars 2024
 * IUT de Rodez, pas de droit d'auteur
 */

package org.fsp.gitfinder.factorie;

import javafx.scene.control.ListCell;
import org.fsp.gitfinder.model.Repository;

/**
 * Construit une cellule pour la liste des dépôts.
 * @author François
 */
public class RepositoryListViewCellFactorie extends ListCell<Repository> {
    @Override
    protected void updateItem(Repository repos, boolean empty) {
        super.updateItem(repos, empty);
        if (repos != null) {
            RepositoryCell data = new RepositoryCell(repos);
            setGraphic(data.getCell());
        }
    }
}