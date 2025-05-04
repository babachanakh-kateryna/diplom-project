package org.classcraft.Controller;
import javafx.event.ActionEvent;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextInputDialog;
import org.classcraft.Composite.AttributeModel;
import org.classcraft.Composite.ClassModel;
import org.classcraft.Composite.MethodModel;
import org.classcraft.Model.DiagramModel;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Контролер для модифікації класу
public class ModifyClassController extends Controller {

    // Конструктор для ініціалізації контролера
    public ModifyClassController(DiagramModel model) {
        super(model);
    }

    // Обробляє подію модифікації класу
    @Override
    public void handle(ActionEvent event) {
        // Списки для вибору класу та типу модифікації
        List<String> classChoices = new ArrayList<>();
        List<String> modificationChoices = new ArrayList<>();
        for (ClassModel clazz : model.getClasses()) {
            classChoices.add(clazz.getName());
        }

        modificationChoices.add("Додати атрибут");
        modificationChoices.add("Додати метод");
        modificationChoices.add("Додати параметр до методу");
        modificationChoices.add("Видалити атрибут");
        modificationChoices.add("Видалити метод");

        // Діалоги для вибору класу та типу модифікації
        ChoiceDialog<String> classDialog = new ChoiceDialog<>("Виберіть клас", classChoices);
        ChoiceDialog<String> modificationDialog = new ChoiceDialog<>(modificationChoices.get(1), modificationChoices);

        classDialog.setTitle("Вибір класу");
        classDialog.setHeaderText("Виберіть клас для модифікації:");
        classDialog.setContentText("Виберіть опцію:");

        modificationDialog.setTitle("Вибір модифікації");
        modificationDialog.setHeaderText("Виберіть тип модифікації:");
        modificationDialog.setContentText("Виберіть опцію:");

        // Обробка вибору класу
        Optional<String> classResult = classDialog.showAndWait();

        classResult.ifPresent(selectedClass -> {
            if (!selectedClass.trim().equals("Виберіть клас")) {
                Optional<String> modificationResult = modificationDialog.showAndWait();
                modificationResult.ifPresent(modification -> {
                    switch (modification) {
                        case "Додати атрибут":
                            addAttribute(nameToClass(selectedClass, model));
                            break;
                        case "Додати метод":
                            addMethod(nameToClass(selectedClass, model));
                            break;
                        case "Додати параметр до методу":
                            addParameterToMethod(nameToClass(selectedClass, model));
                            break;
                        case "Видалити атрибут":
                            removeAttribute(nameToClass(selectedClass, model));
                            break;
                        case "Видалити метод":
                            removeMethod(nameToClass(selectedClass, model));
                            break;
                        default:
                            System.out.println("Помилка вибору типу модифікації");
                    }
                    model.notifyObservers();
                });
            } else {
                System.out.println("Необхідно вибрати клас. Дію скасовано.");
            }
        });
    }

    // Додає параметр до методу класу
    private void addParameterToMethod(ClassModel clazz) {
        List<String> choices = new ArrayList<>();
        for (MethodModel method : clazz.getMethods()) {
            choices.add(method.toString());
        }
        ChoiceDialog<String> dialog = new ChoiceDialog<>("Модифікувати метод", choices);
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(selectedMethod -> {
            for (MethodModel method : clazz.getMethods()) {
                if (method.toString().equals(selectedMethod)) {
                    List<String> addDeleteOptions = new ArrayList<>();
                    addDeleteOptions.add("Додати параметр");
                    addDeleteOptions.add("Видалити параметр");
                    ChoiceDialog<String> addDeleteDialog = new ChoiceDialog<>(addDeleteOptions.getFirst(), addDeleteOptions);
                    Optional<String> addDeleteResult = addDeleteDialog.showAndWait();
                    addDeleteResult.ifPresent(action -> {
                        if (action.trim().equals("Додати параметр")) {
                            TextInputDialog textDialog = new TextInputDialog();
                            textDialog.setTitle("Вибір типу параметра");
                            textDialog.setHeaderText("Введіть назву типу:");
                            textDialog.setContentText("Назва типу:");

                            Optional<String> typeResult = textDialog.showAndWait();
                            typeResult.ifPresent(type -> {
                                if (!type.trim().isEmpty() && type.matches("^[a-zA-Z_$][a-zA-Z\\d_$]*$")) {
                                    method.addParameter(new AttributeModel("", type, 0));
                                    model.notifyObservers();
                                } else {
                                    System.out.println("Назва типу повинна бути валідною.");
                                }
                            });
                        } else {
                            List<String> parameters = new ArrayList<>();
                            for (AttributeModel param : method.getParameters()) {
                                parameters.add(param.toString());
                            }
                            ChoiceDialog<String> paramDialog = new ChoiceDialog<>("Виберіть параметр для видалення", parameters);
                            Optional<String> paramResult = paramDialog.showAndWait();
                            paramResult.ifPresent(selectedParam -> {
                                AttributeModel paramToRemove = null;
                                for (AttributeModel current : method.getParameters()) {
                                    if (current.toString().equals(selectedParam)) {
                                        paramToRemove = current;
                                    }
                                }
                                method.removeParameter(paramToRemove);
                                model.notifyObservers();
                            });
                        }
                    });
                }
            }
        });
    }

    // Додає атрибут до класу
    public void addAttribute(ClassModel clazz) {
        List<String> choices = new ArrayList<>();
        choices.add("Public");
        choices.add("Private");
        choices.add("Protected");
        ChoiceDialog<String> visibilityDialog = new ChoiceDialog<>("Публічний чи приватний?", choices);
        TextInputDialog typeDialog = new TextInputDialog("Тип атрибута");
        TextInputDialog nameDialog = new TextInputDialog("Назва атрибута");

        Optional<String> visibilityResult = visibilityDialog.showAndWait();
        visibilityResult.ifPresent(visibility -> {
            int privacy;
            switch (visibility) {
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
                    System.out.println("Помилка вибору видимості");
            }

            Optional<String> typeResult = typeDialog.showAndWait();
            typeResult.ifPresent(type -> {
                if (!type.trim().isEmpty()) {
                    Optional<String> nameResult = nameDialog.showAndWait();
                    nameResult.ifPresent(name -> {
                        if (!name.isEmpty()) {
                            clazz.addAttribute(name.trim(), type.trim(), privacy);
                        }
                    });
                }
            });
        });
    }

    // Додає метод до класу
    public void addMethod(ClassModel clazz) {
        List<String> choices = new ArrayList<>();
        choices.add("Public");
        choices.add("Private");
        choices.add("Protected");
        ChoiceDialog<String> visibilityDialog = new ChoiceDialog<>("Публічний чи приватний?", choices);
        TextInputDialog returnTypeDialog = new TextInputDialog("Тип повернення");
        TextInputDialog nameDialog = new TextInputDialog("Назва методу");

        Optional<String> visibilityResult = visibilityDialog.showAndWait();
        visibilityResult.ifPresent(visibility -> {
            int privacy;
            switch (visibility) {
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
                    System.out.println("Помилка вибору видимості");
            }

            Optional<String> returnTypeResult = returnTypeDialog.showAndWait();
            returnTypeResult.ifPresent(returnType -> {
                if (!returnType.trim().isEmpty()) {
                    Optional<String> nameResult = nameDialog.showAndWait();
                    nameResult.ifPresent(name -> {
                        if (!name.isEmpty()) {
                            clazz.addMethod(name.trim(), returnType.trim(), privacy);
                        }
                    });
                }
            });
        });
    }

    // Видаляє атрибут із класу
    public void removeAttribute(ClassModel clazz) {
        List<String> choices = new ArrayList<>();
        for (AttributeModel attribute : clazz.getAttributes()) {
            choices.add(attribute.toString());
        }
        ChoiceDialog<String> dialog = new ChoiceDialog<>("Видалити атрибут", choices);
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(selected -> {
            clazz.getAttributes().removeIf(attribute -> attribute.toString().equals(selected));
        });
    }

    // Видаляє метод із класу
    public void removeMethod(ClassModel clazz) {
        List<String> choices = new ArrayList<>();
        for (MethodModel method : clazz.getMethods()) {
            choices.add(method.toString());
        }
        ChoiceDialog<String> dialog = new ChoiceDialog<>("Видалити метод", choices);
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(selected -> {
            clazz.getMethods().removeIf(method -> method.toString().equals(selected));
        });
    }

    // Знаходить клас за назвою
    public ClassModel nameToClass(String name, DiagramModel model) {
        for (ClassModel clazz : model.getClasses()) {
            if (clazz.getName().equals(name)) {
                return clazz;
            }
        }
        return null;
    }
}

