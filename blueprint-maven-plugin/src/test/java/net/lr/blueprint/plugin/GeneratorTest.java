package net.lr.blueprint.plugin;

import net.lr.blueprint.plugin.Generator;

import org.apache.xbean.finder.ClassFinder;
import org.junit.Test;


public class GeneratorTest {
    @Test
    public void testGenerate() throws Exception {
        ClassFinder classFinder = new ClassFinder(this.getClass().getClassLoader());
        new Generator(classFinder, "net.lr.test").generate(System.out);
    }
}
