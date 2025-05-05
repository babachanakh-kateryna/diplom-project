package org.umlgenerator.Model;

import org.umlgenerator.Composite.AttributeUML;
import org.umlgenerator.Composite.ClassUML;
import org.umlgenerator.Composite.MethodeUML;
import org.umlgenerator.Composite.RelationUML;
import org.umlgenerator.Export.ExportStrategy;
import org.umlgenerator.View.Observer;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextInputDialog;

import java.io.*;
import java.lang.reflect.Modifier;
import java.util.*;

public class DiagramModel implements Subject {

    private String directory; // Робоча директорія для сканування .class файлів
    List<Observer> observers; // Список підписаних спостерігачів
    Map<ClassUML, Position> classPositions;  // Положення кожного класу
    List<RelationUML> relationUMLS; // Список усіх зв’язків
    private Map<ClassUML, Boolean> visibiliteClasses; // Видимість класів
    private Map<RelationUML, Boolean> visibiliteRelations; // Видимість зв’язків

    // Конструктор, що ініціалізує модель діаграми
    public DiagramModel(String directory) {
        if(new File(directory).isDirectory()) {
            this.directory = directory;
            ClassUML.setRepertoire(this.directory);
        }else
            throw new IllegalArgumentException("Provided directory does not exist");
        observers = new ArrayList<>();
        classPositions = new HashMap<>();
        relationUMLS = new ArrayList<>();
        visibiliteClasses = new HashMap<>();
        visibiliteRelations = new HashMap<>();
    }

    // Імпортує модель із заданими класами та зв’язками
    public void importModel(Map<ClassUML, Position> cl, List<RelationUML> lr){
        this.relationUMLS = lr;
        this.classPositions = cl;
        notifyObservers();
    }

    // Додає клас до діаграми
    public void addClass(ClassUML classUML, double x , double y) {

        if(this.getClassPositions().contains(classUML)){
            return;
        }
        if(x + y != 0) {
            classPositions.put(classUML, new Position(x, y));
        }else{
            classPositions.put(classUML, this.findFreePosition());
        }

        // Переглядає зв’язки класу для додавання
        for (RelationUML r : classUML.getRelations()) {
            // Перевірка наявності вихідного та цільового класів зв'язку на діаграмі
            if (isClassPresent(r.getSource()) && isClassPresent(r.getTarget())) {
                if (!relationUMLS.contains(r)) {
                    relationUMLS.add(r);
                }
            }
        }

        //  інші класи, що вже присутні, щоб перевірити та додати зв'язки, де новий клас є цільовим
        for (ClassUML c : this.getClassPositions()) {
            if (!c.equals(classUML)) {
                for (RelationUML r : c.getRelations()) {
                    // Перевірка наявності вихідного та цільового класів зв'язку на діаграмі
                    if (r.getTarget().equals(classUML.getName())) {
                        if (isClassPresent(r.getSource())) {
                            if (!relationUMLS.contains(r)) {
                                relationUMLS.add(r);
                            }
                        }
                    }
                }
            }
        }
        notifyObservers();
    }

    // Обчислює вільну позицію для нового класу
    private Position findFreePosition() {
        double widthDef = 100;
        double heightDef = 80;
        double spacing = 20;
        double maxWidth = ApplicationState.getCurrentWindowWidth(); // Largeur de la fenêtre


        double x = spacing;
        double y = spacing;

        boolean positionFind;
        do {
            positionFind = true;
            for (ClassUML c : this.getClassPositions()) {
                Position pos = this.classPositions.get(c);
                if (x + widthDef > pos.getX() && x < pos.getX() + pos.getWidth() &&
                        y + heightDef > pos.getY() && y < pos.getY() + pos.getHeight()) {
                    positionFind = false;
                    break;
                }
            }
            if (!positionFind) {
                x += widthDef + spacing;
                if (x + widthDef + spacing > maxWidth) {
                    x = spacing;
                    y += heightDef + spacing;
                }
            }
        } while (!positionFind);

        return new Position(x, y);
    }

    // Видаляє клас із діаграми
    public void removeClass(ClassUML classUML) {
        relationUMLS.removeIf(r -> r.getSource().equals(classUML.getName()) || r.getTarget().equals(classUML.getName()));
        classPositions.remove(classUML);
        notifyObservers();
    }

    // Перевіряє, чи присутній клас у діаграмі
    private boolean isClassPresent(String source) {
        for(ClassUML c : this.getClassPositions()){
            if(c.getName().equals(source)){
                return true;
            }
        }
        return false;
    }

    // Додає зв’язок
    public void addRelation(RelationUML relationUML) {
        for(ClassUML c : this.getClassPositions()){
            if(relationUML.getSource().equals(c.getName())){
                relationUMLS.add(relationUML);
                c.addRelation(relationUML);
            }
        }
        notifyObservers();
    }

    // Додає атрибут до класу
    public void addAttribute(ClassUML classUML) {
        // Список варіантів видимості
        List<String> choices = new ArrayList<>();
        choices.add("public");
        choices.add("protected");
        choices.add("private");

        ChoiceDialog<String> visibilityDialog = new ChoiceDialog<>("public", choices);

        visibilityDialog.setTitle("Додавання атрибута");
        visibilityDialog.setHeaderText("Виберіть видимість:");

        Optional<String> visibilityResult = visibilityDialog.showAndWait();

        // Якщо користувач обрав видимість
        if (visibilityResult.isPresent()) {
            String visibility = visibilityResult.get();

            // Список варіантів типу
            List<String> typeChoices = List.of("int", "double", "String", "boolean");

            ChoiceDialog<String> typeDialog = new ChoiceDialog<>("int", typeChoices);
            typeDialog.setTitle("Тип атрибута");
            typeDialog.setHeaderText("Виберіть тип атрибута:");

            Optional<String> typeResult = typeDialog.showAndWait();

            TextInputDialog nomDialog = new TextInputDialog();
            nomDialog.setTitle("Назва атрибута");
            nomDialog.setHeaderText("Введіть назву атрибута:");
            nomDialog.setContentText("Назва:");

            Optional<String> nameResult = nomDialog.showAndWait();

            // Якщо користувач вибрав тип та ім'я
            if (typeResult.isPresent() && nameResult.isPresent()) {
                String type = typeResult.get();
                String nom = nameResult.get();
                int privacy = switch (visibility) {
                    case "public" -> Modifier.PUBLIC;
                    case "protected" -> Modifier.PROTECTED;
                    default -> Modifier.PRIVATE;
                };

                classUML.addAttribut(nom, type, privacy);
                Position p = getPositionClasse(classUML.getName());
                p.minHeight+=22;
                notifyObservers();
            }
        }
    }

    // Додає метод до класу
    public void addMethod(ClassUML classUML) {
        // Список варіантів видимості
        List<String> choixVisibilite = new ArrayList<>();
        choixVisibilite.add("public");
        choixVisibilite.add("protected");
        choixVisibilite.add("private");

        ChoiceDialog<String> dialogVisibilite = new ChoiceDialog<>("public", choixVisibilite);

        dialogVisibilite.setTitle("Додавання методу");
        dialogVisibilite.setHeaderText("Виберіть видимість:");

        Optional<String> resultatVisibilite = dialogVisibilite.showAndWait();

        if (resultatVisibilite.isPresent()) {
            String visibilite = resultatVisibilite.get();

            // Liste des choix de types de retour
            List<String> choixTypesRetour = List.of("void", "int", "double", "String", "boolean");
            ChoiceDialog<String> dialogTypeRetour = new ChoiceDialog<>("void", choixTypesRetour);

            dialogTypeRetour.setTitle("Тип повернення методу");
            dialogTypeRetour.setHeaderText("Виберіть тип повернення методу:");

            Optional<String> resultatTypeRetour = dialogTypeRetour.showAndWait();

            TextInputDialog dialogNomMethode = new TextInputDialog();
            dialogNomMethode.setTitle("Назва методу");
            dialogNomMethode.setHeaderText("Введіть назву методу:");
            dialogNomMethode.setContentText("Назва:");

            Optional<String> resultatNomMethode = dialogNomMethode.showAndWait();

            if (resultatTypeRetour.isPresent() && resultatNomMethode.isPresent()) {
                String typeRetour = resultatTypeRetour.get();
                String nomMethode = resultatNomMethode.get();
                int modificateur = switch (visibilite) {
                    case "public" -> Modifier.PUBLIC;
                    case "protected" -> Modifier.PROTECTED;
                    default -> Modifier.PRIVATE;
                };

                classUML.addMethode(nomMethode, typeRetour, modificateur);
                Position p = getPositionClasse(classUML.getName());
                p.minHeight+=22;
                notifyObservers();
            }
        }
    }

    // Експортує модель за допомогою стратегії експорту
    public void export(ExportStrategy export){
        export.export(this);
    }

    // Скидає модель до початкового стану
    public void reset(){
        this.classPositions = new HashMap<>();
        this.relationUMLS = new ArrayList<>();
        notifyObservers();
    }

    // Змінює видимість класу
    public void setClassVisibility(ClassUML classUML, boolean visible) {
        visibiliteClasses.put(classUML, visible);
        notifyObservers();
    }

    // Отримує видимість класу
    public Boolean getClassVisibility(ClassUML classUML) {
        return visibiliteClasses.getOrDefault(classUML, true);
    }

    // Змінює видимість зв’язку
    public void setRelationVisibility(RelationUML relationUML, boolean visible) {
        visibiliteRelations.put(relationUML, visible);
        notifyObservers();
    }

    // Отримує видимість зв’язку
    public Boolean getRelationVisibility(RelationUML relationUML) {
        return visibiliteRelations.getOrDefault(relationUML, true);
    }

    // Змінює видимість усіх класів і зв’язків
    public void toggleVisibilityAll(boolean visible) {
        for (ClassUML classUML : classPositions.keySet()) {
            setClassVisibility(classUML, visible);
        }
        for (RelationUML relationUML : relationUMLS) {
            setRelationVisibility(relationUML, visible);
        }
        notifyObservers();
    }

    // Змінює видимість усіх зв’язків
    public void toggleVisibilityRelation(boolean visible) {
        for (RelationUML relationUML : relationUMLS) {
            ClassUML sourceClass = findClassByName(relationUML.getSource());
            ClassUML targetClass = findClassByName(relationUML.getTarget());

            // il faut que les deux classes soient visibles pour que la relation soit visible
            if (sourceClass != null && targetClass != null) {
                boolean sourceVisibility = getClassVisibility(sourceClass);
                boolean targetVisibility = getClassVisibility(targetClass);

                if ((sourceVisibility && targetVisibility && visible) || !visible) {
                    setRelationVisibility(relationUML, visible);
                }
            }
        }
        notifyObservers();
    }

    // Знаходить клас за його назвою
    private ClassUML findClassByName(String name) {
        for (ClassUML classUML : classPositions.keySet()) {
            if (classUML.getName().equals(name)) {
                return classUML;
            }
        }
        return null;
    }

    // Зберігає класи у файли Java
    public void saveFichierJava(File directory) {
        for (ClassUML classUML : classPositions.keySet()) {
            File file = new File(directory, classUML.getName() + ".java");
            try (PrintWriter out = new PrintWriter(new FileWriter(file))) {
                out.println("package " + (classUML.getPackage() != null ? classUML.getPackage() : "default_package") + ";");
                out.println();
                // class declaration
                String classDeclaration = "class";
                if (classUML.getType().equals("interface")) {
                    classDeclaration = "interface";
                } else if (classUML.getType().equals("abstract")) {
                    classDeclaration = "abstract class";
                }

                // extends and implements
                StringBuilder extendsImplements = new StringBuilder();
                for (RelationUML relationUML : classUML.getRelations()) {
                    if (relationUML.getType().equals(RelationUML.EXTEND)) {
                        extendsImplements.append(" extends ").append(relationUML.getTarget());
                    } else if (relationUML.getType().equals(RelationUML.IMPLEMENT)) {
                        if (extendsImplements.toString().contains("implements")) {
                            extendsImplements.append(", ").append(relationUML.getTarget());
                        } else {
                            extendsImplements.append(" implements ").append(relationUML.getTarget());
                        }
                    }
                }

                out.println("public " + classDeclaration + " " + classUML.getName() + extendsImplements.toString() + " {");
                out.println();

                // generation des attributs
                for (AttributeUML attributeModel : classUML.getAttributs()) {
                    String modifier = visibilityToString(attributeModel.getPrivacy());
                    out.println("    " + modifier + " " + attributeModel.getType() + " " + attributeModel.getName() + ";");
                }
                out.println();

                // generation des methodes
                for (MethodeUML methodeUML : classUML.getMethodes()) {
                    String modifier = visibilityToString(methodeUML.getPrivacy());
                    out.print("    " + modifier + " " + methodeUML.getTypeReturn() + " " + methodeUML.getName() + "(");
                    StringJoiner joiner = new StringJoiner(", ");
                    for (AttributeUML param : methodeUML.getParameters()) {
                        joiner.add(param.getType() + " " + param.getName());
                    }
                    out.print(joiner.toString());
                    out.println(") {");
                    out.println("        // TODO: Implement the method");
                    out.println("    }");
                    out.println();
                }
                out.println("}");
                out.println();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error saving file: " + file.getAbsolutePath());
            }
        }
    }

    // Перетворює символ видимості у рядок
    private String visibilityToString(String visibilitySymbol) {
        return switch (visibilitySymbol) {
            case "+" -> "public";
            case "#" -> "protected";
            default -> "private";
        };
    }

    @Override
    public void registerObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for(Observer observer : observers) {
            observer.update(this);
        }
    }

    // Отримує список класів
    public List<ClassUML> getClassPositions() {
        return new ArrayList<>(classPositions.keySet());
    }

    // Отримує позиції класів
    public Map<ClassUML, Position> getClassesPosition(){
        return classPositions;
    }

    // Отримує позицію класу за його назвою
    public Position getPositionClasse(String c){
        for(Map.Entry<ClassUML, Position> entry : classPositions.entrySet()) {
            if(entry.getKey().getName().equals(c)) {
                return entry.getValue();
            }
        }
        return null;
    }


    // Отримує список зв’язків
    public List<RelationUML> getRelations() {
        return relationUMLS;
    }

    // Отримує директорію
    public String getDirectory() {
        return directory;
    }

    // Встановлює директорію
    public void setDirectory(String directory) {
        if(new File(directory).isDirectory()) {
            this.directory = directory;
            ClassUML.setRepertoire(this.directory);
        }else
            System.out.println("Error: The provided directory does not exist");
        notifyObservers();
    }

    // Встановлює розмір класу
    public void setSizeClass(ClassUML c, double w, double h){
        for (ClassUML cl : getClassPositions()){
            if(cl.equals(c)){
                Position p = this.classPositions.get(cl);
                p.width = w;
                p.height = h;
                break;
            }
        }
        notifyObservers();
    }

    // Встановлює позицію класу
    public void setPosClasse(ClassUML c, double x, double y){
        for (ClassUML cl : getClassPositions()){
            if(cl.equals(c)){
                Position p = this.classPositions.get(cl);
                p.x = x;
                p.y = y;
                break;
            }
        }
        notifyObservers();
    }

    // Отримує зв’язки для класу
    public List<RelationUML> getRelation(ClassUML c) {
        ArrayList<RelationUML> r = new ArrayList<>();
        for(RelationUML re : relationUMLS){
            if (re.getSource().equals(c.getName()) || re.getTarget().equals(c.getName())){
                r.add(re);
            }
        }
        return r;
    }

    // Видаляє зв’язок
    public void removeRelation(String s) {
        relationUMLS.removeIf(r -> r.toString().equals(s));
    }

    // Внутрішній клас для зберігання координат і розмірів
    public static class Position implements Serializable{
        private double x;
        private double y;
        private double width;
        private double height;
        private double minHeight;

        public Position(double a, double b) {
            this.x = a;
            this.y = b;
            this.width = 0;
            this.height = 0;
            this.minHeight = 0;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public double getWidth() {
            return width;
        }

        public double getHeight() {
            return height;
        }

        public double getMinHeight() {
            return minHeight;
        }

        public void setWidth(double width) {
            this.width = width;
        }

        public void setHeight(double height) {
            this.height = height;
        }

        public void setMinHeight(double minHeight) {
            this.minHeight = minHeight;
        }
    }
}