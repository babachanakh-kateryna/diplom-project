package org.classcraft.Model;

import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextInputDialog;
import org.classcraft.Composite.AttributeModel;
import org.classcraft.Composite.ClassModel;
import org.classcraft.Composite.MethodModel;
import org.classcraft.Composite.RelationModel;
import org.classcraft.Export.ExportStrategy;
import org.classcraft.View.Observer;

import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.util.*;
import java.io.*;

public class DiagramModel implements Subject{
    private String directory; // Робоча директорія для сканування .class файлів
    private List<Observer> observers; // Список підписаних спостерігачів
    private Map<ClassModel, Position> classPositions; // Положення кожного класу
    private List<RelationModel> relations; // Список усіх зв’язків
    private Map<ClassModel, Boolean> classVisibility; // Видимість класів
    private Map<RelationModel, Boolean> relationVisibility; // Видимість зв’язків

    // Конструктор, що ініціалізує модель діаграми
    public DiagramModel(String directory) {
        if(new File(directory).isDirectory()) {
            this.directory = directory;
            ClassModel.setDirectory(this.directory);
        } else {
            throw new IllegalArgumentException("Provided directory does not exist");
        }
        observers = new ArrayList<>();
        classPositions = new HashMap<>();
        relations = new ArrayList<>();
        classVisibility = new HashMap<>();
        relationVisibility = new HashMap<>();
    }

    // Імпортує модель із заданими класами та зв’язками
    public void importModel(Map<ClassModel, Position> importedClasses, List<RelationModel> importedRelations) {
        this.classPositions = importedClasses;
        this.relations = importedRelations;
        notifyObservers();
    }

    // Додає клас до діаграми
    public void addClass(ClassModel clazz, double x, double y) {
        if (classPositions.containsKey(clazz)) return;
        classPositions.put(clazz, (x + y != 0) ? new Position(x, y) : findFreePosition());

        // Переглядає зв’язки класу для додавання
        for (RelationModel r : clazz.getRelations()) {
            // Перевірка наявності вихідного та цільового класів зв'язку на діаграмі
            if (isClassPresent(r.getSource()) && isClassPresent(r.getTarget()) && !relations.contains(r)) {
                relations.add(r);
            }
        }

        //  інші класи, що вже присутні, щоб перевірити та додати зв'язки, де новий клас є цільовим
        for (ClassModel c : classPositions.keySet()) {
            if (!c.equals(clazz)) {
                for (RelationModel r : c.getRelations()) {
                    // Перевірка наявності вихідного та цільового класів зв'язку на діаграмі
                    if (r.getTarget().equals(clazz.getName()) && isClassPresent(r.getSource()) && !relations.contains(r)) {
                        relations.add(r);
                    }
                }
            }
        }
        notifyObservers();
    }

    // Перевіряє, чи присутній клас у діаграмі
    private boolean isClassPresent(String className) {
        return classPositions.keySet().stream().anyMatch(c -> c.getName().equals(className));
    }

    // Обчислює вільну позицію для нового класу
    private Position findFreePosition() {
        double defaultWidth = 100, defaultHeight = 80, spacing = 20;
        double maxWidth = ApplicationState.getCurrentWindowWidth();
        double x = spacing, y = spacing;
        boolean found;

        do {
            found = true;
            for (Position pos : classPositions.values()) {
                if (x + defaultWidth > pos.getX() && x < pos.getX() + pos.getWidth() &&
                        y + defaultHeight > pos.getY() && y < pos.getY() + pos.getHeight()) {
                    found = false; break;
                }
            }
            if (!found) {
                x += defaultWidth + spacing;
                if (x + defaultWidth + spacing > maxWidth) {
                    x = spacing;
                    y += defaultHeight + spacing;
                }
            }
        } while (!found);

        return new Position(x, y);
    }

    // Видаляє клас із діаграми
    public void removeClass(ClassModel clazz) {
        relations.removeIf(r -> r.getSource().equals(clazz.getName()) || r.getTarget().equals(clazz.getName()));
        classPositions.remove(clazz);
        notifyObservers();
    }

    // Додає зв’язок
    public void addRelation(RelationModel relation) {
        for (ClassModel clazz : classPositions.keySet()) {
            if (relation.getSource().equals(clazz.getName())) {
                relations.add(relation);
                clazz.addRelation(relation);
            }
        }
        notifyObservers();
    }

    // Додає атрибут до класу
    public void addAttribute(ClassModel clazz) {
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

            TextInputDialog nameDialog = new TextInputDialog();
            nameDialog.setTitle("Назва атрибута");
            nameDialog.setHeaderText("Введіть назву атрибута:");
            nameDialog.setContentText("Назва:");

            Optional<String> nameResult = nameDialog.showAndWait();

            // Якщо користувач вибрав тип та ім'я
            if (typeResult.isPresent() && nameResult.isPresent()) {
                String type = typeResult.get();
                String name = nameResult.get();
                int privacy = switch (visibility) {
                    case "public" -> Modifier.PUBLIC;
                    case "protected" -> Modifier.PROTECTED;
                    default -> Modifier.PRIVATE;
                };

                clazz.addAttribute(name, type, privacy);
                Position position = getClassPosition(clazz.getName());
                position.minHeight += 22;
                notifyObservers();
            }
        }
    }

    // Додає метод до класу
    public void addMethod(ClassModel clazz) {
        // Список варіантів видимості
        List<String> visibilityChoices = new ArrayList<>();
        visibilityChoices.add("public");
        visibilityChoices.add("protected");
        visibilityChoices.add("private");

        ChoiceDialog<String> visibilityDialog = new ChoiceDialog<>("public", visibilityChoices);
        visibilityDialog.setTitle("Додавання методу");
        visibilityDialog.setHeaderText("Виберіть видимість:");

        Optional<String> visibilityResult = visibilityDialog.showAndWait();

        if (visibilityResult.isPresent()) {
            String visibility = visibilityResult.get();

            // Список варіантів типу повернення
            List<String> returnTypeChoices = List.of("void", "int", "double", "String", "boolean");
            ChoiceDialog<String> returnTypeDialog = new ChoiceDialog<>("void", returnTypeChoices);
            returnTypeDialog.setTitle("Тип повернення методу");
            returnTypeDialog.setHeaderText("Виберіть тип повернення методу:");

            Optional<String> returnTypeResult = returnTypeDialog.showAndWait();

            TextInputDialog methodNameDialog = new TextInputDialog();
            methodNameDialog.setTitle("Назва методу");
            methodNameDialog.setHeaderText("Введіть назву методу:");
            methodNameDialog.setContentText("Назва:");

            Optional<String> methodNameResult = methodNameDialog.showAndWait();

            if (returnTypeResult.isPresent() && methodNameResult.isPresent()) {
                String returnType = returnTypeResult.get();
                String methodName = methodNameResult.get();
                int modifier = switch (visibility) {
                    case "public" -> Modifier.PUBLIC;
                    case "protected" -> Modifier.PROTECTED;
                    default -> Modifier.PRIVATE;
                };

                clazz.addMethod(methodName, returnType, modifier);
                Position position = getClassPosition(clazz.getName());
                position.minHeight += 22;
                notifyObservers();
            }
        }
    }

    // Експортує модель за допомогою стратегії експорту
    public void export(ExportStrategy exportStrategy) {
        exportStrategy.export(this);
    }

    // Скидає модель до початкового стану
    public void reset() {
        this.classPositions = new HashMap<>();
        this.relations = new ArrayList<>();
        notifyObservers();
    }

    // Змінює видимість класу
    public void setClassVisibility(ClassModel clazz, boolean visible) {
        classVisibility.put(clazz, visible);
        notifyObservers();
    }

    // Отримує видимість класу
    public Boolean getClassVisibility(ClassModel clazz) {
        return classVisibility.getOrDefault(clazz, true);
    }

    // Змінює видимість зв’язку
    public void setRelationVisibility(RelationModel relation, boolean visible) {
        relationVisibility.put(relation, visible);
        notifyObservers();
    }

    // Отримує видимість зв’язку
    public Boolean getRelationVisibility(RelationModel relation) {
        return relationVisibility.getOrDefault(relation, true);
    }

    // Змінює видимість усіх класів і зв’язків
    public void toggleAllVisibility(boolean visible) {
        for (ClassModel clazz : classPositions.keySet()) {
            setClassVisibility(clazz, visible);
        }
        for (RelationModel relation : relations) {
            setRelationVisibility(relation, visible);
        }
        notifyObservers();
    }

    // Змінює видимість усіх зв’язків
    public void toggleRelationsVisibility(boolean visible) {
        for (RelationModel relation : relations) {
            ClassModel sourceClass = findClassByName(relation.getSource());
            ClassModel targetClass = findClassByName(relation.getTarget());

            if (sourceClass != null && targetClass != null) {
                boolean sourceVisibility = getClassVisibility(sourceClass);
                boolean targetVisibility = getClassVisibility(targetClass);

                if ((sourceVisibility && targetVisibility && visible) || !visible) {
                    setRelationVisibility(relation, visible);
                }
            }
        }
        notifyObservers();
    }

    // Знаходить клас за його назвою
    private ClassModel findClassByName(String name) {
        for (ClassModel clazz : classPositions.keySet()) {
            if (clazz.getName().equals(name)) {
                return clazz;
            }
        }
        return null;
    }

    // Зберігає класи у файли Java
    public void saveJavaFiles(File directory) {
        for (ClassModel clazz : classPositions.keySet()) {
            File file = new File(directory, clazz.getName() + ".java");
            try (PrintWriter out = new PrintWriter(new FileWriter(file))) {
                out.println("package " + (clazz.getPackage() != null ? clazz.getPackage() : "default_package") + ";");
                out.println();
                String classDeclaration = "class";
                if (clazz.getType().equals("interface")) {
                    classDeclaration = "interface";
                } else if (clazz.getType().equals("abstract")) {
                    classDeclaration = "abstract class";
                }

                StringBuilder extendsImplements = new StringBuilder();
                for (RelationModel relation : clazz.getRelations()) {
                    if (relation.getType().equals(RelationModel.EXTEND)) {
                        extendsImplements.append(" extends ").append(relation.getTarget());
                    } else if (relation.getType().equals(RelationModel.IMPLEMENT)) {
                        if (extendsImplements.toString().contains("implements")) {
                            extendsImplements.append(", ").append(relation.getTarget());
                        } else {
                            extendsImplements.append(" implements ").append(relation.getTarget());
                        }
                    }
                }

                out.println("public " + classDeclaration + " " + clazz.getName() + extendsImplements.toString() + " {");
                out.println();

                for (AttributeModel attribute : clazz.getAttributes()) {
                    String modifier = visibilityToString(attribute.getVisibility());
                    out.println("    " + modifier + " " + attribute.getType() + " " + attribute.getName() + ";");
                }
                out.println();

                for (MethodModel method : clazz.getMethods()) {
                    String modifier = visibilityToString(method.getVisibility());
                    out.print("    " + modifier + " " + method.getReturnType() + " " + method.getName() + "(");
                    StringJoiner joiner = new StringJoiner(", ");
                    for (AttributeModel param : method.getParameters()) {
                        joiner.add(param.getType() + " " + param.getName());
                    }
                    out.print(joiner.toString());
                    out.println(") {");
                    out.println("        // TODO: Реалізувати метод");
                    out.println("    }");
                    out.println();
                }
                out.println("}");
                out.println();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Помилка під час збереження файлу " + file.getAbsolutePath());
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
        for (Observer observer : observers) {
            observer.update(this);
        }
    }

    // Отримує список класів
    public List<ClassModel> getClasses() {
        return new ArrayList<>(classPositions.keySet());
    }

    // Отримує позиції класів
    public Map<ClassModel, Position> getClassPositions() {
        return classPositions;
    }

    // Отримує позицію класу за його назвою
    public Position getClassPosition(String className) {
        for (Map.Entry<ClassModel, Position> entry : classPositions.entrySet()) {
            if (entry.getKey().getName().equals(className)) {
                return entry.getValue();
            }
        }
        return null;
    }

    // Отримує список зв’язків
    public List<RelationModel> getRelations() {
        return relations;
    }

    // Отримує директорію
    public String getDirectory() {
        return directory;
    }

    // Встановлює директорію
    public void setDirectory(String directory) {
        if (new File(directory).isDirectory()) {
            this.directory = directory;
            ClassModel.setDirectory(this.directory);
        } else {
            System.out.println("Помилка з директорією");
        }
        notifyObservers();
    }

    // Встановлює розмір класу
    public void setClassSize(ClassModel clazz, double width, double height) {
        for (ClassModel existingClass : getClasses()) {
            if (existingClass.equals(clazz)) {
                Position position = this.classPositions.get(existingClass);
                position.width = width;
                position.height = height;
                break;
            }
        }
        notifyObservers();
    }

    // Встановлює позицію класу
    public void setClassPosition(ClassModel clazz, double x, double y) {
        for (ClassModel existingClass : getClasses()) {
            if (existingClass.equals(clazz)) {
                Position position = this.classPositions.get(existingClass);
                position.x = x;
                position.y = y;
                break;
            }
        }
        notifyObservers();
    }

    // Отримує зв’язки для класу
    public List<RelationModel> getRelationsForClass(ClassModel clazz) {
        ArrayList<RelationModel> relationsForClass = new ArrayList<>();
        for (RelationModel relation : relations) {
            if (relation.getSource().equals(clazz.getName()) || relation.getTarget().equals(clazz.getName())) {
                relationsForClass.add(relation);
            }
        }
        return relationsForClass;
    }

    // Видаляє зв’язок
    public void removeRelation(String relationString) {
        relations.removeIf(r -> r.toString().equals(relationString));
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
