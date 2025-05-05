package org.umlgenerator.Controller;

import org.umlgenerator.Export.ExportImage;
import org.umlgenerator.Model.DiagramModel;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;

import java.io.File;

// Контролер для експорту діаграми у формат зображення
public class ExportImageController extends Controller{

    private Node node; // Вузол для створення знімка діаграми

    // Конструктор для ініціалізації контролера
    public ExportImageController(DiagramModel model, Node node) {
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
                new FileChooser.ExtensionFilter("PNG Files", "*.png"),
                new FileChooser.ExtensionFilter("JPEG Files", "*.jpeg")
        );

        // Встановлення імені файлу за замовчуванням
        fileChooser.setInitialFileName("untitled");

        File file = fileChooser.showSaveDialog(((MenuItem)event.getSource()).getParentPopup().getScene().getWindow());

        if (file != null) {
            new ExportImage(node, file).export(model);
        }
    }
}
