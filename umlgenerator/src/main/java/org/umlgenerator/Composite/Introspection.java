package org.umlgenerator.Composite;


import java.lang.reflect.*;
import java.util.*;

public class Introspection
{
    // Отримати всі поля класу
    public static List<AttributeUML> exploreFields(Class<?> c)
    {
        Field[] f = c.getDeclaredFields();
        List<AttributeUML> listAt = new ArrayList<>();
        for (Field field : f) {
            String typeName = field.getType().getSimpleName();

            // Якщо поле є параметризованою колекцією (наприклад, List<String>)
            if (field.getGenericType() instanceof ParameterizedType paramType) {
                StringBuilder typeBuilder = new StringBuilder(field.getType().getSimpleName());
                typeBuilder.append("<");

                // Отримання параметризованих типів
                Type[] typeArguments = paramType.getActualTypeArguments();
                for (int i = 0; i < typeArguments.length; i++) {
                    // Додати просту назву узагальненого типу
                    typeBuilder.append(((Class<?>) typeArguments[i]).getSimpleName());
                    if (i < typeArguments.length - 1) {
                        typeBuilder.append(", ");
                    }
                }
                typeBuilder.append(">");
                typeName = typeBuilder.toString();
            }

            listAt.add(new AttributeUML(field.getName(), typeName, field.getModifiers()));
        }
        return listAt;
    }

    // Отримати всі методи класу
    public static List<MethodeUML> exploreMethods(Class<?> c)
    {
        Method[] m = c.getDeclaredMethods();
        // сортувати методи в алфавітному порядку
        Arrays.sort(m, Comparator.comparing(Method::getName));
        List<MethodeUML> listM = new ArrayList<>();

        for (Method method : m) {
            List<AttributeUML> listAt = new ArrayList<>();

            Type[] parameterTypes = method.getGenericParameterTypes();
            Parameter[] parameters = method.getParameters();

            for (int i = 0; i < parameterTypes.length; i++) {
                String paramTypeName;

                // Керування загальними типами параметрів
                if (parameterTypes[i] instanceof ParameterizedType) {
                    paramTypeName = getParameterizedTypeName((ParameterizedType) parameterTypes[i]);
                } else if (parameterTypes[i] instanceof Class) {
                    paramTypeName = ((Class<?>) parameterTypes[i]).getSimpleName();
                } else {
                    paramTypeName = parameterTypes[i].getTypeName();
                }

                listAt.add(new AttributeUML(parameters[i].getName(), paramTypeName, parameters[i].getModifiers()));
            }

            // Обробка загального типу повернення
            String returnTypeName;
            Type returnType = method.getGenericReturnType();

            if (returnType instanceof ParameterizedType) {
                returnTypeName = getParameterizedTypeName((ParameterizedType) returnType);
            } else if (returnType instanceof Class) {
                returnTypeName = ((Class<?>) returnType).getSimpleName();
            } else {
                returnTypeName = returnType.getTypeName(); // Резервний варіант
            }

            // Створення об'єкта Method
            MethodeUML methodeUML = new MethodeUML(method.getName(), returnTypeName, method.getModifiers(), listAt);
            listM.add(methodeUML);
        }
        return listM;
    }

    // Повертає назву параметризованого типу, наприклад: List<String>
    private static String getParameterizedTypeName(ParameterizedType type) {
        StringBuilder typeName = new StringBuilder(((Class<?>) type.getRawType()).getSimpleName());
        typeName.append("<");

        Type[] typeArguments = type.getActualTypeArguments();
        for (int i = 0; i < typeArguments.length; i++) {
            if (typeArguments[i] instanceof Class) {
                typeName.append(((Class<?>) typeArguments[i]).getSimpleName());
            } else {
                typeName.append(typeArguments[i].getTypeName());
            }
            if (i < typeArguments.length - 1) {
                typeName.append(", ");
            }
        }

        typeName.append(">");
        return typeName.toString();
    }

    // Отримати зв’язки класу: спадкування, реалізація, використання
    public static List<RelationUML> exploreRelations(Class<?> c){

        List<RelationUML> list = new ArrayList<>();

        // Спадкування
        Class<?> superclass = c.getSuperclass();
        if(superclass!=null && !superclass.getName().startsWith("java.")) {
            RelationUML r = new RelationUML(RelationUML.EXTEND, c.getSimpleName(), superclass.getSimpleName());
            list.add(r);
        }

        // Інтерфейси
        Class<?>[] interfaces = c.getInterfaces();
        for (Class<?> iface : interfaces) {
            RelationUML r = new RelationUML(RelationUML.IMPLEMENT, c.getSimpleName(), iface.getSimpleName());
            list.add(r);
        }

        // Зв’язки через поля
        Field[] fields = c.getDeclaredFields();

        for (Field field : fields) {
            Class<?> fieldType = field.getType();

            // Перевірте, чи це колекція
            if (Collection.class.isAssignableFrom(fieldType)) {
                // Отримати загальний тип
                Type genericType = field.getGenericType();
                if (genericType instanceof ParameterizedType paramType) {
                    Type[] typeArguments = paramType.getActualTypeArguments();

                    for (Type typeArg : typeArguments) {
                        if (typeArg instanceof Class<?> genericClass) {

                            // Перевірка чи це не рідний чи стандартний тип
                            if (!genericClass.isPrimitive() && !genericClass.getName().startsWith("java.")) {
                                RelationUML r = new RelationUML(RelationUML.USE,c.getSimpleName(),genericClass.getSimpleName(),"1","*", field.getName());
                                list.add(r);
                            }
                        }
                    }
                }
            } else if (!fieldType.isPrimitive() && !fieldType.getName().startsWith("java.")) {
                // Якщо це не колекція, перевірка типу безпосередньо
                RelationUML r = new RelationUML(RelationUML.USE,c.getSimpleName(),fieldType.getSimpleName(),"1","1", field.getName());
                list.add(r);
            }
        }

        return list;
    }

}