package bjs.task32;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by U-1 on 17.01.2016.
 */
public class ComputersStoreLoader{
    /**Object which presents hierarchy for whole XML document*/
    private ComputersStore computersStore;

    /**Reference to the event reader*/
    XMLEventReader eventReader;

    public ComputersStoreLoader (ComputersStore computersStore) {
        this.computersStore = computersStore;
    }

    /**
     * Parses XML document
     * @param xmlFile Path to the XML file
     * @return True if successfully parsed
     */
    public boolean parse(String xmlFile) {
        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        FileInputStream fileStream = null;

        try {
            fileStream = new FileInputStream(xmlFile);
            eventReader = xmlInputFactory.createXMLEventReader(fileStream);

            getComputerCatalogs();
        }
        catch (Exception e ) {
            System.out.println("Failed to parse XML. " + e);
            return false;
        }
        finally {
            try {
                if (fileStream != null) {
                    fileStream.close();
                }
            }
            catch(IOException e) {
                //closing quietly
            }

            try {
                if (eventReader != null) {
                    eventReader.close();
                }
            }
            catch(XMLStreamException e) {
                //closing quietly
            }
        }

        return true;
    }

    /**
     * Loads all "catalog" entities
     */
    protected void getComputerCatalogs() throws XMLStreamException {
        ComputersCatalog computersCatalog = null;
        ComputerType computerType = null;

        while(eventReader.hasNext()) {
            XMLEvent xmlEvent = eventReader.nextEvent();

            //Check if event is the start element.
            if (xmlEvent.isStartElement()) {
                StartElement startElement = xmlEvent.asStartElement();
                String elementName = startElement.getName().getLocalPart();

                if (elementName.equalsIgnoreCase("catalogcomputers")) {
                    int id = Integer.parseInt(startElement.getAttributeByName(new QName("id")).getValue());
                    computersCatalog = new ComputersCatalog(id);
                }

                if (elementName.equalsIgnoreCase("computer")) {
                    if (computersCatalog == null) {
                        throw new XMLStreamException("\"computer\" tag not expected here.");
                    }

                    int id = Integer.parseInt(startElement.getAttributeByName(new QName("id")).getValue());
                    computerType = new ComputerType(id);
                }

                xmlEvent = setComputerType(startElement, computerType);
            }

            //Check if event is the end element.
            if (xmlEvent.isEndElement()) {
                EndElement endElement = xmlEvent.asEndElement();

                if (endElement.getName().getLocalPart().equalsIgnoreCase("catalogcomputers")) {
                    computersStore.setCatalog(computersCatalog);
                    computersCatalog = null;
                }

                if (endElement.getName().getLocalPart().equalsIgnoreCase("computer")) {
                    computersCatalog.putComputerType(computerType);
                    computerType = null;
                }
            }
        }
    }

    /**
     * Fills computer type object
     * @param startElement Start element object
     * @param computerType Computer type object
     * @return Current XML event
     * @throws XMLStreamException
     */
    protected XMLEvent setComputerType(StartElement startElement, ComputerType computerType) throws XMLStreamException {
        XMLEvent xmlEvent = startElement;
        String elementName = startElement.getName().getLocalPart();

        if (elementName.equalsIgnoreCase("title")) {
            if (computerType == null) {
                throw new XMLStreamException("\"title\" tag not expected here.");
            }

            xmlEvent = eventReader.nextEvent();
            computerType.setTitle(xmlEvent.asCharacters().getData());
        }

        if (elementName.equalsIgnoreCase("type")) {
            if (computerType == null) {
                throw new XMLStreamException("\"type\" tag not expected here.");
            }

            xmlEvent = eventReader.nextEvent();
            computerType.setType(xmlEvent.asCharacters().getData());
        }

        if (elementName.equalsIgnoreCase("amount")) {
            if (computerType == null) {
                throw new XMLStreamException("\"amount\" tag not expected here.");
            }

            xmlEvent = eventReader.nextEvent();
            computerType.setAmount(Integer.parseInt(xmlEvent.asCharacters().getData()));
        }

        return xmlEvent;
    }
}
