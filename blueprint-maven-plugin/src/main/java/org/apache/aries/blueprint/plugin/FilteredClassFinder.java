package org.apache.aries.blueprint.plugin;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Singleton;

import org.apache.xbean.finder.ClassFinder;
import org.springframework.stereotype.Component;

public class FilteredClassFinder {
    
    @SuppressWarnings("unchecked")
    public static Set<Class<?>> findClasses(ClassFinder finder, Collection<String> packageNames) {
        return findClasses(finder, packageNames, new Class[]{Singleton.class, Component.class});
    }

    public static Set<Class<?>> findClasses(ClassFinder finder, Collection<String> packageNames, Class<? extends Annotation>[] annotations) {
        Set<Class<?>> rawClasses = new HashSet<Class<?>>();
        for (Class<? extends Annotation> annotation : annotations) {
            rawClasses.addAll(finder.findAnnotatedClasses(annotation));
        }
        return filterByBasePackages(rawClasses, packageNames);
    }
    
    private static Set<Class<?>> filterByBasePackages(Set<Class<?>> rawClasses, Collection<String> packageNames) {
        Set<Class<?>> filteredClasses = new HashSet<Class<?>>();
        for (Class<?> clazz : rawClasses) {
            for (String packageName : packageNames) {
                if (clazz.getPackage().getName().startsWith(packageName)) {
                    filteredClasses.add(clazz);
                    continue;
                }
            }
        }
        //System.out.println("Raw: " + rawClasses);
        //System.out.println("Filtered: " + beanClasses);
        return filteredClasses;
    }
}
