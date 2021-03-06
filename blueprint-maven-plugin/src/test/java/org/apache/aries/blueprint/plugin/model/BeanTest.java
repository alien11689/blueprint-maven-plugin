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

import org.apache.aries.blueprint.plugin.test.MyBean1;
import org.apache.aries.blueprint.plugin.test.MyBean3;
import org.apache.aries.blueprint.plugin.test.MyBean4;
import org.apache.aries.blueprint.plugin.test.ServiceAImpl1;
import org.junit.Test;

import javax.inject.Named;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


public class BeanTest {

    @Test
    public void testParseMyBean1() {
        Bean bean = new Bean(MyBean1.class);
        bean.resolve(new Context());
        assertEquals(MyBean1.class, bean.clazz);
        assertEquals("myBean1", bean.id); // Name derived from class name
        assertEquals("init", bean.initMethod);
        assertEquals("destroy", bean.destroyMethod);
        assertEquals("em", bean.persistenceUnitField.getName());
        assertEquals("*", bean.transactionDef.getMethod());
        assertEquals("Required", bean.transactionDef.getType());
        assertEquals(1, bean.properties.size());
        Property prop = bean.properties.iterator().next();
        assertEquals("bean2", prop.name);
        assertEquals("serviceA", prop.ref);
    }

    @Test
    public void testParseMyBean3() {
        Bean bean = new Bean(MyBean3.class);
        bean.resolve(new Context());
        assertEquals(MyBean3.class, bean.clazz);
        assertEquals("myBean3", bean.id); // Name derived from class name
        assertNull("There should be no initMethod", bean.initMethod);
        assertNull("There should be no destroyMethod", bean.destroyMethod);
        assertNull("There should be no persistenceUnit", bean.persistenceUnitField);
        assertEquals("*", bean.transactionDef.getMethod());
        assertEquals("RequiresNew", bean.transactionDef.getType());
        assertEquals(3, bean.properties.size());
    }

    @Test
    public void testParseMyBean4() {
        Bean bean = new Bean(MyBean4.class);
        bean.resolve(new Context());
        assertEquals(MyBean4.class, bean.clazz);
        assertEquals("myBean4", bean.id); // Name derived from class name
        assertNull("There should be no initMethod", bean.initMethod);
        assertNull("There should be no destroyMethod", bean.destroyMethod);
        assertNull("There should be no persistenceUnit", bean.persistenceUnitField);
        assertEquals(0, bean.properties.size());
        assertEquals(6, bean.constructorArguments.size());
    }

    @Test
    public void testParseNamedBean() {
        Bean bean = new Bean(ServiceAImpl1.class);
        bean.resolve(new Context());
        String definedName = ServiceAImpl1.class.getAnnotation(Named.class).value();
        assertEquals("my1", definedName);
        assertEquals("Name should be defined using @Named", definedName, bean.id);
        assertNull("There should be no initMethod", bean.initMethod);
        assertNull("There should be no destroyMethod", bean.destroyMethod);
        assertNull("There should be no persistenceUnit", bean.persistenceUnitField);
        assertNull("There should be no transaction definition", bean.transactionDef);
        assertEquals("There should be no properties", 0, bean.properties.size());
    }

}
