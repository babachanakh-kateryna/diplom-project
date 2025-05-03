package org.classcraft.Composite;

import java.io.Serializable;
import java.lang.reflect.Modifier;

/**
 * Клас, що представляє атрибут класу.
 * Використовується для зберігання інформації про атрибути класу, такі як ім'я, тип та модифікатор доступу.
 */
public class AttributeModel implements Serializable {
    private String name; // Назва атрибуту
    private String type; // Тип атрибуту
    private String visibility; // Модифікатор доступу: + публічний, # protected, - приватний

    public AttributeModel(String name, String type, int visibility){
        this.name = name;
        this.type = type;
        if (Modifier.isPublic(visibility)) {
            this.visibility = "+";
        } else if (Modifier.isProtected(visibility)) {
            this.visibility = "#";
        } else {
            this.visibility = "-";
        }
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getVisibility() {
        return visibility;
    }

    @Override
    public String toString() {
        return "    " + this.visibility + " " + this.name + ": " + this.type;
    }
}

