package net.lr.blueprint.plugin.model;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Named;
import javax.persistence.PersistenceUnit;

import org.springframework.stereotype.Component;

public class Bean implements Comparable<Bean>{
    public String id;
    public Class<?> clazz;
    public String initMethod;
    public String destroyMethod;
    public SortedSet<Property> properties;
    public Field persistenceUnitField; 
    
    public Bean(Class<?> clazz) {
        this.clazz = clazz;
        this.id = getBeanName(clazz);
        for (Method method : clazz.getDeclaredMethods()) {
            PostConstruct postConstruct = method.getAnnotation(PostConstruct.class);
            if (postConstruct != null) {
                this.initMethod = method.getName();
            }
            PreDestroy preDestroy = method.getAnnotation(PreDestroy.class);
            if (preDestroy != null) {
                this.destroyMethod = method.getName();
            }
        }
        this.persistenceUnitField = getPersistenceUnit();
        properties = new TreeSet<>();
    }

    private Field getPersistenceUnit() {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            PersistenceUnit persistenceUnit = field.getAnnotation(PersistenceUnit.class);
            if (persistenceUnit !=null) {
                 return field;
            }
        }
        return null;
    }
    
    public void resolve(Matcher matcher) {
        Class<?> curClass = this.clazz;
        while (curClass != Object.class) {
            addFields(matcher, curClass);
            curClass = curClass.getSuperclass();
        }
    }
    
    private void addFields(Matcher matcher, Class<?> curClass) {
        for (Field field : curClass.getDeclaredFields()) {
            Property prop = Property.create(matcher, field);
            if (prop != null) {
                properties.add(prop);
            }
        }
    }

    public static String getBeanName(Class<?> clazz) {
        Component component = clazz.getAnnotation(Component.class);
        Named named = clazz.getAnnotation(Named.class);
        if (component != null && !"".equals(component.value())) {
            return component.value();
        } else if (named != null && !"".equals(named.value())) {
            return named.value();    
        } else {
            String name = clazz.getSimpleName();
            return getBeanNameFromSimpleName(name);
        }
    }

    private static String getBeanNameFromSimpleName(String name) {
        return name.substring(0, 1).toLowerCase() + name.substring(1, name.length());
    }

    public boolean matches(Class<?> destType, String destId) {
        boolean assignable = destType.isAssignableFrom(this.clazz);
        return assignable && ((destId == null) || id.equals(destId));
    }

    @Override
    public int compareTo(Bean other) {
        return this.clazz.getName().compareTo(other.clazz.getName());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((clazz == null) ? 0 : clazz.getName().hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return clazz.getName();
    }

    public void writeProperties(PropertyWriter writer) {
        for (Property property : properties) {
            writer.writeProperty(property);
        }
    }
    
}
