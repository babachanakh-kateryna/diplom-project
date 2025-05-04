package org.classcraft.View;

import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import org.classcraft.Composite.AttributeModel;
import org.classcraft.Composite.ClassModel;
import org.classcraft.Composite.MethodModel;
import org.classcraft.Composite.RelationModel;
import org.classcraft.Controller.AddRelationController;
import org.classcraft.Controller.DeleteRelationController;
import org.classcraft.Model.DiagramModel;

// Клас для відображення класу в графічному інтерфейсі
public class ClassView extends GridPane {

    private DiagramModel model; // Модель діаграми
    private ClassModel clazz; // Клас, який відображається
    private Pane previewPane; // Панель для попереднього перегляду при зміні розміру або позиції

    private Circle rightCircle, bottomCircle, bottomRightCircle; // Кола для зміни розміру
    private double minHeight = 50; // Мінімальна висота панелі

    // Конструктор для ініціалізації відображення класу
    public ClassView(ClassModel clazz, DiagramModel model) {
        this.model = model;
        this.clazz = clazz;
        this.setVisible(model.getClassVisibility(clazz));
        this.setCursor(Cursor.HAND);

        Label classNameLabel = new Label(clazz.getName());
        classNameLabel.setFont(Font.font("Arial", 16));
        classNameLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2a4d78;");

        Label classTypeLabel = new Label(clazz.getType());
        classTypeLabel.setFont(Font.font("Arial", 12));
        classTypeLabel.setStyle("-fx-text-fill: #7a8b99;");

        VBox classInfo = new VBox(5);
        classInfo.getChildren().addAll(classTypeLabel, classNameLabel);

        Separator separator1 = new Separator();

        VBox attributesSection = new VBox(5);
        for (AttributeModel attribute : clazz.getAttributes()) {
            Label attributeLabel = new Label(attribute.toString());
            attributeLabel.setStyle("-fx-text-fill: #006699;");
            attributesSection.getChildren().add(attributeLabel);
        }

        Separator separator2 = new Separator();

        VBox methodsSection = new VBox(5);
        for (MethodModel method : clazz.getMethods()) {
            Label methodLabel = new Label(method.toString());
            methodLabel.setStyle("-fx-text-fill: #ff6600;");
            methodsSection.getChildren().add(methodLabel);
        }

        add(classInfo, 0, 0);
        add(separator1, 0, 1);
        add(attributesSection, 0, 2);
        add(separator2, 0, 3);
        add(methodsSection, 0, 4);

        ColumnConstraints colConstraints = new ColumnConstraints();
        colConstraints.setFillWidth(true);
        getColumnConstraints().add(colConstraints);

        setVgap(10);
        setPadding(new Insets(10));
        setStyle("-fx-background-color: white; -fx-border-color: #2a4d78; -fx-border-width: 2px; -fx-padding: 10px;");

        // Обробка подій
        setupMouseEvents();
        // Налаштування контекстного меню
        setupContextMenu();
    }

    // Створює коло для зміни розміру
    private Circle createResizeCircle() {
        Circle circle = new Circle(10);
        circle.setFill(Color.BLUE);
        return circle;
    }

    // Оновлює позиції кіл для зміни розміру
    private void updateResizeCirclesPosition() {
        double radius = rightCircle.getRadius();

        AnchorPane.setLeftAnchor(rightCircle, AnchorPane.getLeftAnchor(this) + this.getWidth() - radius);
        AnchorPane.setTopAnchor(rightCircle, AnchorPane.getTopAnchor(this) + this.getHeight() / 2 - radius);

        AnchorPane.setLeftAnchor(bottomCircle, AnchorPane.getLeftAnchor(this) + this.getWidth() / 2 - radius);
        AnchorPane.setTopAnchor(bottomCircle, AnchorPane.getTopAnchor(this) + this.getHeight() - radius);

        AnchorPane.setLeftAnchor(bottomRightCircle, AnchorPane.getLeftAnchor(this) + this.getWidth() - radius);
        AnchorPane.setTopAnchor(bottomRightCircle, AnchorPane.getTopAnchor(this) + this.getHeight() - radius);
    }

    // Налаштовує обробку подій миші
    private void setupMouseEvents() {
        this.setOnMousePressed(event -> {
            if (event.getButton() == MouseButton.PRIMARY) { // Лівий клік
                AnchorPane anchorPane = (AnchorPane) this.getParent();

                // Створення кіл для зміни розміру
                rightCircle = createResizeCircle();
                bottomCircle = createResizeCircle();
                bottomRightCircle = createResizeCircle();
                updateResizeCirclesPosition();

                // Додавання кіл до AnchorPane
                anchorPane.getChildren().addAll(bottomRightCircle);

                // Зміна вигляду курсору на колах
                rightCircle.setCursor(Cursor.E_RESIZE);
                bottomCircle.setCursor(Cursor.S_RESIZE);
                bottomRightCircle.setCursor(Cursor.MOVE);

                // Обробка подій відпускання миші
                rightCircle.setOnMouseReleased(eventB -> {
                    double deltaX = anchorPane.sceneToLocal(event.getSceneX(), event.getSceneY()).getX() - this.getLayoutX();
                    this.setPrefWidth(Math.max(50, deltaX));
                    model.setClassSize(clazz, this.getPrefWidth(), this.getPrefHeight());
                });
                bottomCircle.setOnMouseReleased(eventB -> {
                    double deltaY = anchorPane.sceneToLocal(event.getSceneY(), event.getSceneY()).getY() - this.getLayoutY();
                    this.setPrefHeight(Math.max(this.minHeight, deltaY));
                    model.setClassSize(clazz, this.getPrefWidth(), this.getPrefHeight());
                });
                bottomRightCircle.setOnMouseReleased(eventB -> {
                    double mouseX = anchorPane.sceneToLocal(eventB.getSceneX(), eventB.getSceneY()).getX();
                    double mouseY = anchorPane.sceneToLocal(eventB.getSceneY(), eventB.getSceneY()).getY();

                    double newX = mouseX - this.getWidth();
                    double newY = mouseY - this.getHeight();

                    AnchorPane.setLeftAnchor(this, newX);
                    AnchorPane.setTopAnchor(this, newY);

                    model.setClassPosition(clazz, newX, newY);
                    updateResizeCirclesPosition();
                });

                // Попередній перегляд при перетягуванні
                bottomRightCircle.setOnMouseDragged(eventB -> {
                    anchorPane.getChildren().remove(previewPane);
                    bottomCircle.setFill(new Color(0, 0, 0, 0));
                    bottomRightCircle.setFill(new Color(0, 0, 0, 0));
                    rightCircle.setFill(new Color(0, 0, 0, 0));

                    double mouseX = anchorPane.sceneToLocal(eventB.getSceneX(), eventB.getSceneY()).getX();
                    double mouseY = anchorPane.sceneToLocal(eventB.getSceneY(), eventB.getSceneY()).getY();

                    double newX = mouseX - this.getWidth();
                    double newY = mouseY - this.getHeight();

                    previewPane = new Pane();
                    previewPane.setPrefSize(this.getWidth(), this.getHeight());
                    previewPane.setStyle("-fx-background-color: lightblue;");
                    anchorPane.getChildren().add(previewPane);
                    AnchorPane.setLeftAnchor(previewPane, newX);
                    AnchorPane.setTopAnchor(previewPane, newY);
                });
                rightCircle.setOnMouseDragged(eventB -> {
                    anchorPane.getChildren().remove(previewPane);
                    bottomCircle.setFill(new Color(0, 0, 0, 0));
                    bottomRightCircle.setFill(new Color(0, 0, 0, 0));
                    rightCircle.setFill(new Color(0, 0, 0, 0));

                    double deltaX = anchorPane.sceneToLocal(eventB.getSceneX(), eventB.getSceneY()).getX() - this.getLayoutX();

                    previewPane = new Pane();
                    previewPane.setPrefSize(Math.max(50, deltaX), this.getHeight());
                    previewPane.setStyle("-fx-background-color: lightblue;");
                    anchorPane.getChildren().add(previewPane);
                    AnchorPane.setLeftAnchor(previewPane, this.getLayoutX());
                    AnchorPane.setTopAnchor(previewPane, this.getLayoutY());
                });
                bottomCircle.setOnMouseDragged(eventB -> {
                    anchorPane.getChildren().remove(previewPane);
                    bottomCircle.setFill(new Color(0, 0, 0, 0));
                    bottomRightCircle.setFill(new Color(0, 0, 0, 0));
                    rightCircle.setFill(new Color(0, 0, 0, 0));

                    double deltaY = anchorPane.sceneToLocal(eventB.getSceneY(), eventB.getSceneY()).getY() - this.getLayoutY();

                    previewPane = new Pane();
                    previewPane.setPrefSize(this.getWidth(), Math.max(this.minHeight, deltaY));
                    previewPane.setStyle("-fx-background-color: lightblue;");
                    anchorPane.getChildren().add(previewPane);
                    AnchorPane.setLeftAnchor(previewPane, this.getLayoutX());
                    AnchorPane.setTopAnchor(previewPane, this.getLayoutY());
                });
            }
        });
    }

    // Налаштовує контекстне меню для класу
    private void setupContextMenu() {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem addAttributeItem = new MenuItem("Додати атрибут");
        addAttributeItem.setOnAction(e -> model.addAttribute(clazz));

        MenuItem addMethodItem = new MenuItem("Додати метод");
        addMethodItem.setOnAction(e -> model.addMethod(clazz));

        MenuItem deleteItem = new MenuItem("Видалити клас");
        deleteItem.setOnAction(e -> model.removeClass(clazz));

        MenuItem addRelationItem = new MenuItem("Додати зв’язок");
        addRelationItem.setOnAction(new AddRelationController(model, clazz));

        MenuItem deleteRelationItem = new MenuItem("Видалити зв’язок");
        deleteRelationItem.setOnAction(new DeleteRelationController(model, clazz));

        // Меню для приховування
        Menu hideMenu = new Menu("Приховати");

        MenuItem hideClassItem = new MenuItem("Приховати клас");
        hideClassItem.setOnAction(e -> toggleVisibility());

        MenuItem hideClassRelationsItem = new MenuItem("Приховати зв’язки класу");
        hideClassRelationsItem.setOnAction(e -> toggleRelationsVisibility(false));

        MenuItem hideAllRelationsItem = new MenuItem("Приховати всі зв’язки");
        hideAllRelationsItem.setOnAction(e -> model.toggleRelationsVisibility(false));

        MenuItem showAllRelationsItem = new MenuItem("Показати всі зв’язки");
        showAllRelationsItem.setOnAction(e -> model.toggleRelationsVisibility(true));

        hideMenu.getItems().addAll(hideClassItem, hideClassRelationsItem, new SeparatorMenuItem(), hideAllRelationsItem, showAllRelationsItem);

        // Показати все
        MenuItem showAllItem = new MenuItem("Показати все");
        showAllItem.setOnAction(e -> model.toggleAllVisibility(true));

        contextMenu.getItems().addAll(addAttributeItem, addMethodItem, addRelationItem, deleteRelationItem, new SeparatorMenuItem(), deleteItem, new SeparatorMenuItem(), hideMenu, showAllItem);

        this.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                contextMenu.show(this, e.getScreenX(), e.getScreenY());
            } else {
                contextMenu.hide();
            }
        });
    }

    // Перемикає видимість класу
    private void toggleVisibility() {
        boolean isVisible = this.isVisible();
        this.setVisible(!isVisible);
        model.setClassVisibility(clazz, !isVisible);

        // Оновлення видимості зв’язків, пов’язаних із класом
        for (RelationModel relation : model.getRelations()) {
            if (relation.getSource().equals(clazz.getName()) || relation.getTarget().equals(clazz.getName())) {
                model.setRelationVisibility(relation, !isVisible);
            }
        }
    }

    // Перемикає видимість зв’язків класу
    private void toggleRelationsVisibility(boolean visible) {
        for (RelationModel relation : model.getRelations()) {
            if (relation.getSource().equals(clazz.getName()) || relation.getTarget().equals(clazz.getName())) {
                model.setRelationVisibility(relation, visible);
            }
        }
    }

    // Встановлює мінімальну висоту панелі
    public void setHeightMin(double minHeight) {
        this.minHeight = minHeight;
    }
}

