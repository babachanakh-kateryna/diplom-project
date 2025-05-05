package org.umlgenerator.Controller;

import org.umlgenerator.Composite.ClassUML;
import org.umlgenerator.Model.DiagramModel;
import org.umlgenerator.Composite.RelationUML;
import javafx.event.ActionEvent;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Map;

// Контролер для імпорту моделі діаграми з файлу
public class ImportController extends Controller{

    // Конструктор для ініціалізації контролера
    public ImportController(DiagramModel model) {
        super(model);
    }

    // Обробляє подію імпорту моделі
    @Override
    public void handle(ActionEvent event) {
        FileChooser selecteurDeFichiers = new FileChooser();
        selecteurDeFichiers.setTitle("Виберіть файл моделі");

        // Дозволяються лише файли з розширенням .chrt
        selecteurDeFichiers.getExtensionFilters().add(new FileChooser.ExtensionFilter(".chrt", "*.chrt"));

        // Відображення діалогу для вибору файлу
        File fichier = selecteurDeFichiers.showOpenDialog(((MenuItem)event.getSource()).getParentPopup().getScene().getWindow());

        if (fichier != null && fichier.exists() && fichier.isFile()) {
            try {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fichier));
                Map<ClassUML, DiagramModel.Position> lc = (Map<ClassUML, DiagramModel.Position>) ois.readObject();
                List<RelationUML> lr = (List<RelationUML>) ois.readObject();
                ois.close();
                model.importModel(lc,lr);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Error: file not found or is not a file.");
        }
    }
}
