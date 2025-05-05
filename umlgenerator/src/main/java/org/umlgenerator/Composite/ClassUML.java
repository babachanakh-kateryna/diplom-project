package org.umlgenerator.Composite;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class ClassUML extends FileComposite implements Serializable {

    private String name; // Назва класу
    private String type; // Тип (class / abstract / interface)
    private List<AttributeUML> attributeModels; // Список атрибутів класу
    private List<MethodeUML> methodeUMLS; // Список методів класу
    private List<RelationUML> relationUMLS = new ArrayList<>(); // Зв'язки з іншими класами
    private String packageName; // Назва пакету

    public static String REPERTOIRE_INTELLIJ = "target/classes"; // Стандартний каталог збірки

    public ClassUML(File fic) {
        this.f = fic;
        this.doIntrospection();
    }

    // Конструктор для створення порожнього класу
    public ClassUML(String name) {
        this.name = name;
        this.attributeModels = new ArrayList<>();
        this.methodeUMLS = new ArrayList<>();
        this.relationUMLS = new ArrayList<>();
    }

    // Інспекція класу: отримання атрибутів, методів, зв'язків
    public void doIntrospection(){
        if (f != null) {
            this.name = f.getName().substring(0, f.getName().length() - 6);
            String className = null;
            try {
                // Створюємо URL-адресу з кореневої папки, що містить класи та пакети
                File dir = new File(REPERTOIRE_INTELLIJ);
                URL url = dir.toURI().toURL();

                // Використовуємо URLClassLoader для завантаження класів з URL
                URLClassLoader classLoader = new URLClassLoader(new URL[]{url});

                // Отримати назву класу зі шляху до файлу
                className = f.getAbsolutePath()
                        .replace(dir.getAbsolutePath() + File.separator, "")   // Видалити базовий шлях
                        .replace(File.separator, ".")             // Замінити роздільники крапками
                        .replace(".class", "");

                // Динамічне завантаження класу
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
                this.attributeModels = Introspection.exploreFields(clazz);
                this.methodeUMLS = Introspection.exploreMethods(clazz);
                this.relationUMLS = Introspection.exploreRelations(clazz);

                int lastDotIndex = className.lastIndexOf('.');
                if (lastDotIndex != -1) {
                    this.packageName = className.substring(0, lastDotIndex);
                }
            } catch (ClassNotFoundException e) {
                System.out.println("Class does not exist (path error so far) : " + className);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // Вивід класу з відступом
    @Override
    public void display(String s) {
        String contenu = "";
        for(RelationUML r : this.relationUMLS){
            contenu+="\n"+s+r.toString();
        }
        for(AttributeUML a : this.attributeModels){
            contenu+="\n"+s+a.toString();
        }
        for(MethodeUML m : this.methodeUMLS){
            contenu+="\n"+s+m.toString();
        }
        System.out.println(s+"|> "+this.type+" "+this.getName()+contenu);
    }

    @Override
    public String toString() {
        String contenu = "";
        for(AttributeUML a : this.attributeModels){
            contenu+="\n  "+a.toString();
        }
        for(MethodeUML m : this.methodeUMLS){
            contenu+="\n  "+m.toString();
        }
        return this.type+" "+this.getName()+" {"+contenu+"\n}";
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

    public List<RelationUML> getRelations() {
        return relationUMLS;
    }

    public List<AttributeUML> getAttributs() {
        return attributeModels;
    }

    public List<MethodeUML> getMethodes() {
        return methodeUMLS;
    }

    public void addRelation(RelationUML relationUML) {
        this.relationUMLS.add(relationUML);
    }

    public static void setRepertoire(String r){
        REPERTOIRE_INTELLIJ = r;
    }

    public void addAttribut(String nom, String type, int privacy) {
        this.attributeModels.add(new AttributeUML(nom, type, privacy));
    }

    public void addMethode(String nom, String type, int privacy) {
        this.methodeUMLS.add(new MethodeUML(nom, type, privacy, new ArrayList<>()));
    }

    public void removeRelation(String s) {
        for(RelationUML r : relationUMLS) {
            if(r.toString().equals(s)) {
                relationUMLS.remove(r);
            }
        }
    }

    public String getPackage() {
        return packageName;
    }

    public void setPackage(String packageName) {
        this.packageName = packageName;
    }

}