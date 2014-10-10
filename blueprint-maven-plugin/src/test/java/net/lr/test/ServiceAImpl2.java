package net.lr.test;

import javax.inject.Named;
import javax.inject.Singleton;

import org.springframework.beans.factory.annotation.Value;

@Singleton
@Named("my2")
public class ServiceAImpl2 implements ServiceA {
    @Value("${url}")
    String url;

}
