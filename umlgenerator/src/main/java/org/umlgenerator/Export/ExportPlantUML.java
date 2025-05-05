package org.umlgenerator.Export;

import org.umlgenerator.Composite.ClassUML;
import org.umlgenerator.Model.DiagramModel;
import org.umlgenerator.Composite.RelationUML;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

// Клас для експорту діаграми у формат PlantUML
public class ExportPlantUML implements ExportStrategy {

    private File f; // Файл для збереження експорту

    // Конструктор для ініціалізації файлу
    public ExportPlantUML(File f) {
        this.f = f;
    }

    // Експортує модель діаграми у файл PlantUML
    @Override
    public void export(DiagramModel m) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(f));
            writer.write("@startuml");
            writer.newLine();
            for (ClassUML c : m.getClassPositions()) {
                System.out.println(c.toString());
                writer.write(c.toString());
                writer.newLine();
            }
            writer.newLine();
            for(RelationUML r : m.getRelations()) {
                writer.write(r.toString());
                writer.newLine();
            }
            writer.write("@enduml");
            writer.close();
            System.out.println("File created successfully: " + f.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
