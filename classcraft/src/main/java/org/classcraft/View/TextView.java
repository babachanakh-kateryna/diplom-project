package org.classcraft.View;

import org.classcraft.Composite.RelationModel;
import org.classcraft.Model.DiagramModel;
import org.classcraft.Model.Subject;

// Клас для текстового відображення моделі діаграми
public class TextView implements Observer {

    // Оновлює текстовий вигляд моделі діаграми
    @Override
    public void update(Subject subject) {
        DiagramModel model = (DiagramModel) subject;
        System.out.println("##################ТЕКСТОВИЙ ВИГЛЯД####################");
        model.getClasses().forEach(clazz -> {
            System.out.println(clazz.getType() + ": " + clazz.getName());
            // Перевірка на null перед виведенням атрибутів
            if (clazz.getAttributes() != null) {
                clazz.getAttributes().forEach(System.out::println); // Виведення атрибутів
            }
            // Перевірка на null перед виведенням методів
            if (clazz.getMethods() != null) {
                clazz.getMethods().forEach(System.out::println); // Виведення методів
            }
        });
        System.out.println("Зв’язки моделі:");
        for (RelationModel relation : model.getRelations()) {
            System.out.println(relation.toString());
        }
    }
}