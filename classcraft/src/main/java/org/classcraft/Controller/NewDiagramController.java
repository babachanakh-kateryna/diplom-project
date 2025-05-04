package org.classcraft.Controller;

import javafx.event.ActionEvent;
import org.classcraft.Model.DiagramModel;

// Контролер для створення нової діаграми
public class NewDiagramController extends Controller {

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