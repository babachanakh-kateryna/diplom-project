package org.umlgenerator.Composite;

import java.io.Serializable;

/**
 * Клас, що представляє зв'язок між класами в UML-діаграмі.
 * Зв'язок може бути різних типів: EXTEND, IMPLEMENT або USE.
 * Для типу USE також можуть бути вказані кардинальності зв'язку.
 */
public class RelationUML implements Serializable {
    private final String source; // Ім’я вихідного класу
    private String type; // Тип зв’язку (EXTEND, IMPLEMENT, USE)
    private final String target; // Ім’я цільового класу
    private String cardinaliteSource, cardinaliteTarget, cardinaliteName; // Кардинальності зв’язку (лише для USE)

    // Статичні позначення типів зв’язків
    public final static String IMPLEMENT = "<|..";
    public final static String EXTEND = "<|--";
    public final static String USE = "<--";

    // Конструктор для EXTEND або IMPLEMENT
    public RelationUML(String type, String source, String target) {
        this.type = type;
        this.source = source;
        this.target = target;
    }

    // Конструктор для USE із зазначенням кардинальностей
    public RelationUML(String type, String source, String target, String cardinaliteSource, String cardinaliteTarget, String cardinaliteName) {
        this.type = type;
        this.source = source;
        this.target = target;
        if(type.equals(USE)) {
            this.cardinaliteSource = cardinaliteSource;
            this.cardinaliteTarget = cardinaliteTarget;
            this.cardinaliteName = cardinaliteName;
        }
        else {
            System.out.println("Cardinality is only valid for 'USE' type relations");
        }
    }

    public String toString() {
        if(!type.equals(USE)) {
            // Формат: ClassB <|-- ClassA
            return target + " " + type + " " + source;
        }
        else {
            // Формат: ClassB "1..*" <-- "1" ClassA : attributeName
            return this.getTarget() + " \"" + this.cardinaliteTarget + "\" " + USE + " \"" + this.cardinaliteSource + "\" " + this.getSource() + " : " + this.cardinaliteName;
        }
    }

    // Геттери та сеттери
    public String getSource() {
        return source;
    }

    public String getTarget() {
        return target;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getType() {
        return type;
    }

    public String getCardinaliteSource() {
        return cardinaliteSource;
    }
    public String getCardinaliteTarget() {
        return cardinaliteTarget;
    }
    public String getCardinaliteName() {
        return cardinaliteName;
    }
}