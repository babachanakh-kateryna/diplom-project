package org.umlgenerator.Composite;

import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.util.List;

/**
 * Клас, що представляє методи класу
 */
public class MethodeUML implements Serializable {
    private String name; // Назва методу
    private String typeReturn; // Тип, що повертається
    private String privacy; // Модифікатор доступу
    private List<AttributeUML> parameters; // Список параметрів

    public MethodeUML(String name, String type, int privacy, List<AttributeUML> list){
        this.name = name;
        this.typeReturn =type;
        if (Modifier.isPublic(privacy)) {
            this.privacy="+";
        }else if (Modifier.isProtected(privacy)) {
            this.privacy="#";
        }else{
            this.privacy="-";
        }
        this.parameters = list;
    }

    @Override
    public String toString() {
        StringBuilder strParams = new StringBuilder();
        for(int i = 0; i<this.parameters.size(); i++){
            AttributeUML a = this.parameters.get(i);
            if(i==0) {
                strParams.append(a.getType());
            }else{
                strParams.append(",").append(a.getType());
            }
        }
        return "    "+this.privacy+" "+this.name +"("+strParams+"): "+this.typeReturn;
    }

    public void addParameter(AttributeUML attributeModel){
        this.parameters.add(attributeModel);
    }

    public List<AttributeUML> getParameters() {
        return parameters;
    }

    public String getTypeReturn() {
        return typeReturn;
    }

    public String getName() {
        return name;
    }

    public String getPrivacy() {
        return privacy;
    }

    public void removeParameter(AttributeUML p) {
        if(p != null)
            this.parameters.remove(p);
    }
}
