blueprint-maven-plugin
======================

Maven plugin to create blueprint xml from annotated code

Currently it supports the spring annotations:
@Component(value="beanid")
@Autowired
@Value("${myplaceholder}")

So this allows an easy transition from spring to OSGi/blueprint


For one of the next versions I also aim to support the JEE annotations to be independent from spring
----------------------------------------------------------------------------------------------------

http://docs.oracle.com/javaee/7/api/

http://docs.oracle.com/javaee/7/api/javax/inject/Inject.html
http://docs.oracle.com/javaee/7/api/javax/annotation/ManagedBean.html

http://docs.oracle.com/javaee/7/api/javax/persistence/PersistenceUnit.html
http://docs.oracle.com/javaee/7/api/javax/transaction/Transactional.html


http://search.maven.org/#artifactdetails|javax.annotation|javax.annotation-api|1.2|jar
http://search.maven.org/#artifactdetails|javax.transaction.cdi|javax.transaction.cdi-api|1.2-b03|jar
