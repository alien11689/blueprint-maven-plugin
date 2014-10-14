package net.lr.test;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceUnit;
import javax.transaction.cdi.Transactional;
import javax.transaction.cdi.Transactional.TxType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
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
