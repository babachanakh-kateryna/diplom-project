package org.classcraft.Controller;

import javafx.event.ActionEvent;
import javafx.scene.control.ChoiceDialog;
import org.classcraft.Composite.ClassModel;
import org.classcraft.Composite.RelationModel;
import org.classcraft.Model.DiagramModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Контролер для видалення зв’язку між класами
public class DeleteRelationController extends Controller {

    private ClassModel clazz; // Клас, для якого видаляється зв’язок

    // Конструктор для ініціалізації контролера
    public DeleteRelationController(DiagramModel model, ClassModel clazz) {
        super(model);
        this.clazz = clazz;
    }

    // Обробляє подію видалення зв’язку
    @Override
    public void handle(ActionEvent event) {
        List<String> choices = new ArrayList<>();
        for (RelationModel relation : model.getRelationsForClass(clazz)) {
            choices.add(relation.toString());
        }
        ChoiceDialog<String> dialog = new ChoiceDialog<>("Список зв’язків", choices);
        dialog.setTitle("Видалити зв’язок");
        dialog.setHeaderText("Виберіть зв’язок для видалення");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(selected -> {
            if (!selected.equals("Список зв’язків")) {
                model.removeRelation(selected);
                model.notifyObservers();
            }
        });
    }
}