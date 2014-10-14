package net.lr.test;

import javax.inject.Inject;

import org.ops4j.pax.cdi.api.OsgiService;
import org.springframework.stereotype.Component;

@Component
public class ServiceReferences {
    @Inject @OsgiService ServiceB serviceB;
}
