module org.classcraft.classcraft {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.classcraft to javafx.fxml;
    exports org.classcraft;
}