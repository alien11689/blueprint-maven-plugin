package org.apache.aries.blueprint.plugin.test;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceUnit;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.springframework.beans.factory.annotation.Autowired;

@Singleton
@Transactional(value=TxType.REQUIRED)
public class MyBean1 {

    @Autowired
    ServiceA bean2;
    
    @PersistenceUnit(unitName="person")
    EntityManager em;
    
    @PostConstruct
    public void init() {
        
    }
    
    @PreDestroy
    public void destroy() {
        
    }

    public void saveData() {
        
    }
}
