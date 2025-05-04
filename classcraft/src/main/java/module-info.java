module org.classcraft.classcraft {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens org.classcraft to javafx.fxml;
    exports org.classcraft;
}