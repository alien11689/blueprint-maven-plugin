package net.lr.test.impl;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Singleton;

import net.lr.test.EchoService;

import org.ops4j.pax.cdi.api.OsgiService;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;
import org.ops4j.pax.cdi.api.Properties;
import org.ops4j.pax.cdi.api.Property;
import org.osgi.service.cm.ConfigurationAdmin;

@Singleton
@OsgiServiceProvider(classes = { EchoService.class })
@Properties({ @Property(name = "name", value = "echo1") })
public class EchoServiceImpl implements EchoService {
    @Inject
    @OsgiService
    ConfigurationAdmin configAdmin;
    
    @PostConstruct
    public void init() {
        System.out.println("Init called");
    }
    
    @PreDestroy
    public void destroy() {
        System.out.println("Destroy called");
    }

    public String echo(String msg) {
        return msg;
    }

}
