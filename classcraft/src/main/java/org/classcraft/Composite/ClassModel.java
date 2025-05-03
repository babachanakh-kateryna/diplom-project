package org.classcraft.Composite;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;


public class ClassModel extends FileComposite implements Serializable {

    private String name; // Назва класу
    private String type; // Тип (class / abstract / interface)
    private List<AttributeModel> attributes; // Список атрибутів класу
    private List<MethodModel> methodes; // Список методів класу
    private List<RelationModel> relations = new ArrayList<>(); // Зв'язки з іншими класами
    private String packageName; // Назва пакету

    public static String INTELLIJ_DIRECTORY = "target/classes"; // Стандартний каталог збірки

    public ClassModel(File file) {
        this.f = file;
        this.performIntrospection();
    }

    // Конструктор для створення порожнього класу
    public ClassModel(String name) {
        this.name = name;
        this.attributes = new ArrayList<>();
        this.methodes = new ArrayList<>();
        this.relations = new ArrayList<>();
    }

    // Інспекція класу: отримання атрибутів, методів, зв'язків
    public void performIntrospection() {
        if (f != null) {
            this.name = f.getName().substring(0, f.getName().length() - 6);
            String className = null;
            try {
                File dir = new File(INTELLIJ_DIRECTORY);
                URL url = dir.toURI().toURL();
                URLClassLoader classLoader = new URLClassLoader(new URL[]{url});

                className = f.getAbsolutePath()
                        .replace(dir.getAbsolutePath() + File.separator, "")
                        .replace(File.separator, ".")
                        .replace(".class", "");

                Class<?> clazz = classLoader.loadClass(className);

                // Визначення типу класу
                if (clazz.isInterface()) {
                    this.type = "interface";
                } else if (Modifier.isAbstract(clazz.getModifiers())) {
                    this.type = "abstract";
                } else {
                    this.type = "class";
                }

                // Отримання інформації про клас
                this.attributes = Introspection.exploreFields(clazz);
                this.methodes = Introspection.exploreMethods(clazz);
                this.relations = Introspection.exploreRelations(clazz);

                int lastDotIndex = className.lastIndexOf('.');
                if (lastDotIndex != -1) {
                    this.packageName = className.substring(0, lastDotIndex);
                }

            } catch (ClassNotFoundException e) {
                System.out.println("Клас не знайдено (ймовірно неправильний шлях): " + className);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // Вивід класу з відступом
    @Override
    public void display(String indent) {
        String content = "";
        for (RelationModel r : this.relations) {
            content += "\n" + indent + r.toString();
        }
        for (AttributeModel a : this.attributes) {
            content += "\n" + indent + a.toString();
        }
        for (MethodModel m : this.methodes) {
            content += "\n" + indent + m.toString();
        }
        System.out.println(indent + "|> " + this.type + " " + this.getName() + content);
    }

    @Override
    public String toString() {
        String content = "";
        for (AttributeModel a : this.attributes) {
            content += "\n  " + a.toString();
        }
        for (MethodModel m : this.methodes) {
            content += "\n  " + m.toString();
        }
        return this.type + " " + this.getName() + " {" + content + "\n}";
    }

    // Геттери / сеттери
    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<RelationModel> getRelations() {
        return relations;
    }

    public List<AttributeModel> getAttributes() {
        return attributes;
    }

    public List<MethodModel> getMethods() {
        return methodes;
    }

    public void addRelation(RelationModel relation) {
        this.relations.add(relation);
    }

    public static void setDirectory(String directory) {
        INTELLIJ_DIRECTORY = directory;
    }

    public void addAttribute(String name, String type, int visibility) {
        this.attributes.add(new AttributeModel(name, type, visibility));
    }

    public void addMethod(String name, String type, int visibility) {
        this.methodes.add(new MethodModel(name, type, visibility, new ArrayList<>()));
    }

    public void removeRelation(String s) {
        this.relations.removeIf(r -> r.toString().equals(s));
    }

    public String getPackage() {
        return packageName;
    }

    public void setPackage(String packageName) {
        this.packageName = packageName;
    }
}