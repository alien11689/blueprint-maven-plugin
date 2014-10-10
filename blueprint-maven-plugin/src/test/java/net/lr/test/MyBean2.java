package net.lr.test;

import javax.inject.Named;
import javax.inject.Singleton;

import org.springframework.beans.factory.annotation.Value;

@Singleton
@Named("my2")
public class MyBean2 implements Service1 {
    @Value("${url}")
    String url;

    public void setUrl(String url) {
        this.url = url;
    }
    
}
