package net.lr.blueprint.plugin;

import net.lr.blueprint.plugin.Generator;

import org.apache.xbean.finder.ClassFinder;
import org.junit.Assert;
import org.junit.Test;


public class GeneratorTest {
    @Test
    public void testGenerate() throws Exception {
        ClassFinder classFinder = new ClassFinder(this.getClass().getClassLoader());
        new Generator(classFinder, "net.lr.test").generate(System.out);
    }
    
    @Test
    public void testCleanValue() {
        Generator generator = new Generator(null);
        String value = generator.cleanValue("$my{key:4}");
        Assert.assertEquals("$my{key}", value);
    }
}
