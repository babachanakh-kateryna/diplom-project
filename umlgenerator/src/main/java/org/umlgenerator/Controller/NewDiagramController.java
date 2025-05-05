package org.umlgenerator.Controller;

import org.umlgenerator.Model.DiagramModel;
import javafx.event.ActionEvent;

// Контролер для створення нової діаграми
public class NewDiagramController extends Controller{

    // Конструктор для ініціалізації контролера
    public NewDiagramController(DiagramModel model) {
        super(model);
    }

    // Обробляє подію створення нової діаграми
    @Override
    public void handle(ActionEvent event) {
        model.reset();
    }
}
