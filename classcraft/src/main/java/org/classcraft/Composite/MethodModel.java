package org.classcraft.Composite;

import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.util.List;

/**
 * Клас, що представляє методи класу
 */
public class MethodModel implements Serializable {
    private String name; // Назва методу
    private String returnType; // Тип, що повертається
    private String visibility; // Модифікатор доступу
    private List<AttributeModel> parameters; // Список параметрів

    public MethodModel(String name, String returnType, int visibility, List<AttributeModel> parameters){
        this.name = name;
        this.returnType = returnType;
        if (Modifier.isPublic(visibility)) {
            this.visibility = "+";
        } else if (Modifier.isProtected(visibility)) {
            this.visibility = "#";
        } else {
            this.visibility = "-";
        }
        this.parameters = parameters;
    }

    @Override
    public String toString() {
        StringBuilder strParams = new StringBuilder();
        for(int i = 0; i < this.parameters.size(); i++){
            AttributeModel a = this.parameters.get(i);
            if(i == 0) {
                strParams.append(a.getType());
            } else {
                strParams.append(", ").append(a.getType());
            }
        }
        return "    " + this.visibility + " " + this.name + "(" + strParams + "): " + this.returnType;
    }

    public void addParameter(AttributeModel attribute){
        this.parameters.add(attribute);
    }

    public void removeParameter(AttributeModel p) {
        if(p != null)
            this.parameters.remove(p);
    }

    public List<AttributeModel> getParameters() {
        return parameters;
    }

    public String getReturnType() {
        return returnType;
    }

    public String getName() {
        return name;
    }

    public String getVisibility() {
        return visibility;
    }
}
