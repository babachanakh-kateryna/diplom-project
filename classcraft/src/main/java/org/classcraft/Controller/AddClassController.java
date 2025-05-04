package org.classcraft.Controller;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextInputDialog;
import org.classcraft.Composite.ClassModel;
import org.classcraft.Model.DiagramModel;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

// Контролер для додавання порожнього класу
public class AddClassController extends Controller {

    // Конструктор для ініціалізації контролера
    public AddClassController(DiagramModel model) {
        super(model);
    }

    // Обробляє подію додавання класу
    @Override
    public void handle(ActionEvent event) {
        // Вибір типу класу
        List<String> types = Arrays.asList("class", "interface", "abstract");
        ChoiceDialog<String> typeDialog = new ChoiceDialog<>("class", types);
        typeDialog.setTitle("Створення класу");
        typeDialog.setHeaderText("Виберіть тип класу:");
        typeDialog.setContentText("Тип:");

        Optional<String> typeResult = typeDialog.showAndWait();

        typeResult.ifPresent(type -> {
            TextInputDialog nameDialog = new TextInputDialog();
            nameDialog.setTitle("Створення порожнього класу");
            nameDialog.setHeaderText("Введіть назву нового класу:");
            nameDialog.setContentText("Назва класу:");

            Optional<String> nameResult = nameDialog.showAndWait();
            nameResult.ifPresent(name -> {
                // Перевірка валідності назви
                if (name.trim().isEmpty() || !name.matches("^[a-zA-Z_$][a-zA-Z\\d_$]*$")) {
                    showAlert("Невалідна назва", "Назва класу повинна бути валідною та містити лише літери, цифри, підкреслення або знак долара.");
                } else if (isNameExists(name)) { // Перевірка, чи існує клас із такою назвою
                    showAlert("Назва вже існує", "Клас із такою назвою вже існує. Виберіть іншу назву.");
                } else { // Додавання класу
                    ClassModel newClass = new ClassModel(name);
                    newClass.setType(type);
                    model.addClass(newClass, 0, 0);
                    System.out.println("Тип '" + type.toLowerCase() + "' '" + name + "' створено та додано.");
                }
            });
        });
    }

    // Перевіряє, чи існує клас із вказаною назвою
    private boolean isNameExists(String name) {
        return model.getClasses().stream().anyMatch(clazz -> clazz.getName().equalsIgnoreCase(name));
    }

    // Показує повідомлення про помилку
    private void showAlert(String header, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }
}