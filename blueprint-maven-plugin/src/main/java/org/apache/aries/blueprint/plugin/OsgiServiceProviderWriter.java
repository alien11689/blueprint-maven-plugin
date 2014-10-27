package org.apache.aries.blueprint.plugin;

import java.util.Collection;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.aries.blueprint.plugin.model.Bean;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;
import org.ops4j.pax.cdi.api.Properties;
import org.ops4j.pax.cdi.api.Property;

public class OsgiServiceProviderWriter {
    private XMLStreamWriter writer;

    public OsgiServiceProviderWriter(XMLStreamWriter writer) {
        this.writer = writer;
    }

    public void write(Collection<Bean> beans) throws XMLStreamException {
        for (Bean bean : beans) {
            write(bean);
        }
    }
    
    public void write(Bean bean) throws XMLStreamException {
        OsgiServiceProvider serviceProvider = bean.clazz.getAnnotation(OsgiServiceProvider.class);
        if (serviceProvider == null) {
            return;
        }
        if (serviceProvider.classes().length == 0) {
            throw new IllegalArgumentException("Need to provide the interface class in the @OsgiServiceProvider(classes={...}) annotation on " + bean.clazz);
        }
        Properties properties = bean.clazz.getAnnotation(Properties.class);
        if (properties == null) {
            writer.writeEmptyElement("service");
        } else {
            writer.writeStartElement("service");
        }
        writer.writeAttribute("ref", bean.id);
        Class<?> serviceIf = serviceProvider.classes()[0];
        writer.writeAttribute("interface", serviceIf.getName());
        writer.writeCharacters("\n");
        if (properties != null) {
            writeProperties(properties);
            writer.writeEndElement();
            writer.writeCharacters("\n");
        }
    }

    private void writeProperties(Properties properties) throws XMLStreamException {
        writer.writeCharacters("    ");
        writer.writeStartElement("service-properties");
        writer.writeCharacters("\n");
        for (Property property : properties.value()) {
            writer.writeCharacters("        ");
            writer.writeEmptyElement("entry");
            writer.writeAttribute("key", property.name());
            writer.writeAttribute("value", property.value());
            writer.writeCharacters("\n");
        }
        writer.writeCharacters("    ");
        writer.writeEndElement();
        writer.writeCharacters("\n");
    }


}
