package net.lr.test;

import javax.inject.Inject;

import org.glassfish.osgicdi.OSGiService;
import org.springframework.stereotype.Component;

@Component
public class ServiceReferences {
    @Inject @OSGiService ServiceB serviceB;
}
