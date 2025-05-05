package org.umlgenerator.View;

import org.umlgenerator.Model.DiagramModel;
import org.umlgenerator.Composite.RelationUML;
import org.umlgenerator.Model.Subject;

// Клас для текстового відображення моделі діаграми
public class TextView implements Observer {

    // Оновлює текстовий вигляд моделі діаграми
    @Override
    public void update(Subject s) {
        DiagramModel m = (DiagramModel) s;
        System.out.println("##################VUE TEXTUELLE####################");
        m.getClassPositions().forEach(classe -> {
            System.out.println(classe.getType()+": " + classe.getName());
            // Перевірка на null перед виведенням атрибутів
            if (classe.getAttributs() != null) {
                classe.getAttributs().forEach(System.out::println); // Виведення атрибутів
            }
            // Перевірка на null перед виведенням методів
            if (classe.getMethodes() != null) {
                classe.getMethodes().forEach(System.out::println);// Виведення методів
            }
        });
        System.out.println("Relations:");
        for(RelationUML r : m.getRelations()){
            System.out.println(r.toString());
        }
    }
}
