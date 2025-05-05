module org.umlgenerator {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;


    opens org.umlgenerator to javafx.fxml;
    exports org.umlgenerator;
}