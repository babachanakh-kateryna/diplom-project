package org.classcraft.Export;
import org.classcraft.Model.DiagramModel;

import java.io.*;

// Клас для експорту моделі діаграми у серіалізованому вигляді
public class ExportModel implements ExportStrategy{
    private File file; // Файл для збереження експорту

    // Конструктор для ініціалізації файлу
    public ExportModel(File file) {
        this.file = file;
    }

    // Експортує модель діаграми у серіалізований файл
    @Override
    public void export(DiagramModel model) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(this.file));
            oos.writeObject(model.getClassPositions());
            oos.writeObject(model.getRelations());
            oos.close();
            System.out.println("Файл моделі створено успішно: " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
