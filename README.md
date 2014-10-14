blueprint-maven-plugin
======================

Maven plugin to create blueprint xml from annotated code. It supports two big use cases:
1. Simple transition from spring to blueprint
2. Using blueprint with almost no XML

For examples see the example project and the test classes in the plugin.


Currently it supports the following annotations:

JEE Annotations
---------------
    @Singleton - To define beans
    @Inject - To inject beans into fields
    @Named("myname") - To qualify beans and injects if you need a special impl
    @Transactional - To define a bean as transactional
    @PersistenceUnit(unitName="myunit") - To inject a managed EntityManager for a persistence unit into a bean

pax cdi annotations
-------------------
    @OsgiService - To inject a service or provide a service reference for other injects. Also allows to add a filter
    @OsgiServiceProvider - To publish a bean as a service
    @Properties, @Property - To add service properties to a published service

Spring Annotations
------------------
    @Component(value="beanid") - To define beans
    @Autowired - To inject beans
    @Value("${myplaceholder}") - To inject config admin properties


