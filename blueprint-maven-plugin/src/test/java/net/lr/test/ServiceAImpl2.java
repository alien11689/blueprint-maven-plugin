package net.lr.test;

import javax.inject.Named;
import javax.inject.Singleton;

import org.ops4j.pax.cdi.api.OsgiServiceProvider;
import org.springframework.beans.factory.annotation.Value;

@Singleton
@Named("my2")
@OsgiServiceProvider(classes={ServiceA.class})
public class ServiceAImpl2 implements ServiceA {
    @Value("${url:http://default}")
    String url;

}
