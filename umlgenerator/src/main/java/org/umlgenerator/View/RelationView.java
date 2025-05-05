package org.umlgenerator.View;

import org.umlgenerator.Model.DiagramModel;
import org.umlgenerator.Composite.RelationUML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;

// Клас для відображення зв’язків між класами в графічному інтерфейсі
public class RelationView extends Pane{
    RelationUML r; // Зв’язок, який відображається
    DiagramModel model; // Модель діаграми

    // Конструктор для ініціалізації відображення зв’язку
    public RelationView(RelationUML r, DiagramModel m) {
        this.r = r;
        this.model = m;
        update();
    }

    // Оновлює відображення зв’язку
    public void update() {
        this.setVisible(model.getRelationVisibility(r));

        if (!model.getRelations().isEmpty()) {

            DiagramModel.Position src = model.getPositionClasse(r.getSource());
            DiagramModel.Position trg = model.getPositionClasse(r.getTarget());


            double srcX, trgX, srcY, trgY;

            // Обчислення відстаней між різними парами точок
            double distanceVertical1 = Math.sqrt(
                    Math.pow((src.getX() + src.getWidth() / 2) - (trg.getX() + trg.getWidth() / 2), 2) + //Горизонтальна різниця
                            Math.pow((src.getY() + src.getHeight()) - trg.getY(), 2)                     //Вертикальна різниця
            );

            double distanceVertical2 = Math.sqrt(
                    Math.pow((src.getX() + src.getWidth() / 2) - (trg.getX() + trg.getWidth() / 2), 2) + // Горизонтальна різниця
                            Math.pow(src.getY() - (trg.getY() + trg.getHeight()), 2)                     //Вертикальна різниця
            );

            double distanceHorizontal1 = Math.sqrt(
                    Math.pow((src.getX() + src.getWidth()) - trg.getX(), 2) +                                    // Горизонтальна різниця
                            Math.pow((src.getY() + src.getHeight() / 2) - (trg.getY() + trg.getHeight() / 2), 2) //Вертикальна різниця
            );

            double distanceHorizontal2 = Math.sqrt(
                    Math.pow(src.getX() - (trg.getX() + trg.getWidth()), 2) +                                    // Горизонтальна різниця
                            Math.pow((src.getY() + src.getHeight() / 2) - (trg.getY() + trg.getHeight() / 2), 2) //Вертикальна різниця
            );

            // Визначення найкоротшої відстані та відповідних точок
            double minDistance = Math.min(Math.min(distanceVertical1, distanceVertical2), Math.min(distanceHorizontal1, distanceHorizontal2));

            if (minDistance == distanceVertical1) {
                // Вертикальна лінія: низ джерела до верху цілі
                srcX = src.getX() + src.getWidth() / 2;
                srcY = src.getY() + src.getHeight();
                trgX = trg.getX() + trg.getWidth() / 2;
                trgY = trg.getY();
            } else if (minDistance == distanceVertical2) {
                // Вертикальна лінія: верх джерела до низу цілі
                srcX = src.getX() + src.getWidth() / 2;
                srcY = src.getY();
                trgX = trg.getX() + trg.getWidth() / 2;
                trgY = trg.getY() + trg.getHeight();
            } else if (minDistance == distanceHorizontal1) {
                // Горизонтальна лінія: права сторона джерела до лівої сторони цілі
                srcX = src.getX() + src.getWidth();
                srcY = src.getY() + src.getHeight() / 2;
                trgX = trg.getX();
                trgY = trg.getY() + trg.getHeight() / 2;
            } else {
                // Горизонтальна лінія: ліва сторона джерела до правої сторони цілі
                srcX = src.getX();
                srcY = src.getY() + src.getHeight() / 2;
                trgX = trg.getX() + trg.getWidth();
                trgY = trg.getY() + trg.getHeight() / 2;
            }

            Line line = new Line(srcX, srcY, trgX, trgY);
            line.setStroke(Color.BLACK);
            line.setStrokeWidth(2);

            if(r.getType().equals(RelationUML.IMPLEMENT))
                line.getStrokeDashArray().addAll(10.0, 5.0);

            // Обчислення вершин трикутника
            double triangleSize = 20; // Розмір трикутника
            double angle = Math.atan2(trgY - srcY, trgX - srcX); // Кут лінії (у радіанах)

            // Координати вершин трикутника
            double x2 = trgX - triangleSize * Math.cos(angle - Math.PI / 6); // Перша вершина основи
            double y2 = trgY - triangleSize * Math.sin(angle - Math.PI / 6);

            double x3 = trgX - triangleSize * Math.cos(angle + Math.PI / 6); // Друга вершина основи
            double y3 = trgY - triangleSize * Math.sin(angle + Math.PI / 6);

            // Рівносторонній трикутник
            Polygon triangle = new Polygon();

            triangle.getPoints().addAll(trgX, trgY, x2, y2, x3, y3);
            triangle.setFill(Color.GRAY);
            triangle.setStroke(Color.BLACK); // колір
            triangle.setStrokeWidth(2);
            if(r.getType().equals(RelationUML.USE)){
                Line effacement = new Line(x2, y2, x3, y3);
                effacement.setStroke(Color.GRAY);
                effacement.setFill(Color.GRAY);
                effacement.setStrokeWidth(3);

                double font_size = 25;
                Label cardSrc = new Label(r.getCardinaliteSource());
                cardSrc.setFont(new Font(font_size));
                Label cardTrg = new Label(r.getCardinaliteTarget());
                cardTrg.setFont(new Font(font_size));
                Label cardLabel = new Label(r.getCardinaliteName());

                if(r.getCardinaliteName() != null) {
                    cardLabel.setTranslateX((srcX + trgX) / 2 - 2 * r.getCardinaliteName().length());
                    cardLabel.setTranslateY((srcY + trgY) / 2 - 30);
                }

                if(srcX > trgX)
                    srcX -= 20;
                else
                    trgX -= 20;

                if(srcY > trgY)
                    srcY -= 20;
                else
                    trgY -= 20;

                cardSrc.setTranslateX(srcX);
                cardSrc.setTranslateY(srcY);

                cardTrg.setTranslateX(trgX);
                cardTrg.setTranslateY(trgY);

                getChildren().addAll(triangle, effacement, line, cardSrc, cardTrg, cardLabel);
            }
            else
                getChildren().addAll(line, triangle);
        }
    }
}
