package net.lr.blueprint.plugin.model;

import java.lang.reflect.Field;


public interface Matcher {
    Bean getMatching(Field field);
}
