package user_code_resolver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.Change;
import org.javers.core.diff.Diff;
import org.javers.core.diff.ListCompareAlgorithm;
import org.javers.core.diff.changetype.container.ContainerElementChange;
import org.javers.core.diff.changetype.container.ElementValueChange;
import org.javers.core.diff.changetype.container.ListChange;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * class to save user code between generations
 *
 * @author Jonas Knebel
 * @version 1.1 added support to save images between versions
 */
public class UserCodeResolver {


    private final File file = new File("userCode.json");
    private static final Logger logger = LogManager.getLogger(UserCodeResolver.class);

    public File getFile() {
        return file;
    }

    /**
     * Instantiates a new User code resolver.
     */
    public UserCodeResolver() {
    }

    /**
     * Write the JSOn Object in the new Project
     *
     * @param project
     * @param folder
     * @throws IOException
     */
    public void writeUserContentInNewProject(Project project, File folder) throws IOException {
        if (project.getFiles().size() > 0) {
            updateNamesOfImplFiles(project, folder);
        }

        for (UserFile file : project.getFiles()) {
            Path path = Paths.get(file.getFilename());
            if (Files.notExists(path)) {
                Files.createFile(path);
            }
            if (file.getFilename().endsWith(".jpg") || file.getFilename().endsWith(".jpeg") || file.getFilename().endsWith(".png") || file.getFilename().endsWith(".gif")) {
                byte[] decodedBytes = Base64.getDecoder().decode(file.getContent().get(0));
                try (FileOutputStream outputStream = new FileOutputStream(file.getFilename())) {
                    outputStream.write(decodedBytes);
                } catch (IOException e) {
                    logger.error("An error occurred while writing the file: " + e.getMessage());
                }
            } else {
                try (FileWriter writer = new FileWriter(file.getFilename())) {
                    for (int i = 0; i < file.getContent().size(); i++) {
                        writer.write(file.getContent().get(i) + System.lineSeparator());
                    }
                } catch (IOException e) {
                    logger.error("An error occurred while writing the file: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Read structure from folder as list
     *
     * @param folder the folder
     * @return the list
     * @throws IOException the io exception
     */
    public List<File> readStructureFromFolderAsList(final File folder) throws IOException {
        List<File> filePaths = new ArrayList<>();
        String path = String.valueOf(folder);
        try (Stream<Path> paths = Files.walk(Paths.get(path))) {
            paths.filter(Files::isRegularFile).map(Path::toFile).forEach(file -> {
                if (!file.toString().endsWith("Gen.java") && !file.toString().endsWith(".jar")) {
                    filePaths.add(file);
                }
            });
        }
        return filePaths;
    }

    /**
     * Read content of files and map as string.
     *
     * @param fileList the file paths
     * @return the string
     */
    public String readContentOfFilesAsString(List<File> fileList) throws JsonProcessingException {
        Project project = fileList.stream().map(file -> {
            UserFile userFile = new UserFile();
            userFile.setFilename(String.valueOf(file));
            try {
                //////// encode image as Base64 and set content of UserFile  ////////
                if (file.toString().matches(".*\\.(jpg|jpeg|png|gif)$")) {
                    userFile.setContent(Collections.singletonList(encodeImageToBase64(file)));
                } else {
                    userFile.setContent(Files.readAllLines(file.toPath()));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return userFile;
        }).collect(Collectors.collectingAndThen(Collectors.toList(), Project::new));

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(project);
    }

    /**
     * Returns the image fiel encoded as base64
     *
     * @param file
     * @return image encoded as base64
     * @throws IOException
     */
    private static String encodeImageToBase64(File file) throws IOException {
        byte[] imageBytes = Files.readAllBytes(Paths.get(file.getPath()));
        return Base64.getEncoder().encodeToString(imageBytes);
    }


    /**
     * Write the JsonString to "UserCode.json"
     *
     * @param JsonAsString the root node as string
     * @throws IOException the io exception
     */
    public void writeStringToUserContent(String JsonAsString) throws IOException {
        FileWriter fileWriter = new FileWriter(this.file, false);
        fileWriter.write(JsonAsString);
        fileWriter.close();
    }


    /**
     * This method checks for name changes in the new project and updates the impl files saved in the userCode.json based on that
     *
     * @param project
     * @param folder
     * @return the project with updated names of impl Files
     * @throws IOException
     */
    private Project updateNamesOfImplFiles(Project project, File folder) throws IOException {
        List<String> projectStructureList = new ArrayList<>();
        List<File> fileList = readStructureFromFolderAsList(folder);

        project.getFiles().forEach(file -> projectStructureList.add(file.getFilename()));
        Diff diff = compareStructure(projectStructureList, convertNewProjectStructureToList(fileList));
        //////// Get all elementValueChanges that are of Type ListChanges and update the project based on them ////////
        for (Change change : diff.getChanges()) {
            if (change instanceof ListChange listChange) {
                logger.info("The following structural changes have been found:");
                for (ContainerElementChange element : listChange.getChanges()) {
                    if (element instanceof ElementValueChange elementValueChange) {
                        logger.info(elementValueChange);
                        project = updateFilesBasedOnElementValueChange(elementValueChange, project);
                    }
                }
            }
        }
        return project;
    }

    /**
     * Converts the file list to a string list
     *
     * @param fileList
     * @return fileList converted to a string list
     */
    private List<String> convertNewProjectStructureToList(List<File> fileList) {
        return fileList.stream().map(String::valueOf).collect(Collectors.toList());
    }

    /**
     * This method updates the Impl files based on the ElementValueChange Object
     *
     * @param elementValueChange
     * @param project
     * @return updated project object
     */
    private Project updateFilesBasedOnElementValueChange(ElementValueChange elementValueChange, Project project) {
        //////// Get all relevant values from the ElementValue Change object ////////
        String oldPath = elementValueChange.getLeftValue().toString();
        String newPath = elementValueChange.getRightValue().toString();
        String[] oldPathArray = oldPath.split("\\\\");
        String[] newPathArray = newPath.split("\\\\");
        int fileTypeStart = oldPathArray[oldPathArray.length - 1].indexOf(".");
        String oldConstructorName = oldPathArray[oldPathArray.length - 1].substring(0, fileTypeStart);
        String newConstructorName = newPathArray[oldPathArray.length - 1].substring(0, fileTypeStart);

        //////// Find and update the relevant file with the new project  ////////
        for (UserFile file : project.getFiles()) {
            if (file.getFilename() == elementValueChange.getLeftValue()) {
                file.setFilename(newPath);
                for (int i = 0; i < file.getContent().size(); i++) {
                    String line = file.getContent().get(i);
                    if (line == null) continue;
                    if (line.contains("package")) {
                        line = getNewPackageName(oldPathArray);
                        file.getContent().set(i, line);
                    }
                    if (line.contains("class ") || line.contains(oldConstructorName)) {
                        file.getContent().set(i, line.replace(oldConstructorName, newConstructorName));
                    }
                }
                //TODO check if elementVlaueChange only has 1 value if yes add break;
            }
        }
        return project;
    }

    /**
     * Returns the new PackageName based on the new structure
     *
     * @param oldPathArray
     * @return newPackgeName
     */
    private String getNewPackageName(String[] oldPathArray) {
        String newPackage = oldPathArray[oldPathArray.length - 2];
        for (int arrayIndex = oldPathArray.length - 3; arrayIndex >= 0; arrayIndex--) {
            if (!oldPathArray[arrayIndex].equals("java")) {
                newPackage = oldPathArray[arrayIndex] + "." + newPackage;
            } else break;
        }
        return "package " + newPackage + ";";
    }

    /**
     * Compare structure diff.
     *
     * @param oldFolderStructure the old folderStructure
     * @param newFolderStructure the new folderStructure
     * @return the diff
     */
    private Diff compareStructure(List<String> oldFolderStructure, List<String> newFolderStructure) {
        Javers javers = JaversBuilder.javers().withListCompareAlgorithm(ListCompareAlgorithm.LEVENSHTEIN_DISTANCE).build();
        return javers.compare(oldFolderStructure, newFolderStructure);
    }
}




