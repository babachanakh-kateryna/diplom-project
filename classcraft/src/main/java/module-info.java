module org.classcraft.classcraft {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.swing;


    opens org.classcraft to javafx.fxml;
    exports org.classcraft;
}