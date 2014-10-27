package org.apache.aries.blueprint.plugin.model;

import org.ops4j.pax.cdi.api.OsgiService;

/**
 * Synthetic bean that refers to an OSGi service
 */
public class OsgiServiceBean extends Bean {

    public String filter;

    public OsgiServiceBean(Class<?> clazz, OsgiService osgiService) {
        super(clazz);
        filter = osgiService.filter();
    }

}
