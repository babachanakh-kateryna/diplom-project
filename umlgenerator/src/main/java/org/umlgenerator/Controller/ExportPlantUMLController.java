package org.umlgenerator.Controller;

import org.umlgenerator.Export.ExportPlantUML;
import org.umlgenerator.Model.DiagramModel;
import javafx.event.ActionEvent;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;

import java.io.File;

// Контролер для експорту діаграми у формат PlantUML
public class ExportPlantUMLController extends Controller{

    // Конструктор для ініціалізації контролера
    public ExportPlantUMLController(DiagramModel model) {
        super(model);
    }

    // Обробляє подію експорту у файл PlantUML
    @Override
    public void handle(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Зберегти файл .puml");

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Файли PlantUML (*.puml)", "*.puml");
        fileChooser.getExtensionFilters().add(extFilter);

        // Встановлення імені файлу за замовчуванням
        fileChooser.setInitialFileName("diagram.puml");
        File file = fileChooser.showSaveDialog(((MenuItem)event.getSource()).getParentPopup().getScene().getWindow());

        model.export(new ExportPlantUML(file));
    }
}
