package org.classcraft.Composite;

import java.lang.reflect.*;
import java.lang.reflect.Method;
import java.util.*;

public class Introspection
{
    // Отримати всі поля класу
    public static List<AttributeModel> exploreFields(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        List<AttributeModel> attributeList = new ArrayList<>();

        for (Field field : fields) {
            String typeName = field.getType().getSimpleName();

            // Якщо поле є параметризованою колекцією (наприклад, List<String>)
            if (field.getGenericType() instanceof ParameterizedType paramType) {
                StringBuilder typeBuilder = new StringBuilder(field.getType().getSimpleName());
                typeBuilder.append("<");

                Type[] typeArguments = paramType.getActualTypeArguments();
                for (int i = 0; i < typeArguments.length; i++) {
                    typeBuilder.append(((Class<?>) typeArguments[i]).getSimpleName());
                    if (i < typeArguments.length - 1) {
                        typeBuilder.append(", ");
                    }
                }
                typeBuilder.append(">");
                typeName = typeBuilder.toString();
            }

            attributeList.add(new AttributeModel(field.getName(), typeName, field.getModifiers()));
        }

        return attributeList;
    }

    // Отримати всі методи класу
    public static List<MethodModel> exploreMethods(Class<?> clazz) {
        Method[] methods = clazz.getDeclaredMethods();
        Arrays.sort(methods, Comparator.comparing(Method::getName));
        List<MethodModel> methodList = new ArrayList<>();

        for (Method method : methods) {
            List<AttributeModel> parameters = new ArrayList<>();

            Type[] paramTypes = method.getGenericParameterTypes();
            Parameter[] params = method.getParameters();

            for (int i = 0; i < paramTypes.length; i++) {
                String paramTypeName;
                if (paramTypes[i] instanceof ParameterizedType) {
                    paramTypeName = getParameterizedTypeName((ParameterizedType) paramTypes[i]);
                } else if (paramTypes[i] instanceof Class) {
                    paramTypeName = ((Class<?>) paramTypes[i]).getSimpleName();
                } else {
                    paramTypeName = paramTypes[i].getTypeName();
                }

                parameters.add(new AttributeModel(params[i].getName(), paramTypeName, params[i].getModifiers()));
            }

            String returnTypeName;
            Type returnType = method.getGenericReturnType();

            if (returnType instanceof ParameterizedType) {
                returnTypeName = getParameterizedTypeName((ParameterizedType) returnType);
            } else if (returnType instanceof Class) {
                returnTypeName = ((Class<?>) returnType).getSimpleName();
            } else {
                returnTypeName = returnType.getTypeName();
            }

            methodList.add(new MethodModel(method.getName(), returnTypeName, method.getModifiers(), parameters));
        }

        return methodList;
    }

    // Повертає назву параметризованого типу, наприклад: List<String>
    private static String getParameterizedTypeName(ParameterizedType type) {
        StringBuilder typeName = new StringBuilder(((Class<?>) type.getRawType()).getSimpleName());
        typeName.append("<");

        Type[] args = type.getActualTypeArguments();
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof Class) {
                typeName.append(((Class<?>) args[i]).getSimpleName());
            } else {
                typeName.append(args[i].getTypeName());
            }
            if (i < args.length - 1) {
                typeName.append(", ");
            }
        }

        typeName.append(">");
        return typeName.toString();
    }

    // Отримати зв’язки класу: спадкування, реалізація, використання
    public static List<RelationModel> exploreRelations(Class<?> clazz) {
        List<RelationModel> relationList = new ArrayList<>();

        // Спадкування
        Class<?> superClass = clazz.getSuperclass();
        if (superClass != null && !superClass.getName().startsWith("java.")) {
            relationList.add(new RelationModel(RelationModel.EXTEND, clazz.getSimpleName(), superClass.getSimpleName()));
        }

        // Інтерфейси
        for (Class<?> iface : clazz.getInterfaces()) {
            relationList.add(new RelationModel(RelationModel.IMPLEMENT, clazz.getSimpleName(), iface.getSimpleName()));
        }

        // Зв’язки через поля
        for (Field field : clazz.getDeclaredFields()) {
            Class<?> fieldType = field.getType();

            if (Collection.class.isAssignableFrom(fieldType)) {
                Type genericType = field.getGenericType();
                if (genericType instanceof ParameterizedType paramType) {
                    for (Type typeArg : paramType.getActualTypeArguments()) {
                        if (typeArg instanceof Class<?> genericClass && !genericClass.getName().startsWith("java.")) {
                            relationList.add(new RelationModel(RelationModel.USE, clazz.getSimpleName(), genericClass.getSimpleName(), "1", "*", field.getName()));
                        }
                    }
                }
            } else if (!fieldType.isPrimitive() && !fieldType.getName().startsWith("java.")) {
                relationList.add(new RelationModel(RelationModel.USE, clazz.getSimpleName(), fieldType.getSimpleName(), "1", "1", field.getName()));
            }
        }

        return relationList;
    }
}