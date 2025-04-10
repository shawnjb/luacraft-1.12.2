package com.shawnjb.luacraft.docs;

import java.util.*;

public class LuaDocRegistry {

    public static class Param {
        public final String name;
        public final String type;
        public final String description;

        public Param(String name, String type, String description) {
            this.name = name;
            this.type = type;
            this.description = description;
        }
    }

    public static class Return {
        public final String type;
        public final String description;

        public Return(String type, String description) {
            this.type = type;
            this.description = description;
        }
    }

    public static class FunctionDoc {
        public final String name;
        public final String description;
        public final List<Param> params;
        public final List<Return> returns;
        public final boolean isMethod;

        public FunctionDoc(String name, String description, List<Param> params, List<Return> returns,
                boolean isMethod) {
            this.name = name;
            this.description = description;
            this.params = params;
            this.returns = returns;
            this.isMethod = isMethod;
        }
    }

    public static class FieldDoc {
        public final String name;
        public final String type;
        public final String description;

        public FieldDoc(String name, String type, String description) {
            this.name = name;
            this.type = type;
            this.description = description;
        }
    }

    private static final Map<String, List<FunctionDoc>> functionDocs = new LinkedHashMap<>();
    private static final Map<String, List<FieldDoc>> fieldDocs = new LinkedHashMap<>();
    private static final Set<String> classNames = new LinkedHashSet<>();
    private static final Set<String> globalClassNames = new HashSet<>();

    public static void addFunction(String category, FunctionDoc doc) {
        functionDocs.computeIfAbsent(category, k -> new ArrayList<>()).add(doc);
    }

    public static void addField(String className, FieldDoc field) {
        fieldDocs.computeIfAbsent(className, k -> new ArrayList<>()).add(field);
    }

    public static void addClass(String className) {
        classNames.add(className);
    }

    public static void addGlobalClass(String className) {
        classNames.add(className);
        globalClassNames.add(className);
    }

    private static final Map<String, String> globalClassDescriptions = new HashMap<>();

    public static void addGlobalClass(String className, String description) {
        classNames.add(className);
        globalClassNames.add(className);
        globalClassDescriptions.put(className, description);
    }

    public static String getGlobalClassDescription(String className) {
        return globalClassDescriptions.getOrDefault(className, "");
    }

    public static boolean isGlobalClass(String className) {
        return globalClassNames.contains(className);
    }

    public static Map<String, List<FunctionDoc>> getAllFunctions() {
        return functionDocs;
    }

    public static Map<String, List<FieldDoc>> getAllFields() {
        return fieldDocs;
    }

    public static Set<String> getAllClasses() {
        return classNames;
    }

    public static Set<String> getGlobalClasses() {
        return globalClassNames;
    }

    public static void addMethod(String category, FunctionDoc doc) {
        if (!doc.isMethod) {
            throw new IllegalArgumentException("addMethod() requires isMethod = true in FunctionDoc");
        }
        addFunction(category, doc);
    }

    public static void inheritMethods(String parentClass, String childClass) {
        List<FunctionDoc> parentFunctions = functionDocs.get(parentClass);
        if (parentFunctions != null) {
            for (FunctionDoc func : parentFunctions) {
                String newDescription = func.description + " (Inherited from " + parentClass + ")";
                FunctionDoc copy = new FunctionDoc(
                        func.name,
                        newDescription,
                        new ArrayList<>(func.params),
                        new ArrayList<>(func.returns),
                        func.isMethod);
                addFunction(childClass, copy);
            }
            System.out.println("Inherited " + parentFunctions.size() + " methods from " + parentClass + " to " + childClass);
        }
    
        List<FieldDoc> parentFields = fieldDocs.get(parentClass);
        if (parentFields != null) {
            for (FieldDoc field : parentFields) {
                FieldDoc fieldCopy = new FieldDoc(field.name, field.type, field.description + " (Inherited from " + parentClass + ")");
                addField(childClass, fieldCopy);
            }
        }
    }
    
    private static final List<FieldDoc> globalFields = new ArrayList<>();

    public static void addGlobalField(String fieldName, String type, String description) {
        globalFields.add(new FieldDoc(fieldName, type, description));
    }

    public static List<FieldDoc> getGlobalFields() {
        return globalFields;
    }

    public static void clear() {
        functionDocs.clear();
        fieldDocs.clear();
        classNames.clear();
        globalClassNames.clear();
    }
}