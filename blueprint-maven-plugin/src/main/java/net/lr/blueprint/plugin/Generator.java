package net.lr.blueprint.plugin;

import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.persistence.PersistenceUnit;
import javax.transaction.cdi.Transactional;
import javax.transaction.cdi.Transactional.TxType;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.xbean.finder.ClassFinder;
import org.ops4j.pax.cdi.api.OsgiService;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

public class Generator {
    SortedSet<Bean> availableBeans;

    private String[] packageNames;
    private ClassFinder finder;
    
    Map<TxType, String> txTypeNames;
    Map<Class<?>, Bean> refs;

    public Generator(ClassFinder finder, String... packageNames) {
        this.finder = finder;
        this.packageNames = packageNames;
        this.availableBeans = new TreeSet<>();
        this.refs = new HashMap<>();
        this.txTypeNames = new HashMap<Transactional.TxType, String>();
        this.txTypeNames.put(TxType.REQUIRED, "Required");
    }

    public void generate(OutputStream os) {
        Set<Class<?>> rawClasses = new HashSet<>(finder.findAnnotatedClasses(Component.class));
        rawClasses.addAll(finder.findAnnotatedClasses(Singleton.class));
        Set<Class<?>> beanClasses = filterByBasePackages(rawClasses, packageNames);
        System.out.println("Raw: " + rawClasses);
        System.out.println("Filtered: " + beanClasses);
        addBeans(beanClasses);

        try {
            XMLOutputFactory factory = XMLOutputFactory.newInstance();
            XMLStreamWriter writer = factory.createXMLStreamWriter(os);
            writer.writeStartDocument();
            
            writer.writeStartElement("blueprint");
            writer.writeDefaultNamespace("http://www.osgi.org/xmlns/blueprint/v1.0.0");
            writer.writeNamespace("ext", "http://aries.apache.org/blueprint/xmlns/blueprint-ext/v1.0.0");
            
            writer.writeCharacters("\n");
            for (Bean bean : availableBeans) {
                if (!(bean instanceof OsgiServiceBean)) {
                    writeBean(writer, bean);
                }
            }
            
            writeServiceRefs(writer);
            writeServiceProviders(writer);
            
            writer.writeEndElement();
            writer.writeEndDocument();
            writer.close();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    

    private void addBeans(Set<Class<?>> beanClasses) {
        for (Class<?> clazz : beanClasses) {
            Bean bean = new Bean(clazz);
            availableBeans.add(bean);
            for (Field field : clazz.getDeclaredFields()) {
                OsgiService osgiService = field.getAnnotation(OsgiService.class);
                if (osgiService != null) {
                    availableBeans.add(new OsgiServiceBean(field.getType(), osgiService.filter()));
                }
            }
        }
    }
    
    private void writeServiceProviders(XMLStreamWriter writer) throws XMLStreamException {
        for (Bean bean : availableBeans) {
            writeServiceProvider(bean, writer);
        }
    }

    private void writeServiceProvider(Bean bean, XMLStreamWriter writer) throws XMLStreamException {
        OsgiServiceProvider serviceProvider = bean.clazz.getAnnotation(OsgiServiceProvider.class);
        if (serviceProvider == null) {
            return;
        }
        writer.writeEmptyElement("service");
        writer.writeAttribute("ref", bean.id);
        if (serviceProvider.classes().length == 0) {
            throw new IllegalArgumentException("Need to provide the interface class for OsgiServiceProvider");
        }
        Class<?> serviceIf = serviceProvider.classes()[0];
        writer.writeAttribute("interface", serviceIf.getName());
        writer.writeCharacters("\n");
    }

    private Bean getMatching(Class<?> clazz, Field field) {
        // TODO Replace loop by lookup
        for (Bean bean : availableBeans) {
            Named named = field.getAnnotation(Named.class);
            String destId = (named == null) ? null : named.value();
            if (bean.matches(field.getType(), destId)) {
                return bean;
            }
        }
        System.out.println("Unmatched ref " + clazz.getName() + "," + field.getName() + ", " + field.getType());
        return null;
    }

    private Set<Class<?>> filterByBasePackages(Set<Class<?>> rawClasses, String[] packageNames) {
        Set<Class<?>> filteredClasses = new HashSet<>();
        for (Class<?> clazz : rawClasses) {
            for (String packageName : packageNames) {
                if (clazz.getPackage().getName().startsWith(packageName)) {
                    filteredClasses.add(clazz);
                    continue;
                }
            }
        }
        return filteredClasses;
    }

    private void writeServiceRefs(XMLStreamWriter writer) throws XMLStreamException {
        for (Bean bean : availableBeans) {
            if (bean instanceof OsgiServiceBean) {
                OsgiServiceBean serviceBean = (OsgiServiceBean)bean;
                writer.writeEmptyElement("reference");
                writer.writeAttribute("id", serviceBean.id);
                writer.writeAttribute("interface", serviceBean.clazz.getName());
                if (serviceBean.filter != null && !"".equals(serviceBean.filter)) {
                    writer.writeAttribute("filter", serviceBean.filter);
                }
                writer.writeCharacters("\n");
            }
        }
    }
    
    private void writeBean(XMLStreamWriter writer, Bean bean)
            throws XMLStreamException {
        Class<?> beanClass = bean.clazz;
        writer.writeStartElement("bean");
        writer.writeAttribute("id", bean.id);
        writer.writeAttribute("class", bean.clazz.getName());
        writer.writeAttribute("ext", "http://aries.apache.org/blueprint/xmlns/blueprint-ext/v1.0.0", "field-injection", "true");
        if (bean.preDestroy != null) {
            writer.writeAttribute("init-method", bean.postConstruct);
        }
        if (bean.preDestroy != null) {
            writer.writeAttribute("destroy-method", bean.preDestroy);
        }
        writer.writeCharacters("\n");
        writeTransactional(writer, bean.clazz);
        Class<?> curClass = beanClass;
        while (curClass != Object.class) {
            writeFields(writer, curClass);
            curClass = curClass.getSuperclass();
        }
        writer.writeEndElement();
        writer.writeCharacters("\n");
    }

    private void writeFields(XMLStreamWriter writer, Class<?> beanClass)
            throws XMLStreamException {
        Field[] fields = beanClass.getDeclaredFields();
        for (Field field : fields) {
            writePersistenceUnit(writer, field);
        }
        for (Field field : fields) {
            if (needsInject(field)) {
                Bean matching = getMatching(beanClass, field);
                writeProperty(writer, field, matching);
            }
            Value value = field.getAnnotation(Value.class);
            if (value != null) {
                writePropertyValue(writer, field.getName(), cleanValue(value.value()));
            }
        }
    }

    /**
     * Remove default value definition
     * 
     * @param value
     * @return
     */
    String cleanValue(String value) {
        return value.replaceAll("\\:.*\\}", "}");
    }

    private boolean needsInject(Field field) {
        return field.getAnnotation(Autowired.class) != null || field.getAnnotation(Inject.class) != null;
    }

    private void writeTransactional(XMLStreamWriter writer, Class<?> clazz)
            throws XMLStreamException {
        Transactional transactional = clazz.getAnnotation(Transactional.class);
        if (transactional != null) {
            writer.writeCharacters("    ");
            writer.writeStartElement("tx", "transaction", "http://aries.apache.org/xmlns/transactions/v1.0.0");
            writer.writeAttribute("method", "*");
            writer.writeAttribute("value", txTypeNames.get(transactional.value()));
            writer.writeEndElement();
            writer.writeCharacters("\n");
        }
    }

    private void writePersistenceUnit(XMLStreamWriter writer, Field field)
            throws XMLStreamException {
        PersistenceUnit persistenceUnit = field.getAnnotation(PersistenceUnit.class);
        if (persistenceUnit !=null) {
            writer.writeCharacters("    ");
            writer.writeStartElement("jpa", "unit", "http://aries.apache.org/xmlns/jpa/v1.1.0");
            writer.writeAttribute("unitname", persistenceUnit.unitName());
            writer.writeAttribute("property", field.getName());
            writer.writeEndElement();
            writer.writeCharacters("\n");
        }
    }
    
    private void writeProperty(XMLStreamWriter writer, Field field, Bean bean)
            throws XMLStreamException {
        writer.writeCharacters("    ");
        writer.writeEmptyElement("property");
        writer.writeAttribute("name", field.getName());
        if (bean != null) {
            writer.writeAttribute("ref", bean.id);
        } else {
            // Assume it is define in another manually created blueprint context with default name
            Named named = field.getAnnotation(Named.class);
            String destId = (named != null) ? named.value() : Bean.getBeanName(field.getType());   
            writer.writeAttribute("ref", destId);
        }
        writer.writeCharacters("\n");
    }
    
    private void writePropertyValue(XMLStreamWriter writer, String name, String value)
            throws XMLStreamException {
        writer.writeCharacters("    ");
        writer.writeEmptyElement("property");
        writer.writeAttribute("name", name);
        writer.writeAttribute("value", value);
        writer.writeCharacters("\n");
    }

}
