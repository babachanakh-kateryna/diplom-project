package org.umlgenerator;

import org.umlgenerator.Composite.ClassUML;
import org.umlgenerator.Model.DiagramModel;
import org.umlgenerator.View.TextView;

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

        boolean fin = false;

        // Додавання класів та взаємодія з користувачем
        while(!fin) {

            System.out.println("\nChoose an option:");
            System.out.println("1. Use an existing class file");
            System.out.println("2. Create a new, empty class");
            System.out.println("3. Export plantuml file");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");
            int choix = scanner.nextInt();
            scanner.nextLine();

            switch (choix) {
                case 1:
                    System.out.print("Enter the path to the class file : "); // out/production/sae-301-s3-2024-grp_5_sofien_clement_kateryna/Composite/Classe.class
                    String cheminFichier = scanner.nextLine();
                    File fichierClasse = new File(cheminFichier);

                    if (fichierClasse.exists() && fichierClasse.isFile()) {
                        ClassUML classUML = new ClassUML(fichierClasse);
                        model.addClass(classUML,0,0);
                    } else {
                        System.out.println("File not found. Please try again.");
                    }
                    break;
                case 2:
                    System.out.print("Enter the name of the new class: ");
                    String nomClasse = scanner.nextLine();
                    ClassUML nouvelleClassUML = new ClassUML(nomClasse);
                    model.addClass(nouvelleClassUML,0,0);
                    System.out.println("The empty class '" + nomClasse + "' was created.");
                    break;
                case 3:
                    System.out.println("export....");
                    break;
                case 4:
                    fin = true;
                    break;
                default:
                    System.out.println("Invalid entry. Please try again.");
            }
        }
        scanner.close();

    }
}
