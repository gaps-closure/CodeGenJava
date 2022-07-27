package com.peratonlabs.closure.dfdlgen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import clsvis.model.Class_;

public interface Utils 
{
    /**
     * Extracts and returns root cause from the given throwable.
     */
    static String rootCauseAsString(Throwable throwable) {
        while (throwable.getCause() != null) {
            throwable = throwable.getCause();
        }
        return throwable.toString();
    }

    /**
     * Returns the type which can be load or null otherwise (primitives, anonymous).
     */
    static Class<?> getClassType(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }
        // Array - substitute with original class
        while (clazz.isArray()) {
            clazz = clazz.getComponentType();
        }
        // Skip anonymous classes, primitives
        if (clazz.isAnonymousClass() || clazz.isPrimitive()) {
            return null;
        }
        // Return the type
        return clazz;
    }

    /**
     * Returns given color as RRGGBB string.
     */
    static String colorAsRRGGBB(int color) {
        return String.format( "%06X", color );
    }
    
    
    public static int sortByDepth(Class_ clazz, ArrayList<Class_> toBeSorted) {
        int maxDepth = 0;
        int depth;
        
        Class_ root = clazz;
        
        depth = findDepth(root, clazz);
        if (depth > maxDepth)
            maxDepth = depth;

        for (Class_ sub : toBeSorted) {
            depth = findDepth(root, sub);
            if (depth > maxDepth)
                maxDepth = depth;
        }
        
        toBeSorted.add(clazz);
        Collections.sort(toBeSorted, new ClassComparator());
        
        return maxDepth;
    }
    
    static int findDepth(Class_ root, Class_ clazz) {
        Class_ x = clazz;
        int depth = 0;
        while (x != root) {
            depth++;
            x = x.getSuperclass();
        }
        clazz.setDepth(depth);
        
        return depth;
    }
    
    class ClassComparator implements Comparator<Class_> {
        @Override
        public int compare(Class_ o1, Class_ o2) {
            int d1 = o1.getDepth();
            int d2 = o2.getDepth();

            if (d1 < d2)
                return 1;
            else if (d1 == d2)
                return 0;
            else
                return -1;
        }
    }
}
