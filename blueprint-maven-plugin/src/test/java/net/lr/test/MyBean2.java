package net.lr.test;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MyBean2 implements Service1 {
    @Value("${url}")
    String url;

    public void setUrl(String url) {
        this.url = url;
    }
    
}
