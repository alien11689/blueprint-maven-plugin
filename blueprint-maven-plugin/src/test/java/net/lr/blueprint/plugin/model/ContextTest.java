package net.lr.blueprint.plugin.model;

import java.lang.reflect.Field;

import net.lr.test.MyBean3;
import net.lr.test.ServiceB;
import net.lr.test.ServiceReferences;

import org.junit.Assert;
import org.junit.Test;

public class ContextTest {

    @Test
    public void testLists()  {
        Context context = new Context(MyBean3.class);
        Assert.assertEquals(1, context.getBeans().size());
        Assert.assertEquals(0, context.getServiceRefs().size());
    }
    
    @Test
    public void testLists2()  {
        Context context = new Context(ServiceReferences.class);
        Assert.assertEquals(1, context.getBeans().size());
        Assert.assertEquals(1, context.getServiceRefs().size());
    }
    
    @Test
    public void testMatching() throws NoSuchFieldException, SecurityException  {
        Context context = new Context(ServiceReferences.class);
        Field field = ServiceReferences.class.getDeclaredFields()[0];
        Bean matching = context.getMatching(field);
        Assert.assertEquals(OsgiServiceBean.class, matching.getClass());
        Assert.assertEquals(ServiceB.class, matching.clazz);
        Assert.assertEquals("serviceB", matching.id);
    }
}
