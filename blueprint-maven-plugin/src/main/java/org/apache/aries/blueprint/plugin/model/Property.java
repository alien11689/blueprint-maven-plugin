package org.apache.aries.blueprint.plugin.model;

import java.lang.reflect.Field;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class Property implements Comparable<Property> {
    public String name;
    public String ref;
    public String value;

    public Property(String name, String ref, String value) {
        this.name = name;
        this.ref = ref;
        this.value = value;
    }

    public static Property create(Matcher matcher, Field field) {
        Value value = field.getAnnotation(Value.class);
        if (needsInject(field)) {
            Bean matching = matcher.getMatching(field);
            String ref = (matching == null) ? getRefName(field) : matching.id;
            return new Property(field.getName(), ref, null);
        } else if (value != null){
            return new Property(field.getName(), null, cleanValue(value.value()));
        } else {
            // Field is not a property
            return null;
        }
    }

    /**
     * Assume it is defined in another manually created blueprint context with default name
     * @param field
     * @return
     */
    private static String getRefName(Field field) {
        Named named = field.getAnnotation(Named.class);
        return (named != null) ? named.value() : Bean.getBeanName(field.getType());
    }

    private static boolean needsInject(Field field) {
        return field.getAnnotation(Autowired.class) != null || field.getAnnotation(Inject.class) != null;
    }

    /**
     * Remove default value definition
     * 
     * @param value
     * @return
     */
    private static String cleanValue(String value) {
        return value.replaceAll("\\:.*\\}", "}");
    }

    @Override
    public int compareTo(Property other) {
        return name.compareTo(other.name);
    }

}
