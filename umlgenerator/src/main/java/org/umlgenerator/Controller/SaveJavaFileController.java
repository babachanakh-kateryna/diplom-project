package org.umlgenerator.Controller;

import org.umlgenerator.Model.DiagramModel;
import javafx.event.ActionEvent;
import javafx.stage.DirectoryChooser;

import java.io.File;

// Контролер для збереження класів у файли Java
public class SaveJavaFileController extends Controller{

    private DiagramModel model; // Модель діаграми

    // Конструктор для ініціалізації контролера
    public SaveJavaFileController(DiagramModel model) {
        super(model);
        this.model = model;
    }

    // Обробляє подію збереження класів у файли Java
    @Override
    public void handle(ActionEvent event) {
        // Діалог вибору директорії
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Виберіть директорію для збереження класів");
        File selectedDirectory = directoryChooser.showDialog(null);

        if (selectedDirectory != null) {
            if (this.model != null) {
                this.model.saveFichierJava(selectedDirectory);
            } else {
                System.out.println("Modele non initialisé.");
            }
        } else {
            System.out.println("Directory not selected.");
        }
    }
}
