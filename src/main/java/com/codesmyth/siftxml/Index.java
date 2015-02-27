package com.codesmyth.siftxml;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

class Index {
    private Map<String, Entry> map = new HashMap<String, Entry>();

    /**
     * Recursively index given class and its fields.
     */
    public void create(Class cls) {
        create(cls, getClassHint(cls));
    }

    private void create(Class cls, String classHint) {
        create(cls, classHint, null);
    }

    private void create(Class cls, String classHint, Field clsField) {
        map.put(classHint, new Entry(clsField, cls));

        for (Field field : cls.getFields()) {
            String fieldHint = field.getName();

            Xml annotation = field.getAnnotation(Xml.class);
            if (annotation != null) {
                String value = annotation.value();
                if (",attr".equals(value)) {
                    fieldHint += value;
                } else {
                    fieldHint = value;
                }
            }

            String key = classHint + " > " + fieldHint;
            if (isAnnotated(field.getType())) {
                Class fieldType = field.getType();
                if (fieldType.isArray()) {
                    fieldType = fieldType.getComponentType();
                }
                create(fieldType, key, field);
            } else {
                map.put(key, new Entry(field));
            }
        }

        for (Method method : cls.getMethods()) {
            Xml annotation = method.getAnnotation(Xml.class);
            // require annotations on methods
            if (annotation != null) {
                String hint = method.getName();
                String value = annotation.value();
                if (",attr".equals(value)) {
                    hint += value;
                } else {
                    hint = value;
                }
                String key = classHint + " > " + hint;
                map.put(key, new Entry(method));
            }
        }
    }

    public Entry match(String hint) {
        if (map.containsKey(hint)) {
            return map.get(hint);
        }
        return EMPTY_ENTRY;
    }

    public boolean contains(String key) {
        return map.containsKey(key);
    }

    protected Set<String> keySet() {
        return map.keySet();
    }

    protected void clear() {
        map.clear();
    }

    /**
     * Get hint for matching class to xml node. If annotated with Xml,
     * the `value` argument is used, otherwise the simple name of class is used.
     */
    protected String getClassHint(Class cls) {
        String classHint = cls.getSimpleName();
        Xml annotation = (Xml) cls.getAnnotation(Xml.class);
        if (annotation != null) {
            classHint = annotation.value();
        }
        return classHint;
    }

    /**
     * Determine if class is annotated with Xml.
     */
    protected boolean isAnnotated(Class cls) {
        if (cls.isArray()) {
            cls = cls.getComponentType();
        }
        return cls.isAnnotationPresent(Xml.class);
    }

    public void println() {
        System.out.println("Indexed:");
        for (Map.Entry<String, Entry> e : map.entrySet()) {
            System.out.println(e.getKey() + " :: " + e.getValue());
        }
        System.out.println("-------------");
    }

    public static final Entry EMPTY_ENTRY = new Entry();

    static class Entry {

        // null signifies root result
        Field field;
        Method method;

        // null signifies no object nesting
        Class cls;

        private Entry() {
        }

        private Entry(Field field) {
            this(field, null, null);
        }

        private Entry(Method method) {
            this(null, method, null);
        }

        private Entry(Class cls) {
            this(null, null, cls);
        }

        private Entry(Field field, Class cls) {
            this(field, null, cls);
        }

        private Entry(Field field, Method method, Class cls) {
            this.field = field;
            this.method = method;
            this.cls = cls;
        }

        public Object getSetter() {
            if (field != null) {
                return field;
            }
            return method;
        }

        public boolean isEmpty() {
            return field == null && method == null && cls == null;
        }

        public boolean isRoot() {
            return (field == null || method == null) && cls != null;
        }

        public boolean isNested() {
            return (field != null || method != null) && cls != null;
        }

        public boolean isMember() {
            return (field != null || method != null) && cls == null;
        }
    }
}
