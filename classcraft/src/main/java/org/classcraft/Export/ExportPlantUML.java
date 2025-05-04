package org.classcraft.Export;

import org.classcraft.Composite.ClassModel;
import org.classcraft.Composite.RelationModel;
import org.classcraft.Model.DiagramModel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

// Клас для експорту діаграми у формат PlantUML
public class ExportPlantUML implements ExportStrategy {
    private File file; // Файл для збереження експорту

    // Конструктор для ініціалізації файлу
    public ExportPlantUML(File file) {
        this.file = file;
    }

    // Експортує модель діаграми у файл PlantUML
    @Override
    public void export(DiagramModel model) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write("@startuml");
            writer.newLine();
            for (ClassModel clazz : model.getClasses()) {
                System.out.println(clazz.toString());
                writer.write(clazz.toString());
                writer.newLine();
            }
            writer.newLine();
            for (RelationModel relation : model.getRelations()) {
                writer.write(relation.toString());
                writer.newLine();
            }
            writer.write("@enduml");
            writer.close();
            System.out.println("Файл створено успішно: " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
