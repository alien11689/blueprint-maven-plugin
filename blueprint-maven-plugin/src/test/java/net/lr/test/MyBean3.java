package net.lr.test;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
public class MyBean3 {

    @Inject @Named("my2")
    Service1 bean2;
    
    public void setBean2(Service1 bean2) {
        this.bean2 = bean2;
    }
    
}
