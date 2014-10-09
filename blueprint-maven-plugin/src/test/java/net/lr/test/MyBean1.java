package net.lr.test;

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
    Service1 bean2;
    
    @PersistenceUnit(unitName="person")
    EntityManager em;

    public void setBean2(Service1 bean2) {
        this.bean2 = bean2;
    }
    
    public void setEm(EntityManager em) {
        this.em = em;
    }
    
    public void saveData() {
        
    }
}
