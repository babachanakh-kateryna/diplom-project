package org.classcraft.View;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.Label;
import org.classcraft.Composite.ClassModel;
import org.classcraft.Composite.RelationModel;
import org.classcraft.Model.DiagramModel;
import org.classcraft.Model.Subject;

import java.util.Map;

// Клас для відображення діаграми у графічному інтерфейсі
public class DiagramView extends AnchorPane implements Observer {

    private static final double ZOOM_MIN = 0.2; // Мінімальний масштаб (20%)
    private static final double ZOOM_MAX = 2.03; // Максимальний масштаб (200%)

    private DiagramModel model; // Модель діаграми
    private AnchorPane contentPane; // Панель для вмісту діаграми
    private Label zoomIndicator; // Індикатор масштабу
    private double lastX, lastY; // Координати для переміщення

    // Конструктор для ініціалізації відображення діаграми
    public DiagramView(DiagramModel model, double width, double height) {
        this.setPrefSize(width, height);
        this.model = model;
        model.registerObserver(this);

        contentPane = new AnchorPane();
        contentPane.setPrefSize(width, height);
        this.getChildren().add(contentPane);

        this.setStyle("-fx-background-color: gray;");

        setupZooming(); // Налаштування масштабування
        setupMousePanning(); // Налаштування переміщення мишею
        setupZoomIndicator(); // Налаштування індикатора масштабу
    }

    // Налаштовує індикатор масштабу
    private void setupZoomIndicator() {
        zoomIndicator = new Label("Zoom: 100%");
        zoomIndicator.setStyle("-fx-background-color: white; -fx-border-color: black;");
        zoomIndicator.setPrefWidth(80);

        // Позиціонування внизу ліворуч
        AnchorPane.setTopAnchor(zoomIndicator, 10.0);
        AnchorPane.setLeftAnchor(zoomIndicator, 10.0);

        this.getChildren().add(zoomIndicator);
    }

    // Налаштовує переміщення діаграми мишею
    private void setupMousePanning() {
        this.setOnMousePressed(event -> {
            if (event.isSecondaryButtonDown()) {
                lastX = event.getSceneX();
                lastY = event.getSceneY();
                this.setCursor(Cursor.CLOSED_HAND);
            } else {
                Node clickedNode = event.getPickResult().getIntersectedNode();
                if (!isDescendantOf(clickedNode)) {
                    update(model);
                }
            }
        });

        this.setOnMouseDragged(event -> {
            if (event.isSecondaryButtonDown()) {
                double deltaX = event.getSceneX() - lastX;
                double deltaY = event.getSceneY() - lastY;

                for (Node child : contentPane.getChildren()) {
                    Double currentLeftAnchor = AnchorPane.getLeftAnchor(child);
                    Double currentTopAnchor = AnchorPane.getTopAnchor(child);

                    AnchorPane.setLeftAnchor(child, (currentLeftAnchor == null ? 0.0 : currentLeftAnchor) - deltaX);
                    AnchorPane.setTopAnchor(child, (currentTopAnchor == null ? 0.0 : currentTopAnchor) - deltaY);
                }

                lastX = event.getSceneX();
                lastY = event.getSceneY();
            }
        });

        this.setOnMouseReleased(event -> {
            // Повернення стандартного курсору
            this.setCursor(Cursor.DEFAULT);
        });
    }

    // Налаштовує масштабування діаграми
    private void setupZooming() {
        this.setOnScroll(event -> {
            double currentScaleX = contentPane.getScaleX();
            double currentScaleY = contentPane.getScaleY();

            double zoomFactor = event.getDeltaY() > 0 ? 1.05 : 0.95;
            double newScaleX = currentScaleX * zoomFactor;
            double newScaleY = currentScaleY * zoomFactor;

            // Застосування обмежень масштабу
            if (newScaleX >= ZOOM_MIN && newScaleX <= ZOOM_MAX) {
                contentPane.setScaleX(newScaleX);
                contentPane.setScaleY(newScaleY);

                // Оновлення індикатора масштабу
                int zoomPercentage = (int) (newScaleX * 100);
                zoomIndicator.setText("Zoom: " + zoomPercentage + "%");
            }

            event.consume();
        });
    }


    // Перевіряє, чи є вузол нащадком ClassView
    private boolean isDescendantOf(Node node) {
        while (node != null) {
            if (node instanceof ClassView) {
                return true;
            }
            node = node.getParent();
        }
        return false;
    }

    // Оновлює відображення діаграми
    @Override
    public void update(Subject s) {
        this.model = (DiagramModel) s;

        contentPane.getChildren().clear();

        for (RelationModel relation : model.getRelations()) {
            RelationView relationView = new RelationView(relation, model);
            contentPane.getChildren().add(relationView);
        }

        for (Map.Entry<ClassModel, DiagramModel.Position> entry : model.getClassPositions().entrySet()) {
            ClassModel clazz = entry.getKey();
            DiagramModel.Position position = entry.getValue();

            ClassView classView = new ClassView(clazz, this.model);

            AnchorPane.setLeftAnchor(classView, position.getX());
            AnchorPane.setTopAnchor(classView, position.getY());

            contentPane.getChildren().add(classView);

            if (position.getWidth() != 0 && position.getHeight() != 0) {
                classView.setPrefWidth(position.getWidth());
                classView.setPrefHeight(position.getHeight());
                classView.setHeightMin(position.getMinHeight());
            } else {
                classView.applyCss();
                classView.layout();
                position.setWidth(classView.prefWidth(-1));
                position.setHeight(classView.prefHeight(-1));
                position.setMinHeight(classView.prefHeight(-1));
            }
        }
    }
}