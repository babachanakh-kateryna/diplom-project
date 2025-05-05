package org.umlgenerator.Controller;

import org.umlgenerator.Model.DiagramModel;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.stage.DirectoryChooser;

import java.io.File;

// Контролер для зміни директорії моделі
public class ChangeDirectoryController extends Controller{

    // Конструктор для ініціалізації контролера
    public ChangeDirectoryController(DiagramModel model) {
        super(model);
    }

    // Обробляє подію зміни директорії
    @Override
    public void handle(ActionEvent event) {
        // Вибір директорії
        DirectoryChooser selecteurDeDossiers = new DirectoryChooser();
        selecteurDeDossiers.setTitle("Виберіть директорію з пакетами: out/production/[назва_проекту] для IntelliJ або target/classes для JavaFX");

        // Відображення діалогу для вибору директорії
        File fichier = selecteurDeDossiers.showDialog(((Button)event.getSource()).getScene().getWindow());

        if (fichier != null && fichier.exists() && !fichier.isFile()) {
            model.setDirectory(fichier.getPath());
            System.out.println("Directory '" + fichier.getName() + "' changed.");
        } else {
            System.out.println("Error selecting directory.");
        }
    }
}
