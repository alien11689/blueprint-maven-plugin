package org.apache.aries.blueprint.plugin.model;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.inject.Named;

import org.ops4j.pax.cdi.api.OsgiService;

public class Context implements Matcher {

    SortedSet<Bean> beans;
    SortedSet<OsgiServiceBean> serviceRefs;
    
    public Context(Class<?>... beanClasses) {
        this(Arrays.asList(beanClasses));
    }

    public Context(Collection<Class<?>> beanClasses) {
        this.beans = new TreeSet<Bean>();
        this.serviceRefs = new TreeSet<OsgiServiceBean>();
        addBeans(beanClasses);
    }

    private void addBeans(Collection<Class<?>> beanClasses) {
        for (Class<?> clazz : beanClasses) {
            Bean bean = new Bean(clazz);
            beans.add(bean);
            addServiceRefs(clazz);
        }
    }

    private void addServiceRefs(Class<?> clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            OsgiService osgiService = field.getAnnotation(OsgiService.class);
            if (osgiService != null) {
                serviceRefs.add(new OsgiServiceBean(field.getType(), osgiService));
            }
        }
    }
    
    public void resolve() {
        for (Bean bean : beans) {
            bean.resolve(this);
        }
    }
    
    public Bean getMatching(Field field) {
        Named named = field.getAnnotation(Named.class);
        String destId = (named == null) ? null : named.value();
        // TODO Replace loop by lookup
        for (Bean bean : beans) {
            if (bean.matches(field.getType(), destId)) {
                return bean;
            }
        }
        for (Bean bean : serviceRefs) {
            if (bean.matches(field.getType(), destId)) {
                return bean;
            }
        }
        return null;
    }

    public SortedSet<Bean> getBeans() {
        return beans;
    }
    
    public SortedSet<OsgiServiceBean> getServiceRefs() {
        return serviceRefs;
    }


}
