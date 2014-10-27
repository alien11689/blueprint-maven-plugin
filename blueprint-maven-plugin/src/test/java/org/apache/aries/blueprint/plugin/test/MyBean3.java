package org.apache.aries.blueprint.plugin.test;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Singleton
@Transactional(propagation=Propagation.REQUIRES_NEW)
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
