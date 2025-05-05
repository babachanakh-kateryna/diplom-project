package org.umlgenerator.Controller;

import org.umlgenerator.Composite.ClassUML;
import org.umlgenerator.Model.DiagramModel;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextInputDialog;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

// Контролер для додавання порожнього класу
public class AddClassController extends Controller{

    // Конструктор для ініціалізації контролера
    public AddClassController(DiagramModel model) {
        super(model);
    }

    // Обробляє подію додавання класу
    @Override
    public void handle(ActionEvent event) {

        // Вибір типу класу
        List<String> types = Arrays.asList("class", "interface ", "abstract");
        ChoiceDialog<String> dialogueDeType = new ChoiceDialog<>("class", types);
        dialogueDeType.setTitle("Створення класу");
        dialogueDeType.setHeaderText("Виберіть тип класу:");
        dialogueDeType.setContentText("Тип:");

        Optional<String> resultatType = dialogueDeType.showAndWait();

        resultatType.ifPresent(type -> {
            TextInputDialog dialogueDeTexte = new TextInputDialog();
            dialogueDeTexte.setTitle("Створення порожнього класу");
            dialogueDeTexte.setHeaderText("Введіть назву нового класу:");
            dialogueDeTexte.setContentText("Назва класу:");

            Optional<String> resultatTexte = dialogueDeTexte.showAndWait();
            resultatTexte.ifPresent(nom -> {
                // Перевірка валідності назви
                if (nom.trim().isEmpty() || !nom.matches("^[a-zA-Z_$][a-zA-Z\\d_$]*$")) {
                    showAlert("Невалідна назва", "Назва класу повинна бути валідною та містити лише англійські літери, цифри, підкреслення або знак долара.");
                } else if (isNameExists(nom)) { // Перевірка, чи існує клас із такою назвою
                    showAlert("Назва вже існує", "Клас із такою назвою вже існує. Виберіть іншу назву.");
                } else { // Додавання класу
                    ClassUML nouvelleClassUML = new ClassUML(nom);
                    nouvelleClassUML.setType(type);
                    model.addClass(nouvelleClassUML,0,0);
                    System.out.println("Type " + type.toLowerCase() + " '" + nom + "' has been created.");
                }
            });
        });
    }

    // Перевіряє, чи існує клас із вказаною назвою
    private boolean isNameExists(String nom) {
        return model.getClassPositions().stream().anyMatch(classe -> classe.getName().equalsIgnoreCase(nom));
    }

    // Показує повідомлення про помилку
    private void showAlert(String header, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }
}