package org.umlgenerator.Composite;

import java.io.Serializable;
import java.lang.reflect.Modifier;

/**
 * Клас, що представляє атрибут класу.
 * Використовується для зберігання інформації про атрибути класу, такі як ім'я, тип та модифікатор доступу.
 */
public class AttributeUML implements Serializable {
    private String name; // Назва атрибуту
    private String type; // Тип атрибуту
    private String privacy; // Модифікатор доступу: + публічний, # protected, - приватний

    public AttributeUML(String name, String type, int privacy){
        this.name =name;
        this.type=type;
        if (Modifier.isPublic(privacy)) {
            this.privacy="+";
        }else if (Modifier.isProtected(privacy)) {
            this.privacy="#";
        }else{
            this.privacy="-";
        }
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getPrivacy() {
        return privacy;
    }

    @Override
    public String toString() {
        return "    "+this.privacy+" "+this.name +": "+this.type;
    }
}
