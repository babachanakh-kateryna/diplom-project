package org.umlgenerator.Composite;

/**
 * Клас, що представляє відношення типу "використання" (USE)
 * з додатковими параметрами кардинальності між класами.
 */
public class RelationUse extends RelationUML {

    private String cardinaliteSource; // Кардинальність на стороні джерела
    private String cardinaliteTarget; // Кардинальність на стороні цілі

    // Конструктор для створення зв’язку з кардинальностями
    public RelationUse(String type, String source, String target, String cardinaliteSource, String cardinaliteTarget) {
        super(type, source, target);
        this.cardinaliteSource=cardinaliteSource;
        this.cardinaliteTarget=cardinaliteTarget;
    }

    // Вивід зв’язку у вигляді рядка, що відповідає UML-нотації
    @Override
    public String toString() {
        return this.getTarget() + " \"" + this.cardinaliteTarget + "\" " + USE + " \"" + this.cardinaliteSource + "\" " + this.getSource();
    }
}
