package net.lr.test;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
public class MyBean3 {

    @Inject
    @Named("my1")
    ServiceA serviceA1;

    @Inject
    @Named("my2")
    ServiceA serviceA2;

    @Inject
    ServiceB serviceB;
}
