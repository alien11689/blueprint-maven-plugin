blueprint-maven-plugin
======================

Maven plugin to create blueprint xml from annotated code. It supports two big use cases:
1. Simple transition from spring to blueprint
2. Using blueprint with almost no XML

For examples see the example project and the test classes in the plugin.


Currently it supports the following annotations:

JEE Annotations
---------------

Annotation          | Description
------------------- | -----------
@Singleton          | Define beans
@Inject             | Inject beans into fields
@Named("myname")    | Qualify beans and injects if you need a special impl
@Transactional      | Define a bean as transactional
@PersistenceUnit
(unitName="myunit") | Inject a managed EntityManager for a persistence unit into a bean

Pax CDI Annotations
-------------------


Annotation           | Description
-------------------- | -----------
@OsgiService         | Inject a service or provide a service reference for other injects. Also allows to add a filter
@OsgiServiceProvider | Publish a bean as a service
@Properties
@Property            | Add service properties to a published service

Spring Annotations
------------------

Annotation           | Description
-------------------- | -----------
@Component(value="beanid") | Define beans
@Autowired                 | Inject beans
@Value("${myplaceholder}") | Inject config admin properties
