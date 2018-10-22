// License: GPL. For details, see LICENSE file.
package org.openstreetmap.josm.data.imagery;

import java.io.StringWriter;
import java.net.MalformedURLException;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.sun.org.apache.xerces.internal.jaxp.validation.XMLSchemaFactory;

public class ImageryWriter {


    private static final String MAPS_XSD_NS = "http://josm.openstreetmap.de/maps-1.0";

    public static String toString(List<ImageryInfo> imageries) throws MalformedURLException, SAXException, ParserConfigurationException, TransformerConfigurationException, TransformerException, TransformerFactoryConfigurationError {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        Schema mapsXsd = XMLSchemaFactory
                .newInstance("http://www.w3.org/2001/XMLSchema")
                .newSchema(
                        new StreamSource(ImageryWriter.class.getClassLoader().getResourceAsStream("data/maps.xsd"))
                );
        builderFactory.setSchema(mapsXsd);
        builderFactory.setValidating(true);
        Document root = builderFactory.newDocumentBuilder().newDocument();
        Element el = root.createElementNS(MAPS_XSD_NS, "imagery");
        root.appendChild(el);
        for (ImageryInfo entry: imageries) {
            el.appendChild(new EntrySerializer(root, entry).getEntry());
        }
        return documentToString(root);
    }

    public static String documentToString(Document doc) throws TransformerConfigurationException, TransformerException, TransformerFactoryConfigurationError {
        DOMSource source = new DOMSource(doc);
        StringWriter result = new StringWriter();
        TransformerFactory.newInstance().newTransformer().transform(source, new StreamResult(result));
        return result.toString();
    }

    static class EntrySerializer {
        private final Document parent;
        private final ImageryInfo info;

        EntrySerializer(Document parent, ImageryInfo info) {
            this.parent = parent;
            this.info = info;
        }

        Element getEntry() {
            Element ret = parent.createElementNS(MAPS_XSD_NS, "entry");
            ret.appendChild(getName());
            ret.appendChild(getId());
            ret.appendChild(getCategory());
            return ret;
        }

        private Element getName() {
            Element name = parent.createElementNS(MAPS_XSD_NS, "name");
            name.setNodeValue(info.getName());
            return name;
        }

        private Element getId() {
            Element id = parent.createElementNS(MAPS_XSD_NS, "id");
            id.setNodeValue(info.getId());
            return id;
        }
        private Element getCategory() {
            Element category = parent.createElementNS(MAPS_XSD_NS, "category");
            category.setNodeValue(info.getImageryCategoryOriginalString());
            return category;
        }

        private Element getDate() {
            Element date = parent.createElementNS(MAPS_XSD_NS, "date");
            date.setNodeValue(info.getDate());
            return date;
        }

        private Element getDescription() {
            Element description = parent.createElementNS(MAPS_XSD_NS, "description");
            description.setNodeValue(info.getDescription());
            description.setAttribute("lang", info.getDescriptionLanguage());
            return description;
        }

        private Element getType() {
            Element type = parent.createElementNS(MAPS_XSD_NS, "type");
            type.setNodeValue(info.getImageryType().getTypeString());
            return type;
        }

        private Element getUrl() {
            Element url = parent.createElementNS(MAPS_XSD_NS, "url");
            url.setNodeValue(info.getUrl());
            return url;
        }

        private Element getAttributionText() {
            Element ret = parent.createElementNS(MAPS_XSD_NS, "attribution-text");
            ret.setNodeValue(info.attributionText);
        }
    }
}
