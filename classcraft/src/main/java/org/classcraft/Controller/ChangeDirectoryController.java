package org.classcraft.Controller;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.stage.DirectoryChooser;
import org.classcraft.Model.DiagramModel;

import java.io.File;

// Контролер для зміни директорії моделі
public class ChangeDirectoryController extends Controller {

    // Конструктор для ініціалізації контролера
    public ChangeDirectoryController(DiagramModel model) {
        super(model);
    }

    // Обробляє подію зміни директорії
    @Override
    public void handle(ActionEvent event) {
        // Вибір директорії
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Виберіть директорію з пакетами: out/production/[назва_проекту] для IntelliJ або target/classes для JavaFX");

        // Відображення діалогу для вибору директорії
        File directory = directoryChooser.showDialog(((Button) event.getSource()).getScene().getWindow());

        if (directory != null && directory.exists() && !directory.isFile()) {
            model.setDirectory(directory.getPath());
            System.out.println("Директорію '" + directory.getName() + "' змінено.");
        } else {
            System.out.println("Помилка вибору директорії.");
        }
    }
}