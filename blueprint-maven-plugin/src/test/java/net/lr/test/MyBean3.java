package net.lr.test;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
public class MyBean3 {

    @Inject
    @Named("my1")
    ServiceA service1;

    @Inject
    @Named("my2")
    ServiceA service2;

}
