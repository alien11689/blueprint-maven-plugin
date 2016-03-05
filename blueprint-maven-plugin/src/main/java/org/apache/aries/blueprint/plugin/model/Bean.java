/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.aries.blueprint.plugin.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.PersistenceUnit;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class Bean implements Comparable<Bean> {
    final public String id;
    final public Class<?> clazz;
    public String initMethod;
    public String destroyMethod;
    public SortedSet<Property> properties;
    public List<Argument> constructorArguments;
    public Field persistenceUnitField;
    public TransactionalDef transactionDef;

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
        this.transactionDef = new JavaxTransactionFactory().create(clazz);
        if (this.transactionDef == null) {
            this.transactionDef = new SpringTransactionFactory().create(clazz);
        }
        properties = new TreeSet<>();
        constructorArguments = new ArrayList<>();
    }

    private Field getPersistenceUnit() {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            PersistenceUnit persistenceUnit = field.getAnnotation(PersistenceUnit.class);
            if (persistenceUnit != null) {
                return field;
            }
        }
        return null;
    }

    public void resolve(Matcher matcher) {
        Class<?> curClass = this.clazz;
        resolveConstrctorArguments();
        while (curClass != Object.class) {
            resolveProperties(matcher, curClass);
            curClass = curClass.getSuperclass();
        }
    }

    private void resolveConstrctorArguments() {
        for (Constructor constructor : clazz.getDeclaredConstructors()) {
            Annotation inject = constructor.getAnnotation(Inject.class);
            Annotation autowired = constructor.getAnnotation(Autowired.class);
            if (inject != null || autowired != null) {
                Class[] parameterTypes = constructor.getParameterTypes();
                Annotation[][] parameterAnnotations = constructor.getParameterAnnotations();
                for (int i = 0; i < parameterTypes.length; ++i) {
                    Annotation[] annotations = parameterAnnotations[i];
                    String ref = null;
                    String value = null;
                    for (Annotation annotation : annotations) {
                        if (annotation.annotationType() == Named.class) {
                            ref = ((Named) annotation).value();
                            break;
                        }
                        if (annotation.annotationType() == Value.class) {
                            value = ((Value) annotation).value();
                            break;
                        }
                    }
                    if (ref == null && value == null) {
                        ref = getBeanName(parameterTypes[i]);
                    }
                    constructorArguments.add(new Argument(ref, value));
                }
                break;
            }
        }
    }

    private void resolveProperties(Matcher matcher, Class<?> curClass) {
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

    public void writeArguments(ArgumentWriter writer) {
        for (Argument argument : constructorArguments) {
            writer.writeArgument(argument);
        }
    }
}
