package net.lr.blueprint.plugin;

import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.PersistenceUnit;
import javax.transaction.cdi.Transactional;
import javax.transaction.cdi.Transactional.TxType;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.xbean.finder.ClassFinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

public class Generator {
    Set<Bean> availableBeans;

    private String[] packageNames;
    private ClassFinder finder;
    
    Map<TxType, String> txTypeNames;
    Map<Class<?>, Bean> refs;

    private Set<Class<?>> missing;

    public Generator(ClassFinder finder, String... packageNames) {
        this.finder = finder;
        this.packageNames = packageNames;
        this.availableBeans = new HashSet<>();
        this.refs = new HashMap<>();
        this.txTypeNames = new HashMap<Transactional.TxType, String>();
        this.txTypeNames.put(TxType.REQUIRED, "Required");
        this.missing = new HashSet<>();
    }

    public void generate(OutputStream os) {
        List<Class<?>> rawClasses = finder.findAnnotatedClasses(Component.class);
        List<Class<?>> beanClasses = filterByBasePackages(rawClasses, packageNames);
        addToAvailable(beanClasses);
        resolveRefs(beanClasses);

        try {
            XMLOutputFactory factory = XMLOutputFactory.newInstance();
            XMLStreamWriter writer = factory.createXMLStreamWriter(os);
            XMLStreamWriter outWriter = factory.createXMLStreamWriter(System.out);
            writer.writeStartDocument();
            writer.writeStartElement("blueprint");
            writer.writeCharacters("\n");
            for (Class<?> clazz : beanClasses) {
                writeBean(writer, clazz);
            }
            
            writeRefsForMissingBeans(outWriter, missing);
            
            writer.writeEndElement();
            writer.writeEndDocument();
            writer.close();
            System.out.println();
            System.out.println("Missing:");
            System.out.println(missing);
            
//            Set<Class<?>> omitted = getDiff(availableBeans, new HashSet<Class<?>>(refs.values()));
//            System.out.println("Unreferenced:");
//            System.out.println(omitted);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    private void addToAvailable(List<Class<?>> beanClasses) {
        for (Class<?> clazz : beanClasses) {
            availableBeans.add(new Bean(clazz));
        }
    }
    
    private void resolveRefs(List<Class<?>> classes) {
        for (Class<?> clazz : classes) {
            addRefs(clazz);
        }
    }

    private void addRefs(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            Autowired inject = field.getAnnotation(Autowired.class);
            if (inject != null) {
                Bean bean = getMatching(field.getType());
                if (bean != null) {
                    refs.put(field.getType(), bean);
                } else {
                    missing.add(field.getType());
                }
            }
        }
    }
    
    private Bean getMatching(Class<?> destClazz) {
        for (Bean bean : availableBeans) {
            Class<?> beanClass = bean.clazz;
            if (destClazz.isAssignableFrom(beanClass)) {
                return bean;
            }
        }
        return null;
    }

    private List<Class<?>> filterByBasePackages(List<Class<?>> rawClasses, String[] packageNames) {
        List<Class<?>> filteredClasses = new ArrayList<Class<?>>();
        for (Class<?> clazz : rawClasses) {
            for (String packageName : packageNames) {
                if (clazz.getName().startsWith(packageName)) {
                    filteredClasses.add(clazz);
                    continue;
                }
            }
        }
        return filteredClasses;
    }

    private void writeRefsForMissingBeans(XMLStreamWriter writer,
            Set<Class<?>> missingBeans) throws XMLStreamException {
        Iterator<Class<?>> missingIt = missingBeans.iterator();
        while (missingIt.hasNext()) {
            Class<?> missing = missingIt.next();
            if (missing.isInterface()) {
                missingIt.remove();
                writer.writeEmptyElement("reference");
                writer.writeAttribute("id", Bean.getBeanName(missing));
                writer.writeAttribute("interface", missing.getName());
                writer.writeCharacters("\n");
            }
        }
    }
    
    private void writeBean(XMLStreamWriter writer, Class<?> clazz)
            throws XMLStreamException {
        writer.writeStartElement("bean");
        writer.writeAttribute("id", Bean.getBeanName(clazz));
        writer.writeAttribute("class", clazz.getName());
        writer.writeCharacters("\n");
        writeTransactional(writer, clazz);
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            writePersistenceUnit(writer, field);
        }
        for (Field field : fields) {
            Autowired autowired = field.getAnnotation(Autowired.class);
            if (autowired != null) {
                writeProperty(writer, field.getName(), field.getType());
            }
            Value value = field.getAnnotation(Value.class);
            if (value != null) {
                writePropertyValue(writer, field.getName(), value.value());
            }
        }
        writer.writeEndElement();
        writer.writeCharacters("\n");
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
    
    private void writeProperty(XMLStreamWriter writer, String name, Class<?> type)
            throws XMLStreamException {
        writer.writeCharacters("    ");
        writer.writeEmptyElement("property");
        writer.writeAttribute("name", name);
        if (refs.containsKey(type)) {
            writer.writeAttribute("ref", refs.get(type).id);
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