package org.classcraft.Controller;

import javafx.event.ActionEvent;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextInputDialog;
import org.classcraft.Composite.ClassModel;
import org.classcraft.Composite.RelationModel;
import org.classcraft.Model.DiagramModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Контролер для додавання зв’язку між класами
public class AddRelationController extends Controller {

    private ClassModel clazz; // Клас, для якого додається зв’язок

    // Конструктор без вказівки класу
    public AddRelationController(DiagramModel model) {
        super(model);
        this.clazz = null;
    }

    // Конструктор із вказівкою класу
    public AddRelationController(DiagramModel model, ClassModel clazz) {
        super(model);
        this.clazz = clazz;
    }

    // Обробляє подію додавання зв’язку
    @Override
    public void handle(ActionEvent event) {
        // Списки для вибору джерела, цілі та типу зв’язку
        List<String> sourceChoices = new ArrayList<>();
        List<String> targetChoices = new ArrayList<>();
        List<String> relationTypeChoices = new ArrayList<>();
        for (ClassModel clazz : model.getClasses()) {
            sourceChoices.add(clazz.getName());
            targetChoices.add(clazz.getName());
        }

        relationTypeChoices.add("Implement");
        relationTypeChoices.add("Extend");
        relationTypeChoices.add("Use");

        // Діалоги для вибору джерела, цілі та типу зв’язку
        ChoiceDialog<String> sourceDialog = new ChoiceDialog<>("Виберіть джерело", sourceChoices);
        ChoiceDialog<String> targetDialog = new ChoiceDialog<>("Виберіть ціль", targetChoices);
        ChoiceDialog<String> relationTypeDialog = new ChoiceDialog<>(relationTypeChoices.get(1), relationTypeChoices);

        sourceDialog.setTitle("Вибір джерела");
        sourceDialog.setHeaderText("Виберіть клас-джерело:");
        sourceDialog.setContentText("Виберіть опцію:");

        targetDialog.setTitle("Вибір цілі");
        targetDialog.setHeaderText("Виберіть клас-ціль:");
        targetDialog.setContentText("Виберіть опцію:");

        relationTypeDialog.setTitle("Тип зв’язку");
        relationTypeDialog.setHeaderText("Виберіть тип зв’язку:");
        relationTypeDialog.setContentText("Виберіть опцію:");

        // Обробка вибору джерела
        if (clazz == null) {
            Optional<String> sourceResult = sourceDialog.showAndWait();
            sourceResult.ifPresent(source -> {
                if (!source.trim().equals("Виберіть джерело")) {
                    System.out.println("Джерело: " + source);
                    selectTarget(targetDialog, relationTypeDialog, source);
                } else {
                    System.out.println("Необхідно вибрати клас. Дію скасовано.");
                }
            });
        } else {
            String source = clazz.getName();
            System.out.println("Джерело: " + source);
            selectTarget(targetDialog, relationTypeDialog, source);
        }
    }

    // Обробляє вибір цілі зв’язку
    private void selectTarget(ChoiceDialog<String> targetDialog, ChoiceDialog<String> relationTypeDialog, String source) {
        Optional<String> targetResult = targetDialog.showAndWait();
        targetResult.ifPresent(target -> {
            if (!target.trim().equals("Виберіть ціль")) {
                System.out.println("Ціль: " + target);
                selectRelationType(relationTypeDialog, source, target);
            } else {
                System.out.println("Дію скасовано.");
            }
        });
    }

    // Обробляє вибір типу зв’язку та кардинальностей
    private void selectRelationType(ChoiceDialog<String> relationTypeDialog, String source, String target) {
        Optional<String> relationTypeResult = relationTypeDialog.showAndWait();
        relationTypeResult.ifPresent(relationType -> {
            System.out.println("Тип зв’язку: " + relationType);
            RelationModel newRelation;
            switch (relationType) {
                case "Implement":
                    newRelation = new RelationModel(RelationModel.IMPLEMENT, source, target);
                    break;
                case "Extend":
                    newRelation = new RelationModel(RelationModel.EXTEND, source, target);
                    break;
                case "Use":
                    List<String> cardinalityChoices = new ArrayList<>();
                    cardinalityChoices.add("0");
                    cardinalityChoices.add("0..1");
                    cardinalityChoices.add("1");
                    cardinalityChoices.add("1..*");
                    cardinalityChoices.add("*");

                    ChoiceDialog<String> sourceCardinalityDialog = new ChoiceDialog<>("Вибір кардинальності джерела", cardinalityChoices);
                    ChoiceDialog<String> targetCardinalityDialog = new ChoiceDialog<>("Вибір кардинальності цілі", cardinalityChoices);
                    TextInputDialog cardinalityLabelDialog = new TextInputDialog("Виберіть мітку кардинальності");

                    String sourceCardinality = "Вибір кардинальності джерела";
                    String targetCardinality = "Вибір кардинальності цілі";
                    String cardinalityLabel;

                    while (sourceCardinality.equals("Вибір кардинальності джерела"))
                        sourceCardinality = sourceCardinalityDialog.showAndWait().get();

                    while (targetCardinality.equals("Вибір кардинальності цілі"))
                        targetCardinality = targetCardinalityDialog.showAndWait().get();

                    cardinalityLabel = cardinalityLabelDialog.showAndWait().get();

                    newRelation = new RelationModel(RelationModel.USE, source, target, sourceCardinality, targetCardinality, cardinalityLabel);
                    break;
                default:
                    System.out.println("Помилка вибору типу зв’язку. Дію скасовано.");
                    return;
            }
            model.addRelation(newRelation);
            System.out.println("Новий зв’язок: " + newRelation);
        });
    }
}