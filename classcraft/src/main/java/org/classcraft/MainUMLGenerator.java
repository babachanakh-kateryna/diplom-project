package org.classcraft;

import org.classcraft.Composite.ClassModel;
import org.classcraft.Model.DiagramModel;
import org.classcraft.View.TextView;

import java.io.File;
import java.util.Scanner;


public class MainUMLGenerator {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Створення моделі діаграми
        DiagramModel model = new DiagramModel("src");

        // Створення та реєстрація текстового вигляду як спостерігача
        TextView textView = new TextView();
        model.registerObserver(textView);

        boolean exit = false;

        // Додавання класів та взаємодія з користувачем
        while (!exit) {
            System.out.println("\nВиберіть опцію:");
            System.out.println("1. Використати існуючий файл класу");
            System.out.println("2. Створити новий порожній клас");
            System.out.println("3. Експортувати файл PlantUML");
            System.out.println("4. Вийти");
            System.out.print("Введіть ваш вибір: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.print("Введіть шлях до файлу класу: ");
                    String filePath = scanner.nextLine();
                    File classFile = new File(filePath);

                    if (classFile.exists() && classFile.isFile()) {
                        ClassModel clazz = new ClassModel(classFile);
                        model.addClass(clazz, 0, 0);
                    } else {
                        System.out.println("Файл не знайдено. Спробуйте ще раз.");
                    }
                    break;
                case 2:
                    System.out.print("Введіть назву нового класу: ");
                    String className = scanner.nextLine();
                    ClassModel newClass = new ClassModel(className);
                    model.addClass(newClass, 0, 0);
                    System.out.println("Порожній клас '" + className + "' створено.");
                    break;
                case 3:
                    System.out.println("Експортування...");
                    break;
                case 4:
                    exit = true;
                    break;
                default:
                    System.out.println("Невалідний вибір. Спробуйте ще раз.");
            }
        }
        scanner.close();
    }
}
