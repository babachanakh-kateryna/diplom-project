package org.umlgenerator.Controller;

import org.umlgenerator.Export.ExportModel;
import org.umlgenerator.Model.DiagramModel;
import javafx.event.ActionEvent;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;

import java.io.File;

// Контролер для експорту моделі діаграми у серіалізованому вигляді
public class ExportModelController extends Controller{

    // Конструктор для ініціалізації контролера
    public ExportModelController(DiagramModel model) {
        super(model);
    }

    // Обробляє подію експорту моделі
    @Override
    public void handle(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Зберегти модель");

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Файли UML (*.chrt)", "*.chrt");
        fileChooser.getExtensionFilters().add(extFilter);

        // Встановлення імені файлу за замовчуванням
        fileChooser.setInitialFileName("projectUML.chrt");
        File file = fileChooser.showSaveDialog(((MenuItem)event.getSource()).getParentPopup().getScene().getWindow());

        model.export(new ExportModel(file));
    }
}
