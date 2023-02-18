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
import java.util.stream.Stream;

/**
 * class to save usercode between generations
 *
 * @author Jonas Knebel
 * @version 2.0 improved Structure of file and fixed some errors
 */
public class UserCodeResolver {


    private File file = new File("userCode.json");
    private static final Logger logger = LogManager.getLogger(UserCodeResolver.class);

    public File getFile() {
        return file;
    }

    /**
     * Instantiates a new User code resolver.
     *
     * @throws IOException the io exception
     */
    public UserCodeResolver() {
    }


    public void writeUserContentInFiles(Project project, File folder) throws IOException {

        if (project.getFiles().size() > 0) {
            updateNamesOfImplFiles(project, folder);
        }

        for (UserFile file : project.getFiles()) {
            Path path = Paths.get(file.getFilename());
            if (Files.notExists(path)) {
                Files.createFile(path);
            }
            if (file.getFilename().endsWith(".jpg") || file.getFilename().endsWith(".jpeg") || file.getFilename().endsWith(".png") || file.getFilename().endsWith(".gif")) {
                List list = new ArrayList();
                byte[] decodedBytes = Base64.getDecoder().decode(file.getContent().get(0));
                list.add(decodedBytes);
                file.setContent(list);
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
     * Read content of files and map as string.7
     *
     * @param fileList the file paths
     * @return the string
     */
    public String readContentOfFilesAsString(List<File> fileList) throws JsonProcessingException {
        Project project = new Project();
        BufferedReader reader;
        ObjectMapper objectMapper = new ObjectMapper();
        for (File file : fileList) {
            try {
                String fileName = file.toString();
                UserFile userFile = new UserFile();
                userFile.setFilename(String.valueOf(file));
                if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png") || fileName.endsWith(".gif")) {
                    userFile.setContent(Collections.singletonList(encodeImageToBase64(file)));
                } else {
                    reader = new BufferedReader(new FileReader(file));
                    ArrayList<String> fileStringList = new ArrayList<>();
                    String line = reader.readLine();
                    fileStringList.add(line);
                    while (line != null) {
                        line = reader.readLine();
                        if (line != null) {
                            fileStringList.add(line);
                        }
                    }
                    reader.close();
                    userFile.setContent(fileStringList);
                }
                project.addFile(userFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(project);
    }

    public static String encodeImageToBase64(File file) throws IOException {
        byte[] imageBytes = Files.readAllBytes(Paths.get(file.getPath()));
        return Base64.getEncoder().encodeToString(imageBytes);
    }


    /**
     * Write string to file.
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
     * @param project
     * @param folder
     * @return
     * @throws IOException
     */
    public Project updateNamesOfImplFiles(Project project, File folder) throws IOException {
        List<String> projectStructureList = new ArrayList<>();
        List<File> fileList = readStructureFromFolderAsList(folder);
        for (UserFile userList : project.getFiles()) {
            projectStructureList.add(userList.getFilename());
        }
        Diff diff = compareStructure(projectStructureList, convertNewProjectStructureToList(fileList));
        for (Change change : diff.getChanges()) {
            if (change instanceof ListChange listChange) {
                for (ContainerElementChange element : listChange.getChanges()) {
                    if (element instanceof ElementValueChange elementValueChange) {
                        project = updateFilesBasedOnElementValueChange(elementValueChange, project);
                    }
                }
            }
        }
        return project;
    }

    /**
     * Convert new project structure to list<String>.
     *
     * @param fileList the file list
     * @return the list
     * @throws IOException the io exception
     */
    private List<String> convertNewProjectStructureToList(List<File> fileList) {
        List<String> fileListAsString = new ArrayList<>();
        for (File file : fileList) {
            fileListAsString.add(String.valueOf(file).replace("\\\\", "\\\\\\\\"));
        }
        return fileListAsString;
    }

    private Project updateFilesBasedOnElementValueChange(ElementValueChange elementValueChange, Project project) {
        String oldPath = elementValueChange.getLeftValue().toString();
        String newPath = elementValueChange.getRightValue().toString();
        String[] oldPathArray = oldPath.split("\\\\");
        String[] newPathArray = newPath.split("\\\\");
        int fileTypeStart = oldPathArray[oldPathArray.length - 1].indexOf(".");
        String oldConstructorName = oldPathArray[oldPathArray.length - 1].substring(0, fileTypeStart);
        String newConstructorName = newPathArray[oldPathArray.length - 1].substring(0, fileTypeStart);
        String newPackage = oldPathArray[oldPathArray.length - 2];
        for (UserFile file : project.getFiles()) {
            if (file.getFilename() == elementValueChange.getLeftValue()) {
                file.setFilename(newPath);
                for (int i = 0; i < file.getContent().size(); i++) {
                    String line = file.getContent().get(i);
                    if (line == null) continue;
                    if (line.contains("package")) {
                        for (int arrayIndex = oldPathArray.length - 3; arrayIndex >= 0; arrayIndex--) {
                            if (oldPathArray[arrayIndex].equals("java")) {
                                break;
                            } else {
                                newPackage = oldPathArray[arrayIndex] + "." + newPackage;
                            }
                        }
                        line = "package " + newPackage + ";";
                        file.getContent().set(i, line);
                    } else if (line.contains("class ") || line.contains(oldConstructorName)) {
                        file.getContent().set(i, line.replace(oldConstructorName, newConstructorName));
                    }
                }
            }
        }
        return project;
    }


    /**
     * Compare structure diff.
     *
     * @param oldFolderstructure the old folderstructure
     * @param newFolderstructure the new folderstructure
     * @return the diff
     */
    private Diff compareStructure(List<String> oldFolderstructure, List<String> newFolderstructure) {
        Javers javers = JaversBuilder.javers().withListCompareAlgorithm(ListCompareAlgorithm.LEVENSHTEIN_DISTANCE).build();
        return javers.compare(oldFolderstructure, newFolderstructure);
    }
}




