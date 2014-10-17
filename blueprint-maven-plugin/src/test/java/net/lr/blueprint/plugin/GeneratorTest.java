package net.lr.blueprint.plugin;

import static java.util.Arrays.asList;
import static net.lr.blueprint.plugin.FilteredClassFinder.findClasses;

import java.util.Set;

import net.lr.blueprint.plugin.model.Context;

import org.apache.xbean.finder.ClassFinder;
import org.junit.Test;


public class GeneratorTest {
    @Test
    public void testGenerate() throws Exception {
        ClassFinder classFinder = new ClassFinder(this.getClass().getClassLoader());
        Set<Class<?>> beanClasses = findClasses(classFinder, asList("net.lr.test"));
        Context context = new Context(beanClasses);
        context.resolve();
        new Generator(context, System.out).generate();
    }
    
}
