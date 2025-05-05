package org.umlgenerator.Controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import org.umlgenerator.Model.DiagramModel;

// Абстрактний клас для контролерів подій
public abstract class Controller implements EventHandler<ActionEvent> {

    protected DiagramModel model; // Модель діаграми

    // Конструктор для ініціалізації контролера з моделлю
    public Controller(DiagramModel model) {
        this.model = model;
    }

    // Абстрактний метод для обробки подій
    public abstract void handle(ActionEvent event);
}