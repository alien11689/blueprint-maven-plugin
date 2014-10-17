package net.lr.blueprint.plugin.model;

import javax.inject.Named;

import net.lr.blueprint.plugin.model.Bean;
import net.lr.test.MyBean1;
import net.lr.test.ServiceAImpl1;

import org.junit.Assert;
import org.junit.Test;


public class BeanTest {
    
    @Test
    public void testParseMyBean1() {
        Bean bean = new Bean(MyBean1.class);
        bean.resolve(new Context());
        Assert.assertEquals(MyBean1.class, bean.clazz);
        Assert.assertEquals("myBean1", bean.id); // Name derived from class name
        Assert.assertEquals("init", bean.initMethod);
        Assert.assertEquals("destroy", bean.destroyMethod);
        Assert.assertEquals("em", bean.persistenceUnitField.getName());
        Assert.assertEquals(1, bean.properties.size());
        Property prop = bean.properties.iterator().next();
        Assert.assertEquals("bean2", prop.name);
        Assert.assertEquals("serviceA", prop.ref);
    }
    
    @Test
    public void testParseNamedBean() {
        Bean bean = new Bean(ServiceAImpl1.class);
        bean.resolve(new Context());
        String definedName = ServiceAImpl1.class.getAnnotation(Named.class).value();
        Assert.assertEquals("my1", definedName);
        Assert.assertEquals("Name should be defined using @Named", definedName, bean.id);
        Assert.assertNull("There should be no initMethod", bean.initMethod);
        Assert.assertNull("There should be no destroyMethod", bean.destroyMethod);
        Assert.assertNull("There should be no persistenceUnit", bean.persistenceUnitField);
        Assert.assertEquals("There should be no properties", 0, bean.properties.size());
    }

}
