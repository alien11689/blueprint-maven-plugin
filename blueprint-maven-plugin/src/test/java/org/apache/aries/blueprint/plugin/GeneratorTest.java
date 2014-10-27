package org.apache.aries.blueprint.plugin;

import static java.util.Arrays.asList;
import static org.apache.aries.blueprint.plugin.FilteredClassFinder.findClasses;

import java.util.Set;

import org.apache.aries.blueprint.plugin.Generator;
import org.apache.aries.blueprint.plugin.model.Context;
import org.apache.aries.blueprint.plugin.test.MyBean1;
import org.apache.xbean.finder.ClassFinder;
import org.junit.Test;


public class GeneratorTest {
    @Test
    public void testGenerate() throws Exception {
        ClassFinder classFinder = new ClassFinder(this.getClass().getClassLoader());
        String packageName = MyBean1.class.getPackage().getName();
        Set<Class<?>> beanClasses = findClasses(classFinder, asList(packageName));
        Context context = new Context(beanClasses);
        context.resolve();
        new Generator(context, System.out).generate();
    }
    
}
