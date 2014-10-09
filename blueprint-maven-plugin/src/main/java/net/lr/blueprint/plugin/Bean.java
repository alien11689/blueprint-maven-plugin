package net.lr.blueprint.plugin;

import org.springframework.stereotype.Component;

public class Bean {
    String id;
    Class<?> clazz;
    
    public Bean(Class<?> clazz) {
        this.clazz = clazz;
        this.id = getBeanName(clazz);
    }
    
    static String getBeanName(Class<?> clazz) {
        Component component = clazz.getAnnotation(Component.class);
        if (component != null && !"".equals(component.value())) {
            return component.value();
        } else {
            String name = clazz.getSimpleName();
            return name.substring(0, 1).toLowerCase() + name.substring(1, name.length());
        }
    }


    
}
