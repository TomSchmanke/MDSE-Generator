import de.arinir.mdsd.metamodell.MDSDMetamodell.UMLClassDiagramm;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import template_data.*;
import util.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.*;
import java.util.stream.Stream;

/**
 * @author Tom Schmanke
 * @version 1.0 Create overall program schedule
 */
public class Main {
    private static final Logger log = LogManager.getLogger(Main.class);

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

        log.info("Start generating application with name: {}", NAME);

        //////// Determine if the generator runs for the first time ////////
        Path generatedDirectory = Paths.get(NAME);
        boolean isFirstGeneration = Files.notExists(generatedDirectory);
        log.debug("There already is an directory with the name {}", isFirstGeneration);


        //////// Read the user code from the old project ////////
        if (!isFirstGeneration) {
            log.info("Start reading the user created code of the 'old' project ...");
            // ToDo
            log.info("Reading the user created code successful!");
        }


        //////// Rename the old project to create a new project later on ////////
        if (!isFirstGeneration) {
            long timestamp = Instant.now().getEpochSecond();
            log.info("Renaming the old project by concatenating the timestamp {} to the root directory", timestamp);
            File oldDirectory = new File(NAME);
            File newDirectory = new File(NAME + timestamp);
            boolean renameFlag = oldDirectory.renameTo(newDirectory);
            log.info("Renaming the old project successful? {}", renameFlag);
        }


        //////// Download zip with initial project structure from Spring Initializr ////////
        log.info("Start generating Spring project from Spring Initializr ...");
        ProjectInitializer projectInitializer = new ProjectInitializer();
        List<String> dependencies = Arrays.asList("devtools", "web", "data-jpa", "h2", "lombok");
        String nameOfZip = projectInitializer.loadGeneratedFilesFromSpringInitializer(GROUP_ID, ARTIFACT_ID, NAME,
                DESCRIPTION, JAVA_VERSION, SPRING_BOOT_VERSION, dependencies);
        log.info("Generating Spring project successful!");

        if(nameOfZip == null) {
            return;
        }
        log.info("Start unzipping file with Spring project from Spring Initializr ...");
        projectInitializer.unzipFile(nameOfZip, nameOfZip.substring(0, nameOfZip.length() - 3));
        log.info("Unzipping file successful!");


        //////// Create directories for controllers, entities and repositories ////////
        log.info("Start creating directories for controllers, entities and repositories ...");
        projectInitializer.newDirectoryFromPath(PATH_TO_FILES, "controllers");
        projectInitializer.newDirectoryFromPath(PATH_TO_FILES, "entities");
        projectInitializer.newDirectoryFromPath(PATH_TO_FILES, "repositories");
        log.info("Creating directories successful!");


        //////// Converting data from XML-File to our data model ////////
        log.info("Start reading XML-file with UML definitions ...");
        XMLConverter xmlConverter = new XMLConverter();
        UMLClassDiagramm diagram = xmlConverter.processXMLUMLFile("/Flottenmanagement.xml");
        log.info("Process XML-file successful!");

        log.info("Start converting MDSDDiagram to data model ...");
        DataConverter dataConverter = new DataConverter(diagram);
        DataModel dataModel = dataConverter.convertMDSDDiagramToDataModel();
        log.info("Convert to data model successful!");


        //////// Generating files from templates ////////
        log.info("Start filling templated with application specific data ...");
        String packagePath = GROUP_ID.replaceAll("-", "") + '.' + ARTIFACT_ID.replaceAll("-", "") + '.';
        String packagePathControllers = packagePath + "controllers";
        String packagePathEntities = packagePath + "entities";
        String packagePathRepositories = packagePath + "repositories";

        TemplateResolver templateResolver = new TemplateResolver();
        List<String> generatedControllerFiles = templateResolver.createControllerFiles(dataModel.getControllerDataModels(), packagePathControllers, packagePathEntities, packagePathRepositories, TARGET_PATH_CONTROLLERS);
        List<String> generatedEntityFiles = templateResolver.createEntityFiles(dataModel.getEntityDataModels(), dataModel.getAssociationsDataModels(), packagePathEntities, TARGET_PATH_ENTITIES);
        List<String> generatedRepositoryFiles = templateResolver.createRepositoryFiles(dataModel.getRepositoryDataModels(), packagePathRepositories, packagePathEntities, TARGET_PATH_REPOSITORIES);
        List<String> applicationPropertiesFile = templateResolver.createApplicationProperties(ARTIFACT_ID, RESOURCES_PATH);
        List<String> readMeFile = templateResolver.createReadMe(ARTIFACT_ID, BASE_PATH);
        List<String> generatedFiles = new ArrayList<>();
        Stream.of(generatedControllerFiles, generatedEntityFiles, generatedRepositoryFiles, applicationPropertiesFile, readMeFile).forEach(generatedFiles::addAll);
        log.info("Fill template files successful! Generated files: {}", generatedFiles);


        //////// Copying basic files ////////
        log.info("Start copying editorconfig and gitignore files ...");
        FileCopier fileCopier = new FileCopier();
        fileCopier.copyFile(GENERATOR_STANDARD_FILES_PATH + "/template-editorconfig", BASE_PATH + "/.editorconfig");
        fileCopier.copyFile(GENERATOR_STANDARD_FILES_PATH + "/template-gitignore", BASE_PATH + "/.gitignore");
        log.info("Copy successful!");


        //////// Copying user generated code to new project ////////
        log.info("Start adding the user code to the 'new' project ...");
        // ToDO
        log.info("Adding the user code to the 'new' project successful!");


        //////// Show errors to the user ////////
        log.info("Start showing the errors to user ...");
        // ToDO
        log.info("Showing the errors to user successful!");


        log.info("Generating application {} was successful!", NAME);
    }
}
