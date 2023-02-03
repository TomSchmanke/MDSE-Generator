import de.arinir.mdsd.metamodell.MDSDMetamodell.UMLClassDiagramm;

import template_data.*;
import util.CreateProjectStructureAsJson;
import util.DataConverter;
import util.ProjectInitializer;
import util.TemplateResolver;
import util.XMLConverter;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Main {

    private static final String GROUP_ID_PART_1 = "de";
    private static final String GROUP_ID_PART_2 = "generator";
    private static final String GROUP_ID = GROUP_ID_PART_1 + "." + GROUP_ID_PART_2;
    private static final String ARTIFACT_ID = "generated-application";
    private static final String NAME = "generated-application";
    private static final String DESCRIPTION = "Generated basic build for Spring Boot";
    private static final String JAVA_VERSION = "17";
    private static final String SPRING_BOOT_VERSION = "3.0.2";
    private static final String PATH_TO_FILES = NAME + "/src/main/java/" + GROUP_ID_PART_1 + "/" + GROUP_ID_PART_2  + "/" + ARTIFACT_ID.replaceAll("-", "" )  + "/";

    public static void main(String[] args) throws IOException {
        //File file = new File ("./src/main");
        //CreateProjectStructureAsJson createProjectStructureAsJson = new CreateProjectStructureAsJson(file);

        List<String> dependencies = Arrays.asList("devtools", "web", "security", "data-jpa");

        ProjectInitializer projectInitializer = new ProjectInitializer();
        String nameOfZip = projectInitializer.loadGeneratedFilesFromSpringInitializer(GROUP_ID, ARTIFACT_ID, NAME,
                DESCRIPTION, JAVA_VERSION, SPRING_BOOT_VERSION, dependencies);

        if(nameOfZip == null) {
            return;
        }
        projectInitializer.unzipFile(nameOfZip, nameOfZip.substring(0, nameOfZip.length() - 3));
        projectInitializer.newDirectoryFromPath(PATH_TO_FILES, "controllers");
        projectInitializer.newDirectoryFromPath(PATH_TO_FILES, "entities");
        projectInitializer.newDirectoryFromPath(PATH_TO_FILES, "repositories");

        XMLConverter xmlConverter = new XMLConverter();
        UMLClassDiagramm diagram = xmlConverter.processXMLUMLFile("/Flottenmanagement.xml");

        DataConverter dataConverter = new DataConverter(diagram);
        DataModel dataModel = dataConverter.convertMDSDDiagramToDataModel();

        String packagePath = GROUP_ID + '.' + ARTIFACT_ID + '.';
        String packagePathControllers = packagePath + "controllers";
        String packagePathEntities = packagePath + "entities";
        String packagePathRepositories = packagePath + "repositories";

        TemplateResolver templateResolver = new TemplateResolver();
        List<String> generatedControllerFiles = templateResolver.createControllerFiles(dataModel.getControllerDataModels(), packagePathControllers, packagePathEntities, packagePathRepositories);
        List<String> generatedEntityFiles = templateResolver.createEntityFiles(dataModel.getEntityDataModels(), dataModel.getAssociationsDataModels(), packagePathEntities);
        List<String> generatedRepositoryFiles = templateResolver.createRepositoryFiles(dataModel.getRepositoryDataModels(), packagePathRepositories, packagePathEntities);

        System.out.println(generatedControllerFiles);
        System.out.println(generatedEntityFiles);
        System.out.println(generatedRepositoryFiles);
    }
}
