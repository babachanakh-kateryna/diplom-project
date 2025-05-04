package org.classcraft.View;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import org.classcraft.Composite.RelationModel;
import org.classcraft.Model.DiagramModel;

// Клас для відображення зв’язків між класами в графічному інтерфейсі
public class RelationView extends Pane {

    private RelationModel relation; // Зв’язок, який відображається
    private DiagramModel model; // Модель діаграми

    // Конструктор для ініціалізації відображення зв’язку
    public RelationView(RelationModel relation, DiagramModel model) {
        this.relation = relation;
        this.model = model;
        update();
    }

    // Оновлює відображення зв’язку
    public void update() {
        this.setVisible(model.getRelationVisibility(relation));

        if (!model.getRelations().isEmpty()) {
            DiagramModel.Position source = model.getClassPosition(relation.getSource());
            DiagramModel.Position target = model.getClassPosition(relation.getTarget());

            double sourceX, targetX, sourceY, targetY;

            // Обчислення відстаней між різними парами точок
            double verticalDistance1 = Math.sqrt(
                    Math.pow((source.getX() + source.getWidth() / 2) - (target.getX() + target.getWidth() / 2), 2) +
                            Math.pow((source.getY() + source.getHeight()) - target.getY(), 2)
            );

            double verticalDistance2 = Math.sqrt(
                    Math.pow((source.getX() + source.getWidth() / 2) - (target.getX() + target.getWidth() / 2), 2) +
                            Math.pow(source.getY() - (target.getY() + target.getHeight()), 2)
            );

            double horizontalDistance1 = Math.sqrt(
                    Math.pow((source.getX() + source.getWidth()) - target.getX(), 2) +
                            Math.pow((source.getY() + source.getHeight() / 2) - (target.getY() + target.getHeight() / 2), 2)
            );

            double horizontalDistance2 = Math.sqrt(
                    Math.pow(source.getX() - (target.getX() + target.getWidth()), 2) +
                            Math.pow((source.getY() + source.getHeight() / 2) - (target.getY() + target.getHeight() / 2), 2)
            );

            // Визначення найкоротшої відстані та відповідних точок
            double minDistance = Math.min(Math.min(verticalDistance1, verticalDistance2), Math.min(horizontalDistance1, horizontalDistance2));

            if (minDistance == verticalDistance1) {
                // Вертикальна лінія: низ джерела до верху цілі
                sourceX = source.getX() + source.getWidth() / 2;
                sourceY = source.getY() + source.getHeight();
                targetX = target.getX() + target.getWidth() / 2;
                targetY = target.getY();
            } else if (minDistance == verticalDistance2) {
                // Вертикальна лінія: верх джерела до низу цілі
                sourceX = source.getX() + source.getWidth() / 2;
                sourceY = source.getY();
                targetX = target.getX() + target.getWidth() / 2;
                targetY = target.getY() + target.getHeight();
            } else if (minDistance == horizontalDistance1) {
                // Горизонтальна лінія: права сторона джерела до лівої сторони цілі
                sourceX = source.getX() + source.getWidth();
                sourceY = source.getY() + source.getHeight() / 2;
                targetX = target.getX();
                targetY = target.getY() + target.getHeight() / 2;
            } else {
                // Горизонтальна лінія: ліва сторона джерела до правої сторони цілі
                sourceX = source.getX();
                sourceY = source.getY() + source.getHeight() / 2;
                targetX = target.getX() + target.getWidth();
                targetY = target.getY() + target.getHeight() / 2;
            }

            Line line = new Line(sourceX, sourceY, targetX, targetY);
            line.setStroke(Color.BLACK);
            line.setStrokeWidth(2);

            if (relation.getType().equals(RelationModel.IMPLEMENT)) {
                line.getStrokeDashArray().addAll(10.0, 5.0);
            }

            // Обчислення вершин трикутника
            double triangleSize = 20; // Розмір трикутника
            double angle = Math.atan2(targetY - sourceY, targetX - sourceX); // Кут лінії (у радіанах)

            // Координати вершин трикутника
            double x2 = targetX - triangleSize * Math.cos(angle - Math.PI / 6); // Перша вершина основи
            double y2 = targetY - triangleSize * Math.sin(angle - Math.PI / 6);

            double x3 = targetX - triangleSize * Math.cos(angle + Math.PI / 6); // Друга вершина основи
            double y3 = targetY - triangleSize * Math.sin(angle + Math.PI / 6);

            // Рівносторонній трикутник
            Polygon triangle = new Polygon();
            triangle.getPoints().addAll(targetX, targetY, x2, y2, x3, y3);
            triangle.setFill(Color.GRAY);
            triangle.setStroke(Color.BLACK);
            triangle.setStrokeWidth(2);

            if (relation.getType().equals(RelationModel.USE)) {
                Line baseLine = new Line(x2, y2, x3, y3);
                baseLine.setStroke(Color.GRAY);
                baseLine.setFill(Color.GRAY);
                baseLine.setStrokeWidth(3);

                double fontSize = 25;
                Label sourceCardinalityLabel = new Label(relation.getSourceMultiplicity());
                sourceCardinalityLabel.setFont(new Font(fontSize));
                Label targetCardinalityLabel = new Label(relation.getTargetMultiplicity());
                targetCardinalityLabel.setFont(new Font(fontSize));
                Label relationLabel = new Label(relation.getRelationName());

                if (relation.getRelationName() != null) {
                    relationLabel.setTranslateX((sourceX + targetX) / 2 - 2 * relation.getRelationName().length());
                    relationLabel.setTranslateY((sourceY + targetY) / 2 - 30);
                }

                if (sourceX > targetX) {
                    sourceX -= 20;
                } else {
                    targetX -= 20;
                }

                if (sourceY > targetY) {
                    sourceY -= 20;
                } else {
                    targetY -= 20;
                }

                sourceCardinalityLabel.setTranslateX(sourceX);
                sourceCardinalityLabel.setTranslateY(sourceY);

                targetCardinalityLabel.setTranslateX(targetX);
                targetCardinalityLabel.setTranslateY(targetY);

                getChildren().addAll(triangle, baseLine, line, sourceCardinalityLabel, targetCardinalityLabel, relationLabel);
            } else {
                getChildren().addAll(line, triangle);
            }
        }
    }
}