package org.classcraft.Controller;

import javafx.event.ActionEvent;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import org.classcraft.Composite.ClassModel;
import org.classcraft.Composite.RelationModel;
import org.classcraft.Model.DiagramModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Map;

// Контролер для імпорту моделі діаграми з файлу
public class ImportController extends Controller {

    // Конструктор для ініціалізації контролера
    public ImportController(DiagramModel model) {
        super(model);
    }

    // Обробляє подію імпорту моделі
    @Override
    public void handle(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Виберіть файл моделі");

        // Дозволяються лише файли з розширенням .chrt
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Файли UML", "*.chrt"));

        // Відображення діалогу для вибору файлу
        File file = fileChooser.showOpenDialog(((MenuItem) event.getSource()).getParentPopup().getScene().getWindow());

        if (file != null && file.exists() && file.isFile()) {
            try {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
                Map<ClassModel, DiagramModel.Position> classes = (Map<ClassModel, DiagramModel.Position>) ois.readObject();
                List<RelationModel> relations = (List<RelationModel>) ois.readObject();
                ois.close();
                model.importModel(classes, relations);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Вибір файлу скасовано або файл невалідний.");
        }
    }
}

