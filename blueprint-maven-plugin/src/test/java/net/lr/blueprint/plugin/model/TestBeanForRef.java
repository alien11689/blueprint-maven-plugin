package net.lr.blueprint.plugin.model;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import net.lr.test.ServiceA;
import net.lr.test.ServiceB;

@Singleton
public class TestBeanForRef {
    @Inject ServiceA serviceA;
    @Autowired ServiceB serviceB;
    @Value("${name:default}") String name;
    @PersistenceUnit(unitName="myunit") EntityManager em;
}
