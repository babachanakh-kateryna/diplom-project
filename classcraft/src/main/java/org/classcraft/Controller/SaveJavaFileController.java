package org.classcraft.Controller;

import javafx.event.ActionEvent;
import javafx.stage.DirectoryChooser;
import org.classcraft.Model.DiagramModel;

import java.io.File;

// Контролер для збереження класів у файли Java
public class SaveJavaFileController extends Controller {

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
                this.model.saveJavaFiles(selectedDirectory);
            } else {
                System.out.println("Модель не ініціалізована.");
            }
        } else {
            System.out.println("Директорію не вибрано.");
        }
    }
}

