/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.aries.blueprint.plugin;

import lombok.AllArgsConstructor;
import org.apache.aries.blueprint.plugin.model.Bean;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;
import org.ops4j.pax.cdi.api.Properties;
import org.ops4j.pax.cdi.api.Property;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.Collection;

@AllArgsConstructor
public class OsgiServiceProviderWriter {
    final private XMLStreamWriter writer;

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
