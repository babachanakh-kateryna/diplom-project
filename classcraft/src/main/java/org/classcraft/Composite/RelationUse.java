package org.classcraft.Composite;

/**
 * Клас, що представляє відношення типу "використання" (USE)
 * з додатковими параметрами кардинальності між класами.
 */
public class RelationUse extends RelationModel {

    private String sourceMultiplicity; // Кардинальність на стороні джерела
    private String targetMultiplicity; // Кардинальність на стороні цілі

    // Конструктор для створення зв’язку з кардинальностями
    public RelationUse(String type, String source, String target,
                       String sourceMultiplicity, String targetMultiplicity) {
        super(type, source, target);
        this.sourceMultiplicity = sourceMultiplicity;
        this.targetMultiplicity = targetMultiplicity;
    }

    // Вивід зв’язку у вигляді рядка, що відповідає UML-нотації
    @Override
    public String toString() {
        return this.getTarget() + " \"" + this.targetMultiplicity + "\" " + USE + " \"" + this.sourceMultiplicity + "\" " + this.getSource();
    }
}