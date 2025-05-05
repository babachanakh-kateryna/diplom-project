package org.umlgenerator.Export;

import org.umlgenerator.Model.DiagramModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

// Клас для експорту моделі діаграми у серіалізованому вигляді
public class ExportModel implements ExportStrategy{
    private File f; // Файл для збереження експорту

    // Конструктор для ініціалізації файлу
    public ExportModel(File f) {
        this.f = f;
    }

    // Експортує модель діаграми у серіалізований файл
    @Override
    public void export(DiagramModel m) {
        try{
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(this.f));
            oos.writeObject(m.getClassesPosition());
            oos.writeObject(m.getRelations());
            oos.close();
            System.out.println("Template file created successfully: " + f.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
