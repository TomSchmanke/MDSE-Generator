import com.fasterxml.jackson.databind.ObjectMapper;
import de.arinir.mdsd.metamodell.MDSDMetamodell.UMLClassDiagramm;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import template_data.DataModel;
import user_code_resolver.UserCodeResolver;
import user_code_resolver.UserFileWrapper;
import util.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author Tom Schmanke
 * @version 1.0 Create overall program schedule
 */
public class Main {
    private static final Logger log = LogManager.getLogger(Main.class);

    private static String GROUP_ID = "de.generated";
    private static String ARTIFACT_ID = "generated-application";
    private static String NAME = "generated-application";
    private static String DESCRIPTION = "Generated basic build for Spring Boot";

    public static void main(String[] args) throws IOException {
        log.info("Read command line arguments.");
        //////// Read CLI arguments ////////
        readArgs(args);
        log.info("Reading command line arguments successful.");


        //////// Declare variables based on default values or CLI arguments ////////
        String javaVersion = "17";
        String springBootVersion = "3.0.2";
        String pathToFiles = NAME + "/src/main/java/" + GROUP_ID.replaceAll("\\.", "/") + "/" + ARTIFACT_ID.replaceAll("-", "") + "/";

        String targetPathControllers = pathToFiles + "controllers";
        String targetPathEntities = pathToFiles + "entities";
        String targetPathRepositories = pathToFiles + "repositories";

        String basePath = NAME;
        String resourcesPath = NAME + "/src/main/resources";
        String generatorStandardFilesPath = "src/main/resources/standard_files";


        log.info("Start generating application with name: {}", NAME);
        //////// Determine if the generator runs for the first time ////////
        Path generatedDirectory = Paths.get(NAME);
        boolean isFirstGeneration = Files.notExists(generatedDirectory);
        log.debug("There already is an directory with the name {}", isFirstGeneration);


        //////// Read the user code from the old project ////////
        ObjectMapper objectMapper = new ObjectMapper();
        UserFileWrapper userFileWrapper = objectMapper.readValue("{}", UserFileWrapper.class);
        UserCodeResolver userCodeResolver = new UserCodeResolver();
        File file = new File("./" + NAME);
        if (!isFirstGeneration) {
            log.info("Start updating 'old' project object based on the json ...");
            if (userCodeResolver.getFile() != null && userCodeResolver.getFile().length() > 0) {
                userFileWrapper = objectMapper.readValue(userCodeResolver.getFile(), UserFileWrapper.class);
            }
            log.info("Updating of the 'old' project object successful!");
            log.info("Start reading the user created code of the 'old' project ...");
            List<File> fileList = userCodeResolver.readStructureFromFolderAsList(file);
            log.info("Reading the user created code successful!");
            String contentOfFiles = userCodeResolver.readContentOfFilesAsString(fileList);
            log.info("Start writing the user created code into 'userCode.json'");
            userCodeResolver.writeStringToUserContent(contentOfFiles);
            log.info("Writing of the user created code successful!");
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
                DESCRIPTION, javaVersion, springBootVersion, dependencies);
        log.info("Generating Spring project successful!");

        //////// Unzip file ////////
        if (nameOfZip == null) {
            log.error("No zip file was downloaded from Spring Initializr");
            return;
        }
        log.info("Start unzipping file {} with Spring project from Spring Initializr ...", nameOfZip);
        projectInitializer.unzipFile(nameOfZip, nameOfZip.substring(0, nameOfZip.length() - 3));
        log.info("Unzipping file successful!");


        //////// Create directories for controllers, entities and repositories ////////
        log.info("Start creating directories for controllers, entities and repositories ...");
        projectInitializer.newDirectoryFromPath(pathToFiles, "controllers");
        projectInitializer.newDirectoryFromPath(pathToFiles, "entities");
        projectInitializer.newDirectoryFromPath(pathToFiles, "repositories");
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
        List<String> generatedControllerFiles = templateResolver.createControllerFiles(dataModel.getControllerDataModels(), packagePathControllers, packagePathEntities, packagePathRepositories, targetPathControllers);
        List<String> generatedEntityFiles = templateResolver.createEntityFiles(dataModel.getEntityDataModels(), dataModel.getAssociationsDataModels(), packagePathEntities, targetPathEntities);
        List<String> generatedRepositoryFiles = templateResolver.createRepositoryFiles(dataModel.getRepositoryDataModels(), packagePathRepositories, packagePathEntities, targetPathRepositories);
        List<String> applicationPropertiesFile = templateResolver.createApplicationProperties(ARTIFACT_ID, resourcesPath);
        List<String> readMeFile = templateResolver.createReadMe(ARTIFACT_ID, basePath);
        List<String> generatedFiles = new ArrayList<>();
        Stream.of(generatedControllerFiles, generatedEntityFiles, generatedRepositoryFiles, applicationPropertiesFile, readMeFile).forEach(generatedFiles::addAll);
        log.info("Fill template files successful! Generated files: {}", generatedFiles);


        //////// Copying basic files ////////
        log.info("Start copying editorconfig and gitignore files ...");
        FileCopier fileCopier = new FileCopier();
        fileCopier.copyFile(generatorStandardFilesPath + "/template-editorconfig", basePath + "/.editorconfig");
        fileCopier.copyFile(generatorStandardFilesPath + "/template-gitignore", basePath + "/.gitignore");
        log.info("Copy successful!");

        //////// Copying user generated code to new project ////////
        log.info("Start adding the user code to the 'new' project ...");
        userCodeResolver.writeUserContentInNewProject(userFileWrapper, file);
        log.info("Adding the user code to the 'new' project successful!");

        log.info("Generating application {} was successful!", NAME);
    }

    /**
     * Reads the Java command line arguments. Required order is: groupId, artifactId, name, description.
     * If there are no CLI arguments the default values are used. If the number of arguments isn't exactly
     * four the defaults are used.
     *
     * @param args Java CLI arguments
     */
    private static void readArgs(String[] args) {
        if (args.length == 4) {
            GROUP_ID = args[0];
            ARTIFACT_ID = args[1];
            NAME = args[2];
            DESCRIPTION = args[3];
        }
    }
}
