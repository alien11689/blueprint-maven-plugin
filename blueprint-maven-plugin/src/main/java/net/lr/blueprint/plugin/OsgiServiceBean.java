package net.lr.blueprint.plugin;

public class OsgiServiceBean extends Bean {

    String filter;

    public OsgiServiceBean(Class<?> clazz, String Filter) {
        super(clazz);
        filter = Filter;
    }

}
