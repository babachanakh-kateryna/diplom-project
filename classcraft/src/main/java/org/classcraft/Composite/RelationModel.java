package org.classcraft.Composite;

import java.io.Serializable;

/**
 * Клас, що представляє зв'язок між класами в UML-діаграмі.
 * Зв'язок може бути різних типів: EXTEND, IMPLEMENT або USE.
 * Для типу USE також можуть бути вказані кардинальності зв'язку.
 */
public class RelationModel implements Serializable {
    private final String source; // Ім’я вихідного класу
    private String type; // Тип зв’язку (EXTEND, IMPLEMENT, USE)
    private final String target; // Ім’я цільового класу

    // Кардинальності зв’язку (лише для USE)
    private String sourceMultiplicity;
    private String targetMultiplicity;
    private String relationName;

    // Статичні позначення типів зв’язків
    public static final String IMPLEMENT = "<|..";
    public static final String EXTEND = "<|--";
    public static final String USE = "<--";

    // Конструктор для EXTEND або IMPLEMENT
    public RelationModel(String type, String source, String target) {
        this.type = type;
        this.source = source;
        this.target = target;
    }

    // Конструктор для USE із зазначенням кардинальностей
    public RelationModel(String type, String source, String target,
                         String sourceMultiplicity, String targetMultiplicity, String relationName) {
        this.type = type;
        this.source = source;
        this.target = target;

        if (type.equals(USE)) {
            this.sourceMultiplicity = sourceMultiplicity;
            this.targetMultiplicity = targetMultiplicity;
            this.relationName = relationName;
        } else {
            System.out.println("Cardinality is only valid for 'USE' type relations.");
        }
    }

    @Override
    public String toString() {
        if (!type.equals(USE)) {
            // Формат: ClassB <|-- ClassA
            return target + " " + type + " " + source;
        } else {
            // Формат: ClassB "1..*" <-- "1" ClassA : attributeName
            return target + " \"" + targetMultiplicity + "\" " + USE + " \"" + sourceMultiplicity + "\" " + source + " : " + relationName;
        }
    }

    // Геттери та сеттери
    public String getSource() {
        return source;
    }

    public String getTarget() {
        return target;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSourceMultiplicity() {
        return sourceMultiplicity;
    }

    public String getTargetMultiplicity() {
        return targetMultiplicity;
    }

    public String getRelationName() {
        return relationName;
    }
}