package org.umlgenerator.Controller;

import org.umlgenerator.Composite.ClassUML;
import org.umlgenerator.Composite.RelationUML;
import org.umlgenerator.Model.DiagramModel;
import javafx.event.ActionEvent;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextInputDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Контролер для додавання зв’язку між класами
public class AddRelationController extends Controller{

    ClassUML classUML; // Клас, для якого додається зв’язок

    // Конструктор без вказівки класу
    public AddRelationController(DiagramModel model) {
        super(model);
        classUML = null;
    }

    // Конструктор із вказівкою класу
    public AddRelationController(DiagramModel model, ClassUML c) {
        super(model);
        classUML = c;
    }

    // Обробляє подію додавання зв’язку
    @Override
    public void handle(ActionEvent event) {

        // Списки для вибору джерела, цілі та типу зв’язку
        List<String> choixSrc = new ArrayList<>();
        List<String> choixTarget = new ArrayList<>();
        List<String> choixTypeRelation = new ArrayList<>();
        for(ClassUML c : model.getClassPositions()) {
            choixSrc.add(c.getName());
            choixTarget.add(c.getName());
        }

        choixTypeRelation.add("Implement");
        choixTypeRelation.add("Extend");
        choixTypeRelation.add("Use");

        // Діалоги для вибору джерела, цілі та типу зв’язку
        ChoiceDialog<String> dialSrc = new ChoiceDialog<>("Виберіть джерело", choixSrc);
        ChoiceDialog<String> dialTarget = new ChoiceDialog<>("Виберіть ціль", choixTarget);
        ChoiceDialog<String> dialTypeRelation = new ChoiceDialog<>(choixTypeRelation.get(1),choixTypeRelation);

        dialSrc.setTitle("Вибір джерела");
        dialSrc.setHeaderText("Виберіть клас-джерело:");
        dialSrc.setContentText("CВиберіть опцію:");

        dialTarget.setTitle("Вибір цілі");
        dialTarget.setHeaderText("Виберіть клас-ціль:");
        dialTarget.setContentText("Виберіть опцію:");

        dialTypeRelation.setTitle("Тип зв’язку");
        dialTypeRelation.setHeaderText("Виберіть тип зв’язку:");
        dialTypeRelation.setContentText("Виберіть опцію:");

        // Обробка вибору джерела
        if(classUML == null) {
            Optional<String> resultSrc = dialSrc.showAndWait();
            resultSrc.ifPresent(chSrc -> {
                if (!chSrc.trim().equals("Виберіть джерело")) {
                    System.out.println("src : " + chSrc);
                    target(dialTarget, dialTypeRelation, chSrc);
                } else {
                    System.out.println("You must choose a class. Action canceled.");
                }
            });
        }
        else{
            String chSrc = classUML.getName();
            System.out.println("src : " + chSrc);
            target(dialTarget, dialTypeRelation, chSrc);
        }
    }

    // Обробляє вибір цілі зв’язку
    private void target(ChoiceDialog<String> dialTarget, ChoiceDialog<String> dialTypeRelation, String chSrc) {
        Optional<String> resultTarget = dialTarget.showAndWait();
        resultTarget.ifPresent(chTarget -> {
            if (!chTarget.trim().equals("Виберіть ціль")) {
                System.out.println("target : " + chTarget);
                type(dialTypeRelation, chSrc, chTarget);
            }
            else
                System.out.println("Action canceled");
        });
    }

    // Обробляє вибір типу зв’язку та кардинальностей
    private void type(ChoiceDialog<String> dialTypeRelation, String chSrc, String chTarget) {
        Optional<String> resultTypeRelation = dialTypeRelation.showAndWait();
        resultTypeRelation.ifPresent(chTypeRelation -> {
            System.out.println("typeRelation : " + chTypeRelation);
            RelationUML newRelationUML;
            switch(chTypeRelation){
                case "Implement":
                    newRelationUML = new RelationUML(RelationUML.IMPLEMENT, chSrc, chTarget);
                    break;
                case "Extend":
                    newRelationUML = new RelationUML(RelationUML.EXTEND, chSrc, chTarget);
                    break;
                case "Use" :
                    List<String> choixCardinalite = new ArrayList<>();
                    choixCardinalite.add("0");
                    choixCardinalite.add("0..1");
                    choixCardinalite.add("1");
                    choixCardinalite.add("1..*");
                    choixCardinalite.add("*");

                    ChoiceDialog<String> dialCardSrc = new ChoiceDialog<>("Вибір кардинальності Джерело", choixCardinalite);
                    ChoiceDialog<String> dialCardTrg = new ChoiceDialog<>("Вибір кардинальності Цілі", choixCardinalite);
                    TextInputDialog labelCard = new TextInputDialog("Виберіть мітку кардинальності");

                    String cardSrc = "Вибір кардинальності Джерело";
                    String cardTrg = "Вибір кардинальності Цілі";
                    String cardLabel;

                    while(cardSrc.equals("Вибір кардинальності Джерело"))
                        cardSrc = dialCardSrc.showAndWait().get();

                    while(cardTrg.equals("Вибір кардинальності Цілі"))
                        cardTrg = dialCardTrg.showAndWait().get();

                    cardLabel = labelCard.showAndWait().get();

                    newRelationUML = new RelationUML(RelationUML.USE, chSrc, chTarget, cardSrc, cardTrg, cardLabel);
                    break;
                default:
                    System.out.println("error in type of relation");
                    return;
            }
            model.addRelation(newRelationUML);
            System.out.println("newRelation : " + newRelationUML);
        });
    }
}
