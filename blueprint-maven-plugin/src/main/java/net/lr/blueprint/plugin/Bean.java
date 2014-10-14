package net.lr.blueprint.plugin;

import java.lang.reflect.Method;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Named;

import org.springframework.stereotype.Component;

public class Bean implements Comparable<Bean>{
    String id;
    Class<?> clazz;
    String postConstruct;
    String preDestroy;
    
    public Bean(Class<?> clazz) {
        this.clazz = clazz;
        this.id = getBeanName(clazz);
        for (Method method : clazz.getDeclaredMethods()) {
            PostConstruct postConstruct = method.getAnnotation(PostConstruct.class);
            if (postConstruct != null) {
                this.postConstruct = method.getName();
            }
            PreDestroy preDestroy = method.getAnnotation(PreDestroy.class);
            if (preDestroy != null) {
                this.preDestroy = method.getName();
            }
        }
    }
    
    static String getBeanName(Class<?> clazz) {
        Component component = clazz.getAnnotation(Component.class);
        Named named = clazz.getAnnotation(Named.class);
        if (component != null && !"".equals(component.value())) {
            return component.value();
        } else if (named != null && !"".equals(named.value())) {
                return named.value();    
        } else {
            String name = clazz.getSimpleName();
            return name.substring(0, 1).toLowerCase() + name.substring(1, name.length());
        }
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
    
    
}
