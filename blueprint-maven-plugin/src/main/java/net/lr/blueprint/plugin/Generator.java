package net.lr.blueprint.plugin;

import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.PersistenceUnit;
import javax.transaction.cdi.Transactional;
import javax.transaction.cdi.Transactional.TxType;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import net.lr.blueprint.plugin.model.Bean;
import net.lr.blueprint.plugin.model.Context;
import net.lr.blueprint.plugin.model.OsgiServiceBean;
import net.lr.blueprint.plugin.model.Property;
import net.lr.blueprint.plugin.model.PropertyWriter;

public class Generator implements PropertyWriter {
    private static final String NS_BLUEPRINT = "http://www.osgi.org/xmlns/blueprint/v1.0.0";
    private static final String NS_EXT = "http://aries.apache.org/blueprint/xmlns/blueprint-ext/v1.0.0";
    private static final String NS_JPA = "http://aries.apache.org/xmlns/jpa/v1.1.0";
    private static final String NS_TX = "http://aries.apache.org/xmlns/transactions/v1.1.0";

    private Context context;
    private XMLStreamWriter writer;
    Map<TxType, String> txTypeNames;

    public Generator(Context context, OutputStream os) throws XMLStreamException {
        this.context = context;
        this.txTypeNames = new HashMap<Transactional.TxType, String>();
        this.txTypeNames.put(TxType.REQUIRED, "Required");
        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        writer = factory.createXMLStreamWriter(os);
    }

    public void generate() {
        try {
            writer.writeStartDocument();
            writer.writeCharacters("\n");
            writeBlueprint();
            for (Bean bean : context.getBeans()) {
                writeBeanStart(bean);
                bean.writeProperties(this);
                writer.writeEndElement();
                writer.writeCharacters("\n");
            }
            
            writeServiceRefs();
            new OsgiServiceProviderWriter(writer).write(context.getBeans());
            
            writer.writeEndElement();
            writer.writeCharacters("\n");
            writer.writeEndDocument();
            writer.writeCharacters("\n");
            writer.close();
        } catch (XMLStreamException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private void writeBlueprint() throws XMLStreamException {
        writer.writeStartElement("blueprint");
        writer.writeDefaultNamespace(NS_BLUEPRINT);
        writer.writeNamespace("ext", NS_EXT);
        writer.writeNamespace("jpa", NS_JPA);
        writer.writeNamespace("tx", NS_TX);
        writer.writeCharacters("\n");
    }
    
    public void writeBeanStart(Bean bean) throws XMLStreamException {
        writer.writeStartElement("bean");
        writer.writeAttribute("id", bean.id);
        writer.writeAttribute("class", bean.clazz.getName());
        writer.writeAttribute("ext", NS_EXT, "field-injection", "true");
        if (bean.postConstruct != null) {
            writer.writeAttribute("init-method", bean.postConstruct);
        }
        if (bean.preDestroy != null) {
            writer.writeAttribute("destroy-method", bean.preDestroy);
        }
        writer.writeCharacters("\n");
        writeTransactional(bean.clazz);

        if (bean.persistenceUnitField != null) {
            writePersistenceUnit(bean.persistenceUnitField);
        }
    }
    
    private void writeTransactional(Class<?> clazz)
            throws XMLStreamException {
        Transactional transactional = clazz.getAnnotation(Transactional.class);
        if (transactional != null) {
            writer.writeCharacters("    ");
            writer.writeEmptyElement("tx", "transaction", NS_TX);
            writer.writeAttribute("method", "*");
            writer.writeAttribute("value", txTypeNames.get(transactional.value()));
            writer.writeCharacters("\n");
        }
    }

    private void writePersistenceUnit(Field field) throws XMLStreamException {
        PersistenceUnit persistenceUnit = field.getAnnotation(PersistenceUnit.class);
        if (persistenceUnit !=null) {
            writer.writeCharacters("    ");
            writer.writeEmptyElement("jpa", "context", NS_JPA);
            writer.writeAttribute("unitname", persistenceUnit.unitName());
            writer.writeAttribute("property", field.getName());
            writer.writeCharacters("\n");
        }
    }
    
    private void writeServiceRefs() throws XMLStreamException {
        for (OsgiServiceBean serviceBean : context.getServiceRefs()) {
            writeServiceRef(serviceBean);
        }
    }

    private void writeServiceRef(OsgiServiceBean serviceBean) throws XMLStreamException {
        writer.writeEmptyElement("reference");
        writer.writeAttribute("id", serviceBean.id);
        writer.writeAttribute("interface", serviceBean.clazz.getName());
        if (serviceBean.filter != null && !"".equals(serviceBean.filter)) {
            writer.writeAttribute("filter", serviceBean.filter);
        }
        writer.writeCharacters("\n");
    }

    @Override
    public void writeProperty(Property property) {
        try {
            writer.writeCharacters("    ");
            writer.writeEmptyElement("property");
            writer.writeAttribute("name", property.name);
            if (property.ref != null) {
                writer.writeAttribute("ref", property.ref);
            } else if (property.value != null) {
                writer.writeAttribute("value", property.value);
            }
            writer.writeCharacters("\n");
        } catch (XMLStreamException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}
