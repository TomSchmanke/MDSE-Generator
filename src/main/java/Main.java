import user_code_resolver.UserCodeResolver;
import de.arinir.mdsd.metamodell.MDSDMetamodell.UMLClassDiagramm;

import template_data.*;
import util.*;

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
    private static final String PATH_TO_FILES = NAME + "/src/main/java/" + GROUP_ID_PART_1 + "/" + GROUP_ID_PART_2 + "/" + ARTIFACT_ID.replaceAll("-", "") + "/";

    private static final String TARGET_PATH_CONTROLLERS = PATH_TO_FILES + "controllers";
    private static final String TARGET_PATH_ENTITIES = PATH_TO_FILES + "entities";
    private static final String TARGET_PATH_REPOSITORIES = PATH_TO_FILES + "repositories";

    private static final String BASE_PATH = NAME;
    private static final String RESOURCES_PATH = NAME + "/src/main/resources";
    private static final String GENERATOR_STANDARD_FILES_PATH = "src/main/resources/standard_files";

    public static void main(String[] args) throws IOException {
        //  File file = new File ("./src/main/java/util");
        // UserCodeResolver createProjectStructureAsJson = new UserCodeResolver(file);

        List<String> dependencies = Arrays.asList("devtools", "web", "data-jpa", "h2");

        ProjectInitializer projectInitializer = new ProjectInitializer();
        String nameOfZip = projectInitializer.loadGeneratedFilesFromSpringInitializer(GROUP_ID, ARTIFACT_ID, NAME,
                DESCRIPTION, JAVA_VERSION, SPRING_BOOT_VERSION, dependencies);

        if (nameOfZip == null) {
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


//        printDataModel(dataModel);

        String packagePath = GROUP_ID.replaceAll("-", "") + '.' + ARTIFACT_ID.replaceAll("-", "") + '.';
        String packagePathControllers = packagePath + "controllers";
        String packagePathEntities = packagePath + "entities";
        String packagePathRepositories = packagePath + "repositories";

        TemplateResolver templateResolver = new TemplateResolver();
        List<String> generatedControllerFiles = templateResolver.createControllerFiles(dataModel.getControllerDataModels(), packagePathControllers, packagePathEntities, packagePathRepositories, TARGET_PATH_CONTROLLERS);
        List<String> generatedEntityFiles = templateResolver.createEntityFiles(dataModel.getEntityDataModels(), dataModel.getAssociationsDataModels(), packagePathEntities, TARGET_PATH_ENTITIES);
        List<String> generatedRepositoryFiles = templateResolver.createRepositoryFiles(dataModel.getRepositoryDataModels(), packagePathRepositories, packagePathEntities, TARGET_PATH_REPOSITORIES);
        String applicationPropertiesFile = templateResolver.createApplicationProperties(ARTIFACT_ID, RESOURCES_PATH);
        String readMeFile = templateResolver.createReadMe(ARTIFACT_ID, BASE_PATH);


        System.out.println(generatedControllerFiles);
        System.out.println(generatedEntityFiles);
        System.out.println(generatedRepositoryFiles);
        System.out.println(applicationPropertiesFile);
        System.out.println(readMeFile);

        FileCopier fileCopier = new FileCopier();
        fileCopier.copyFile(GENERATOR_STANDARD_FILES_PATH + "/template-editorconfig", BASE_PATH + "/.editorconfig");
        fileCopier.copyFile(GENERATOR_STANDARD_FILES_PATH + "/template-gitignore", BASE_PATH + "/.gitignore");
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
