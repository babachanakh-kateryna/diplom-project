package org.classcraft.Controller;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import org.classcraft.Export.ExportImage;
import org.classcraft.Model.DiagramModel;

import java.io.File;

// Контролер для експорту діаграми у формат зображення
public class ImageExportController extends Controller {

    private Node node; // Вузол для створення знімка діаграми

    // Конструктор для ініціалізації контролера
    public ImageExportController(DiagramModel model, Node node) {
        super(model);
        this.node = node;
    }

    // Обробляє подію експорту зображення
    @Override
    public void handle(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Зберегти зображення");

        // Додавання фільтрів для файлів
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PNG файли", "*.png"),
                new FileChooser.ExtensionFilter("JPEG файли", "*.jpeg")
        );

        // Встановлення імені файлу за замовчуванням
        fileChooser.setInitialFileName("untitled");

        File file = fileChooser.showSaveDialog(((MenuItem) event.getSource()).getParentPopup().getScene().getWindow());

        if (file != null) {
            new ExportImage(node, file).export(model);
        }
    }
}