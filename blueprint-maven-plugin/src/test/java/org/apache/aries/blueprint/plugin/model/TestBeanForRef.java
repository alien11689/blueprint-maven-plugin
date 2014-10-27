package org.apache.aries.blueprint.plugin.model;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceUnit;

import org.apache.aries.blueprint.plugin.test.ServiceA;
import org.apache.aries.blueprint.plugin.test.ServiceB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

@Singleton
public class TestBeanForRef {
    @Inject ServiceA serviceA;
    @Autowired ServiceB serviceB;
    @Value("${name:default}") String name;
    @PersistenceUnit(unitName="myunit") EntityManager em;
}
