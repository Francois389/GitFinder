module org.fsp.gitfinder {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.fsp.gitfinder to javafx.fxml;
    exports org.fsp.gitfinder;
    exports org.fsp.gitfinder.model;
    opens org.fsp.gitfinder.model to javafx.fxml;
}