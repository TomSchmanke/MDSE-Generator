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

    public static void main(String[] args) throws IOException {
        //File file = new File ("./src/main");
        //CreateProjectStructureAsJson createProjectStructureAsJson = new CreateProjectStructureAsJson(file);

        String groupIdPart1 = "de";
        String groupIdPart2 = "generator";
        String groupId = groupIdPart1 + '.' + groupIdPart2;
        String artifactId = "generated-application";
        String name = "generated-application";
        String description = "Generated basic build for Spring Boot";
        String javaVersion = "17";
        String bootVersion = "3.0.2";
        List<String> dependencies = Arrays.asList("devtools", "web", "security", "data-jpa");

        String pathToFiles = name + "/src/main/java/" + groupIdPart1 + "/" + groupIdPart2  + "/" + artifactId.replaceAll("-", "" )  + "/";

        ProjectInitializer projectInitializer = new ProjectInitializer();
        String nameOfZip = projectInitializer.loadGeneratedFilesFromSpringInitializer(groupId, artifactId, name,
                description, javaVersion, bootVersion, dependencies);

        if(nameOfZip == null) {
            return;
        }
        projectInitializer.unzipFile(nameOfZip, nameOfZip.substring(0, nameOfZip.length() - 3));
        projectInitializer.newDirectoryFromPath(pathToFiles, "controllers");
        projectInitializer.newDirectoryFromPath(pathToFiles, "entities");
        projectInitializer.newDirectoryFromPath(pathToFiles, "repositories");

        XMLConverter xmlConverter = new XMLConverter();
        UMLClassDiagramm diagram = xmlConverter.processXMLUMLFile("/Flottenmanagement.xml");

        DataConverter dataConverter = new DataConverter(diagram);
        DataModel dataModel = dataConverter.convertMDSDDiagramToDataModel();

//        printDataModel(dataModel);

        String packagePath = groupId + '.' + artifactId + '.';
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

    // TODO: only for test/debug purposes, delete later
    private static void printDataModel(DataModel dataModel) {
        dataModel.getEntityDataModels().forEach(e -> {
            System.out.println("Entitiy: " + e.getEntityName());
            System.out.println("Attributes: ");
            e.getAttributeDataModels().forEach(a -> {
                System.out.println("    " + a.getAttributeName() + ", Type: " + a.getAttributeType());
            });
            System.out.println();
        });
    }
}
