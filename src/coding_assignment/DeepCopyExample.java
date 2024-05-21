package coding_assignment;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.lang.reflect.*;
import java.util.*;

public class DeepCopyExample {
    
    // Cache to store fields of a class to avoid repeated reflection operations
    private static Map<Class<?>, List<Field>> fieldsCache = new HashMap<>();

    /**
     * This method performs a deep copy of the given object.
     * It handles different types of objects like primitives, Collections, Maps, Arrays, and custom objects.
     * For custom objects, it copies each non-final field recursively.
     * @param original The object to be copied
     * @return A deep copy of the original object
     */
    public static Object deepCopy(Object original) throws Exception {
        // Handle null and immutable types
        if (original == null || original.getClass().isPrimitive() || 
            original instanceof String || original instanceof Integer || 
            original instanceof Byte || original instanceof Character || 
            original instanceof Short || original instanceof Boolean || 
            original instanceof Long || original instanceof Float || 
            original instanceof Double) {
            return original;
        }

        // Handle Man objects
        if (original instanceof Man) {
            Man originalMan = (Man) original;
            String name = originalMan.getName();
            int age = originalMan.getAge();
            List<String> favoriteBooks = new ArrayList<>(originalMan.getFavoriteBooks());
            return new Man(name, age, favoriteBooks);
        }

        // Handle Collections
        if (original instanceof Collection) {
            Collection<?> originalCollection = (Collection<?>) original;
            Collection copiedCollection = originalCollection.getClass().newInstance();
            for (Object item : originalCollection) {
                copiedCollection.add(item instanceof String || item instanceof Integer || 
                                     item instanceof Byte || item instanceof Character || 
                                     item instanceof Short || item instanceof Boolean || 
                                     item instanceof Long || item instanceof Float || 
                                     item instanceof Double ? item : deepCopy(item));
            }
            return copiedCollection;
        }

        // Handle Maps
        if (original instanceof Map) {
            Map<?, ?> originalMap = (Map<?, ?>) original;
            Map copiedMap = originalMap.getClass().newInstance();
            for (Map.Entry<?, ?> entry : originalMap.entrySet()) {
                copiedMap.put(entry.getKey() instanceof String || entry.getKey() instanceof Integer || 
                              entry.getKey() instanceof Byte || entry.getKey() instanceof Character || 
                              entry.getKey() instanceof Short || entry.getKey() instanceof Boolean || 
                              entry.getKey() instanceof Long || entry.getKey() instanceof Float || 
                              entry.getKey() instanceof Double ? entry.getKey() : deepCopy(entry.getKey()), 
                              entry.getValue() instanceof String || entry.getValue() instanceof Integer || 
                              entry.getValue() instanceof Byte || entry.getValue() instanceof Character || 
                              entry.getValue() instanceof Short || entry.getValue() instanceof Boolean || 
                              entry.getValue() instanceof Long || entry.getValue() instanceof Float || 
                              entry.getValue() instanceof Double ? entry.getValue() : deepCopy(entry.getValue()));
            }
            return copiedMap;
        }

        // Handle Arrays
        if (original.getClass().isArray()) {
            int length = Array.getLength(original);
            Object copiedArray = Array.newInstance(original.getClass().getComponentType(), length);
            for (int i = 0; i < length; i++) {
                Array.set(copiedArray, i, deepCopy(Array.get(original, i)));
            }
            return copiedArray;
        }

        // Handle other objects
        Object copiedObject = original.getClass().newInstance();

        for (Field field : getAllFields(original.getClass())) {
            if (!Modifier.isFinal(field.getModifiers())) {
                field.setAccessible(true);
                field.set(copiedObject, deepCopy(field.get(original)));
            }
        }

        return copiedObject;
    }

    /**
     * This method retrieves all fields of a class, including those from its superclasses.
     * It uses a cache to avoid repeated reflection operations.
     * @param clazz The class to retrieve fields from
     * @return A list of all fields of the class
     */
    private static List<Field> getAllFields(Class<?> clazz) {
        if (fieldsCache.containsKey(clazz)) {
            return fieldsCache.get(clazz);
        }

        List<Field> fields = new ArrayList<>();
        while (clazz != null) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }

        fieldsCache.put(clazz, fields);
        return fields;
    }

    /**
     * The main method for testing the deep copy operation.
     */
    public static void main(String[] args) throws Exception {
        // Create a list of favorite books
        List<String> books = new ArrayList<>();
        books.add("Book1");
        books.add("Book2");

        // Create a new Man object
        Man man = new Man("Man1", 25, books);
        System.out.println("Original man's favorite books: " + man.getFavoriteBooks());

        // Create a deep copy of the Man object using the deepCopy method
        Man copiedMan = (Man) deepCopy(man);
        System.out.println("Copied man's favorite books: " + copiedMan.getFavoriteBooks());

        // Modify the original Man object
        man.getFavoriteBooks().add("Book3");
        System.out.println("Original man's favorite books after modification: " + man.getFavoriteBooks());

        // The copied Man object's favorite books should not be affected by the modification of the original Man object
        System.out.println("Copied man's favorite books after original's modification: " + copiedMan.getFavoriteBooks());
    }
}