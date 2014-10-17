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
        Assert.assertEquals(MyBean1.class, bean.clazz);
        Assert.assertEquals("myBean1", bean.id); // Name derived from class name
        Assert.assertEquals("init", bean.postConstruct);
        Assert.assertEquals("destroy", bean.preDestroy);
    }
    
    @Test
    public void testParseNamedBean() {
        Bean bean = new Bean(ServiceAImpl1.class);
        String definedName = ServiceAImpl1.class.getAnnotation(Named.class).value();
        Assert.assertEquals("my1", definedName);
        Assert.assertEquals(definedName, bean.id); // Name defined using @Named
    }

}
