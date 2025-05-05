package org.umlgenerator.Controller;

import org.umlgenerator.Composite.AttributeUML;
import org.umlgenerator.Composite.ClassUML;
import org.umlgenerator.Composite.MethodeUML;
import org.umlgenerator.Model.DiagramModel;
import javafx.event.ActionEvent;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextInputDialog;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Контролер для модифікації класу
public class ModifyClassController extends Controller{

    // Конструктор для ініціалізації контролера
    public ModifyClassController(DiagramModel model) {
        super(model);
    }

    // Обробляє подію модифікації класу
    @Override
    public void handle(ActionEvent event) {

        // Списки для вибору класу та типу модифікації
        List<String> choixSrc = new ArrayList<>();
        List<String> choixModification = new ArrayList<>();
        for(ClassUML c : model.getClassPositions()) {
            choixSrc.add(c.getName());
        }

        choixModification.add("Додати атрибут");
        choixModification.add("Додати метод");
        choixModification.add("Додати параметр до методу");
        choixModification.add("Видалити атрибут");
        choixModification.add("Видалити метод");

        // Діалоги для вибору класу та типу модифікації
        ChoiceDialog<String> dialSrc = new ChoiceDialog<>("Виберіть клас", choixSrc);
        ChoiceDialog<String> dialModification = new ChoiceDialog<>(choixModification.get(1),choixModification);

        dialSrc.setTitle("Вибір класу");
        dialSrc.setHeaderText("Виберіть клас для модифікації:");
        dialSrc.setContentText("Виберіть опцію:");

        dialModification.setTitle("Вибір модифікації");
        dialModification.setHeaderText("Виберіть тип модифікації:");
        dialModification.setContentText("Виберіть опцію:");

        // Обробка вибору класу
        Optional<String> resultSrc = dialSrc.showAndWait();

        resultSrc.ifPresent(chSrc -> {
            if (!chSrc.trim().equals("Виберіть клас")) {
                Optional<String> resultModification = dialModification.showAndWait();
                resultModification.ifPresent(chMod -> {
                   switch(chMod){
                       case "Додати атрибут":
                           addAttribut(nameToClass(chSrc, model));
                           break;
                       case "Додати метод":
                           addMethode(nameToClass(chSrc, model));
                           break;
                       case "Додати параметр до методу":
                           addAttributToMethode(nameToClass(chSrc, model));
                           break;
                       case "Видалити атрибут":
                           removeAttribut(nameToClass(chSrc, model));
                           break;
                       case "Видалити метод":
                           removeMethode(nameToClass(chSrc, model));
                           break;
                       default:
                           System.out.println("Erreur de modification");
                   }
                    model.notifyObservers();
                });
            }
            else{
                System.out.println("You must choose a class, the action has been cancelled");
            }
        });
    }

    // Додає параметр до методу класу
    private void addAttributToMethode(ClassUML c) {
        List<String> choix = new ArrayList<>();
        for(MethodeUML m : c.getMethodes()){
            choix.add(m.toString());
        }
        ChoiceDialog<String> dial = new ChoiceDialog<>("Модифікувати метод", choix);
        Optional<String> result = dial.showAndWait();
        result.ifPresent(ch -> {
            for(MethodeUML m : c.getMethodes()){
                if(m.toString().equals(ch)) {
                    List<String> add_del = new ArrayList<>();
                    add_del.add("Додати параметр");
                    add_del.add("Видалити параметр");
                    ChoiceDialog<String> dial_add_del = new ChoiceDialog<>(add_del.getFirst(), add_del);
                    Optional<String> result_add_del = dial_add_del.showAndWait();
                    result_add_del.ifPresent(chAdd -> {
                        if(chAdd.trim().equals("Додати параметр")){
                            TextInputDialog dialogueDeTexte = new TextInputDialog();
                            dialogueDeTexte.setTitle("Вибір типу параметра");
                            dialogueDeTexte.setHeaderText("Введіть назву типу:");
                            dialogueDeTexte.setContentText("Назва типу:");

                            Optional<String> resultatTexte = dialogueDeTexte.showAndWait();
                            resultatTexte.ifPresent(type -> {
                                if (!type.trim().isEmpty() && type.matches("^[a-zA-Z_$][a-zA-Z\\d_$]*$")) {
                                    m.addParameter(new AttributeUML("", type, 0));
                                    model.notifyObservers();
                                } else {
                                    System.out.println("Name is invalid");
                                }
                            });
                        }
                        else{
                            List<String> parametres = new ArrayList<>();
                            for(AttributeUML p : m.getParameters()){
                                parametres.add(p.toString());
                            }
                            ChoiceDialog<String> dial_param = new ChoiceDialog<>("Виберіть параметр для видалення", parametres);
                            Optional<String> result_param = dial_param.showAndWait();
                            result_param.ifPresent(chParam -> {
                                AttributeUML p = null;
                                for(AttributeUML current : m.getParameters()){
                                    if(current.toString().equals(chParam)){
                                        p = current;
                                    }
                                }
                                m.removeParameter(p);
                                model.notifyObservers();
                            });
                        }
                    });
                }
            }
        });
    }

    // Додає атрибут до класу
    public void addAttribut(ClassUML c){
        List<String> choix = new ArrayList<>();
        choix.add("Public");
        choix.add("Private");
        choix.add("Protected");
        ChoiceDialog<String> Pub_Prv = new ChoiceDialog<>("Public або Private ?", choix);
        TextInputDialog type = new TextInputDialog("Тип атрибута");
        TextInputDialog nom = new TextInputDialog("Назва атрибута");

        Optional<String> resultPub = Pub_Prv.showAndWait();
        resultPub.ifPresent(pub -> {
            int privacy;
            switch(pub){
                case "Public":
                    privacy = Modifier.PUBLIC;
                    break;
                case "Private":
                    privacy = Modifier.PRIVATE;
                    break;
                case "Protected":
                    privacy = Modifier.PROTECTED;
                    break;
                default:
                    privacy = Modifier.PRIVATE;
                    System.out.println("Erreur Privacy");
            }

            Optional<String> resultType = type.showAndWait();
            resultType.ifPresent(ty -> {
                if(!ty.trim().isEmpty()) {
                    Optional<String> resultNom = nom.showAndWait();
                    resultNom.ifPresent(n -> {
                        if(!n.isEmpty()) {
                            c.addAttribut(n.trim(), ty.trim(), privacy);
                        }
                    });
                }
            });
        });
    }

    // Додає метод до класу
    public void addMethode(ClassUML c){
        List<String> choix = new ArrayList<>();
        choix.add("Public");
        choix.add("Private");
        choix.add("Protected");
        ChoiceDialog<String> Pub_Prv = new ChoiceDialog<>("Public або Private ?", choix);
        TextInputDialog type = new TextInputDialog("Тип повернення");
        TextInputDialog nom = new TextInputDialog("Назва методу");

        Optional<String> resultPub = Pub_Prv.showAndWait();
        resultPub.ifPresent(pub -> {
            int privacy;
            switch(pub){
                case "Public":
                    privacy = Modifier.PUBLIC;
                    break;
                case "Private":
                    privacy = Modifier.PRIVATE;
                    break;
                case "Protected":
                    privacy = Modifier.PROTECTED;
                    break;
                default:
                    privacy = Modifier.PRIVATE;
                    System.out.println("Erreur Privacy");
            }

            Optional<String> resultType = type.showAndWait();
            resultType.ifPresent(ty -> {
                if(!ty.trim().isEmpty()) {
                    Optional<String> resultNom = nom.showAndWait();
                    resultNom.ifPresent(n -> {
                        if(!n.isEmpty()) {
                            c.addMethode(n.trim(), ty.trim(), privacy);
                        }
                    });
                }
            });
        });
    }

    // Видаляє атрибут із класу
    public void removeAttribut(ClassUML c){
        List<String> choix = new ArrayList<>();
        for(AttributeUML a : c.getAttributs()){
            choix.add(a.toString());
        }
        ChoiceDialog<String> dial = new ChoiceDialog<>("Видалити атрибут", choix);
        Optional<String> result = dial.showAndWait();
        result.ifPresent(ch -> {
            c.getAttributs().removeIf(a -> a.toString().equals(ch));
        });
    }

    // Видаляє метод із класу
    public void removeMethode(ClassUML c){
        List<String> choix = new ArrayList<>();
        for(MethodeUML m : c.getMethodes()){
            choix.add(m.toString());
        }
        ChoiceDialog<String> dial = new ChoiceDialog<>("Видалити метод", choix);
        Optional<String> result = dial.showAndWait();
        result.ifPresent(ch -> {
            c.getMethodes().removeIf(m -> m.toString().equals(ch));
        });
    }

    // Знаходить клас за назвою
    public ClassUML nameToClass(String nom, DiagramModel model){
        for(ClassUML c : model.getClassPositions()){
            if(c.getName().equals(nom)){
                return c;
            }
        }
        return null;
    }
}
