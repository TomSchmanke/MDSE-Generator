package util;

import de.arinir.XMI2MDSDMetamodellConverter.XMI2MDSDMetamodellConverter.Converter;
import de.arinir.mdsd.metamodell.MDSDMetamodell.UMLClassDiagramm;

import java.io.IOException;
import java.io.InputStream;

/**
 * Helper class to convert XML file to MDSDMetamodell object.
 *
 * @author Laura Schmidt
 * @version 1.0 Initial implementation
 */
public class XMLConverter {

    /**
     * Used to process a XML file representing a UML class diagram. The file is read and
     * transformed into the MDSDMetamodell.
     *
     * @param path Path to XML file. File needs to be stored in resources directory
     * @return Parsed XML file as UMLClassDiagramm.
     * @throws IOException Thrown when path is not valid.
     */
    public UMLClassDiagramm processXMLUMLFile(String path) throws IOException {
        try (InputStream s = XMLConverter.class.getResourceAsStream(path)) {
            Converter converter = new Converter();
            return converter.convert(s);
        }
    }
}
