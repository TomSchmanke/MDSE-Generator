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

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * class to save user code between generations
 *
 * @author Jonas Knebel
 * @version 1.0 initial creation
 *
 * @author Jonas Knebel
 * @version 1.1 added support to save binary files between versions
 */
public class UserCodeResolver {


    private static final File file = new File("userCode.json");
    private static final Logger logger = LogManager.getLogger(UserCodeResolver.class);

    /**
     * default constructor
     */
    public UserCodeResolver() {
    }


    /**
     * This method reads the structure of the {@code folder} passed into the method and returns a list of all file paths
     * which don't end with "BaseGen.java" or ".jar.
     *
     * @param folder of which the structure should be examined
     * @return the list of  all file paths in that folder, which don't end with "BaseGen.java" or ".jar
     */
    public List<File> readStructureFromFolderAsList(final File folder) throws IOException {
        List<File> filePaths = new ArrayList<>();
        String path = String.valueOf(folder);
        try (Stream<Path> paths = Files.walk(Paths.get(path))) {
            paths.filter(Files::isRegularFile).map(Path::toFile).forEach(file -> {
                if (!file.toString().endsWith("BaseGen.java") && !file.toString().endsWith(".jar") && !file.toString().contains("\\.idea\\") && !file.toString().contains("\\target\\")) {
                    filePaths.add(file);
                }
            });
        } catch (IOException e) {
            logger.error("Error reading structure of folder {}: {}", folder.toString(), e.getMessage());
            throw e;
        }
        return filePaths;
    }

    /**
     * This method reads the content of all files in the {@code fileList} and returns a {@code String} of a
     * formatted as a JSON object, which gets constructed with the {@link UserFileWrapper} and the {@link UserFile}.
     * In case the file is a binary file, which is checked by {@link UserCodeResolver#isBinaryFile} it gets encoded as Base64.
     *
     * @param fileList the file paths
     * @return a {@code String} of a formatted as a JSON object constructed with the {@link UserFileWrapper}
     * @throws JsonProcessingException if the {link UserFileWrapper} object cant be transformed toa JSON string by the {@link ObjectMapper}
     */
    public String readContentOfFilesAsString(List<File> fileList) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        UserFileWrapper userFileWrapper = fileList.stream().map(file -> {
            UserFile userFile = new UserFile();
            userFile.setFilename(String.valueOf(file));
            try {
                //////// encode file as Base64 and set content of UserFile  ////////
                if (isBinaryFile(file.toString())) {
                    userFile.setContent(Collections.singletonList(encodeFileToBase64(file)));
                } else {
                    userFile.setContent(Files.readAllLines(file.toPath()));
                }
            } catch (IOException e) {
                logger.error("Error reading file {}: {}", file, e.getMessage());
            }
            return userFile;
        }).collect(Collectors.collectingAndThen(Collectors.toList(), UserFileWrapper::new));
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(userFileWrapper);
    }

    /**
     * This method checks if a file is a binary file based on the file ending. It should be noted that the list might
     * not be exhaustive, if you need more file formats it might be better to use <a href="https://tika.apache.org/">Tika</a>.
     * This implementation was used to prevent false positives and make it clear which files get encoded.
     *
     * @param file as String which should be examined for file ending
     * @return {@code true} if file is binary file else return {@code false}
     */
    private static boolean isBinaryFile(String file) {
        return file.matches(".*\\.(jpeg|jpg|png|gif|bmp|tiff|ico|webp|heic|heif|avi|mp4|wmv|mp3|wav|ogg|flac|pdf|doc|docx|xls|xlsx|ppt|pptx|zip|tar|gz|bz2|7z|apk|ipa|exe|dll)$");
    }


    /**
     * This method writes the formatted JSON String that {@link UserCodeResolver#readContentOfFilesAsString} to {@link UserCodeResolver#file}.
     *
     * @param jsonAsFormattedString the user code JSON formatted as JSON string
     * @throws IOException if the file exists but is a directory rather than a regular file, does not exist but cannot be created, or cannot be opened for any other reason
     */
    public void writeStringToUserContent(String jsonAsFormattedString) throws IOException {
        FileWriter fileWriter = new FileWriter(file, false);
        fileWriter.write(jsonAsFormattedString);
        fileWriter.close();
    }

    /**
     * This method writes the content of the {@link UserFileWrapper} object to all files, which might have been modified or added by the user.
     *
     * @param folder          of the newly generated project
     * @throws IOException Signals that an I/O exception of some sort has occurred. This class is the general class of exceptions produced by failed or interrupted I/O operations.
     */
    public void writeUserContentInNewProject(File folder) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        UserFileWrapper userFileWrapper = objectMapper.readValue(this.getFile(), UserFileWrapper.class);
        if (!userFileWrapper.getFiles().isEmpty()) {
            userFileWrapper = this.updateNamesOfImplFiles(userFileWrapper, folder);
        }

        for (UserFile file : userFileWrapper.getFiles()) {
            Path path = Paths.get(file.getFilename());
            try {
                if (Files.notExists(path)) {
                    Files.createFile(path);
                }
            } catch (IOException e) {
                logger.error("An error occurred while creating the file {}: {}", file, e.getMessage());
                throw e;
            }

            if (isBinaryFile(file.getFilename())) {
                byte[] decodedBytes = Base64.getDecoder().decode(file.getContent().get(0));
                try (FileOutputStream outputStream = new FileOutputStream(file.getFilename())) {
                    outputStream.write(decodedBytes);
                } catch (IOException e) {
                    logger.error("An error occurred while writing in file {}: {}", file, e.getMessage());
                    throw e;
                }
            } else {
                try (FileWriter writer = new FileWriter(file.getFilename())) {
                    for (int i = 0; i < file.getContent().size(); i++) {
                        writer.write(file.getContent().get(i).concat(System.lineSeparator()));
                    }
                } catch (IOException e) {
                    logger.error("An error occurred while writing in file {}: {}", file, e.getMessage());
                    throw e;
                }
            }
        }
    }

    /**
     * This method returns the value of the field file
     *
     * @return the value of the field file
     */
    public File getFile() {
        return file;
    }

    /**
     * This method returns the {@code file} encoded as base64 {@code String}.
     *
     * @param file the file that should be encoded as base64, this works on any file that consists of binary data.
     * @return file encoded as base64 {@code String}
     * @throws IOException if an I/O error occurs reading from the stream OutOfMemoryError – if an array of the required
     *                     size cannot be allocated, for example the file is larger that 2GB.
     */
    private static String encodeFileToBase64(File file) throws IOException {
        byte[] fileBytes = Files.readAllBytes(Paths.get(file.getPath()));
        return Base64.getEncoder().encodeToString(fileBytes);
    }

    /**
     * This method compares the folder structure of the old and the new project with {@link UserCodeResolver#compareStructure}
     * and updates the {@code filename} and {@code content} of all {@link UserFile} Objects in the {@link UserFileWrapper} accordingly.
     * This means updating the {@code filename}, the class name and all constructor names inside the of {@code content} if a renaming has occurred,
     * which the {@link UserCodeResolver#compareStructure} has found.
     *
     * @param userFileWrapper wrapper object which contains a list of {@link UserFile} objects
     * @param folder          of the newly generated project
     * @return wrapper object {@link UserFileWrapper} with updated values of the {@link UserFile} objects
     */
    private UserFileWrapper updateNamesOfImplFiles(UserFileWrapper userFileWrapper, File folder) throws IOException {
        List<String> oldProjectStructureList = new ArrayList<>();
        List<File> newProjectStructureList = this.readStructureFromFolderAsList(folder);

        userFileWrapper.getFiles().forEach(file -> oldProjectStructureList.add(file.getFilename()));
        Diff diff = this.compareStructure(oldProjectStructureList, this.convertNewProjectStructureToList(newProjectStructureList));
        //////// Get all elementValueChanges that are of Type ListChanges and update the userFile objects based on them ////////
        for (Change change : diff.getChanges()) {
            if (change instanceof ListChange listChange) {
                logger.info("The following structural changes have been found:");
                for (ContainerElementChange element : listChange.getChanges()) {
                    if (element instanceof ElementValueChange elementValueChange) {
                        logger.info(elementValueChange);
                        userFileWrapper = this.updateFilesBasedOnElementValueChange(elementValueChange, userFileWrapper);
                    }
                }
            }
        }
        return userFileWrapper;
    }

    /**
     * This method converts a list of files to a list of strings.
     *
     * @param fileList which should be converted
     * @return fileList converted to a string list
     */
    private List<String> convertNewProjectStructureToList(List<File> fileList) {
        return fileList.stream().map(String::valueOf).collect(Collectors.toList());
    }

    /**
     * This method updates the {@link UserFile} objects inside the {@link UserFileWrapper} object based on
     * the {@link ElementValueChange} object
     *
     * @param elementValueChange which gets extracted from the {@link Diff} object which gets created by the {@link UserCodeResolver#compareStructure} method
     * @param userFileWrapper    is the object which contains the list of the {@link UserFile} objects
     * @return the updated {@link UserFileWrapper} object which contains the list of {@link UserFile} objects with updated file paths / names,
     * package names, class names and constructor names
     */
    private UserFileWrapper updateFilesBasedOnElementValueChange(ElementValueChange elementValueChange, UserFileWrapper userFileWrapper) {
        //////// Get all relevant values from the ElementValue Change object ////////
        String oldPath = elementValueChange.getLeftValue().toString();
        String newPath = elementValueChange.getRightValue().toString();
        String[] oldPathArray = oldPath.split("\\\\");
        String[] newPathArray = newPath.split("\\\\");
        int oldFileTypeStart = oldPathArray[oldPathArray.length - 1].indexOf(".");
        String oldConstructorName = oldPathArray[oldPathArray.length - 1].substring(0, oldFileTypeStart);
        int newFileTypeStart = newPathArray[newPathArray.length - 1].indexOf(".");;
        String newConstructorName = newPathArray[newPathArray.length - 1].substring(0, newFileTypeStart);
        //////// Find and update the relevant file with the new project  ////////
        for (UserFile file : userFileWrapper.getFiles()) {
            if (file.getFilename() == elementValueChange.getLeftValue()) {
                file.setFilename(newPath);
                for (int i = 0; i < file.getContent().size(); i++) {
                    String line = file.getContent().get(i);
                    if (line == null) continue;
                    if (line.contains("package")) {
                        line = this.getNewPackageName(oldPathArray);
                        file.getContent().set(i, line);
                    }
                    if (line.contains("class ")) {
                        line = line.replace(oldConstructorName, newConstructorName);
                        String baseClass= "";
                        String[] words = line.split("\\s+");
                        for(String word : words){
                            if(word.contains("BaseGen")){
                                baseClass = word;
                            break;
                            }
                        }
                        String tempName = newConstructorName;
                        String newBaseGenClassName = tempName.replace("Impl", "BaseGen");
                        line = line.replace(baseClass, newBaseGenClassName);
                        file.getContent().set(i, line);
                    }
                    if(line.contains(oldConstructorName)) {
                        file.getContent().set(i, line.replace(oldConstructorName, newConstructorName));
                    }
                }
            }
        }
        return userFileWrapper;
    }

    /**
     * Returns the new PackageName based on the new structure
     *
     * @param oldPathArray the path array of the old project
     * @return newPackageName the new package name for the java file in the new project
     */
    private String getNewPackageName(String[] oldPathArray) {
        String newPackage = oldPathArray[oldPathArray.length - 2];
        for (int arrayIndex = oldPathArray.length - 3; arrayIndex >= 0; arrayIndex--) {
            if (!oldPathArray[arrayIndex].equals("java")) {
                newPackage = oldPathArray[arrayIndex].concat(".").concat(newPackage);
            } else break;
        }
        return "package ".concat(newPackage).concat(";");
    }

    /**
     * This method compares the
     *
     * @param oldFolderStructure the folderStructure of the old project as a {@code List<String>} of all file paths strings, which don't end with {@code "BaseGen.java"} or {@code ".jar"}
     * @param newFolderStructure the folderStructure of the new project as a {@code List<String>} list all file path strings, which don't end with {@code "BaseGen.java"} or {@code ".jar"}
     * @return diff which displays the differences in the project structure based on the levenshtein distance
     */
    private Diff compareStructure(List<String> oldFolderStructure, List<String> newFolderStructure) {
        Javers javers = JaversBuilder.javers().withListCompareAlgorithm(ListCompareAlgorithm.LEVENSHTEIN_DISTANCE).build();
        return javers.compare(oldFolderStructure, newFolderStructure);
    }
}




