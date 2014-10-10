package net.lr.test.impl;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.glassfish.osgicdi.OSGiService;
import org.osgi.service.cm.ConfigurationAdmin;

import net.lr.test.EchoService;

@Singleton
public class EchoServiceImpl implements EchoService {
    @Inject @OSGiService
    ConfigurationAdmin configAdmin;
    
    public String echo(String msg) {
        return msg;
    }

}
