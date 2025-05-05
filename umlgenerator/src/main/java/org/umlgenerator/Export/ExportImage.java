package org.umlgenerator.Export;

import org.umlgenerator.Model.DiagramModel;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

// Клас для експорту діаграми у формат зображення
public class ExportImage implements ExportStrategy {
    private Node node;  // Вузол для створення знімка
    private File file;  // Файл для збереження зображення

    // Конструктор для ініціалізації вузла та файлу
    public ExportImage(Node node, File file) {
        this.node = node;
        this.file = file;
    }

    // Експортує діаграму як зображення у форматі PNG
    @Override
    public void export(DiagramModel m) {
        SnapshotParameters parameters = new SnapshotParameters();
        WritableImage image = node.snapshot(parameters, null);
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
            System.out.println("Image saved successfully: " + file.getAbsolutePath());
        } catch (IOException ex) {
            System.out.println("Error saving image: " + ex.getMessage());
        }
    }
}