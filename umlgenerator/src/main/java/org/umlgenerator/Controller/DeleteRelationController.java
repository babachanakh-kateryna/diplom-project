package org.umlgenerator.Controller;

import org.umlgenerator.Composite.ClassUML;
import org.umlgenerator.Model.DiagramModel;
import org.umlgenerator.Composite.RelationUML;
import javafx.event.ActionEvent;
import javafx.scene.control.ChoiceDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Контролер для видалення зв’язку між класами
public class DeleteRelationController extends Controller{

    ClassUML classUML; // Клас, для якого видаляється зв’язок

    // Конструктор для ініціалізації контролера
    public DeleteRelationController(DiagramModel model, ClassUML c) {
        super(model);
        classUML = c;
    }

    // Обробляє подію видалення зв’язку
    @Override
    public void handle(ActionEvent event) {
        List<String> choix = new ArrayList<>();
        for(RelationUML r : model.getRelation(classUML)){
            choix.add(r.toString());
        }
        ChoiceDialog<String> dial = new ChoiceDialog<>("Список зв’язків", choix);
        dial.setTitle("Видалити зв’язок");
        dial.setHeaderText("Виберіть зв’язок для видалення");
        Optional<String> result = dial.showAndWait();
        result.ifPresent(s -> {
            if(!s.equals("Список зв’язків")){
                model.removeRelation(s);
                model.notifyObservers();
            }
        });
    }
}