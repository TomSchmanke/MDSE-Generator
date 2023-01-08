import de.arinir.mdsd.metamodell.MDSDMetamodell.UMLClassDiagramm;

import template_data.*;
import util.DataConverter;
import util.ProjectInitializer;
import util.TemplateResolver;
import util.XMLConverter;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        ProjectInitializer projectInitializer = new ProjectInitializer();
        String nameOfZip = projectInitializer.loadGeneratedFilesFromSpringInitializer();

        if(nameOfZip == null) {
            return;
        }
        projectInitializer.unzipFile(nameOfZip, nameOfZip.substring(0, nameOfZip.length() - 3));

        XMLConverter xmlConverter = new XMLConverter();
        UMLClassDiagramm diagram = xmlConverter.processXMLUMLFile("/Flottenmanagement.xml");

        DataConverter dataConverter = new DataConverter(diagram);
        DataModel dataModel = dataConverter.convertMDSDDiagramToDataModel();

        TemplateResolver templateResolver = new TemplateResolver();
        templateResolver.setDataModel(dataModel);

        templateResolver.createControllerFiles();
        templateResolver.createEntityFiles();
        templateResolver.createRepositoryFiles();
    }
}
