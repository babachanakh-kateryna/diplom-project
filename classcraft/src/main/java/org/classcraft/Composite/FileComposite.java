package org.classcraft.Composite;

import java.io.File;

/**
 * Абстрактний клас для представлення файлів та директорій у структурі композиту.
 * Використовується для створення дерева файлів і директорій.
 */
public abstract class FileComposite {
    // Фізичний файл або директорія, яка представляє цей елемент (клас або пакет)
    protected File f;

    // Абстрактний метод для виводу структури з відступом
    public abstract void display(String indent);

    // Отримати ім’я файлу без шляху
    public String getName() {
        return f.getName();
    }
}