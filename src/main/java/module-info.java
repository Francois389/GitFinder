module org.fsp.gitfinder {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;
    requires org.json;


    opens org.fsp.gitfinder to javafx.fxml;
    exports org.fsp.gitfinder;
    exports org.fsp.gitfinder.model;
    opens org.fsp.gitfinder.model to javafx.fxml;
    exports org.fsp.gitfinder.factorie;
    opens org.fsp.gitfinder.factorie to javafx.fxml;
    exports org.fsp.gitfinder.controleur;
    opens org.fsp.gitfinder.controleur to javafx.fxml;

}