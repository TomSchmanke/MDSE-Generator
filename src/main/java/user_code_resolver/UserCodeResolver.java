package user_code_resolver;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
 * The type User code resolver.
 */
public class UserCodeResolver {

    private File file = new File("userCode.json");
    /**
     * Instantiates a new User code resolver.
     *
     * @param folder the folder
     * @throws IOException the io exception
     */
    public UserCodeResolver(File folder) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Project project = objectMapper.readValue(this.file, Project.class);
        //METHODE OK
        writeStringToUserContent(readContentOfFilesAsString(readStructureFromFolderAsList(folder), project));


        updateNamesOfImplFiles(project, folder);
        writeUserContentInFiles(project, folder);
    }


    /**
     * Convert new project structure to list<String>.
     *
     * @param fileList the file list
     * @return the list
     * @throws IOException the io exception
     */
    private List<String> convertNewProjectStructureToList(List<File> fileList) throws IOException {
        List<String> fileListAsString = new ArrayList<>();
        for(File file: fileList){
            fileListAsString.add(String.valueOf(file).replace("\\\\", "\\\\\\\\"));
        }
        return fileListAsString;
    }

    private void writeUserContentInFiles(Project project, File folder) throws IOException {
        updateNamesOfImplFiles(project, folder);
            for(UserFiles file: project.getFiles()) {
                Path path = Paths.get(file.getFilename());
                if(Files.notExists(path)){
                    Files.createFile(path);
                }
                try (FileWriter writer = new FileWriter(file.getFilename())) {
                    for (int i = 0; i < file.getContent().size(); i++) {
                        writer.write(file.getContent().get(i) + System.lineSeparator());
                    }
                } catch (IOException e) {
                    System.out.println("An error occurred while writing the file: " + e.getMessage());
                }
            }
    }


    private Project updateNamesOfImplFiles(Project project, File folder) throws IOException {
        List<String> projectStructureList = new ArrayList<>();
        List<File> fileList = readStructureFromFolderAsList(folder);
        for(UserFiles userList : project.getFiles()){
            projectStructureList.add(userList.getFilename());
        }
        Diff diff = compareStructure(projectStructureList,convertNewProjectStructureToList(fileList));
        for (Change change : diff.getChanges()) {
            if (change instanceof ListChange listChange) {
                for (ContainerElementChange element : listChange.getChanges()) {
                    if (element instanceof ElementValueChange elementValueChange) {
                        String oldPath = elementValueChange.getLeftValue().toString();
                        String newPath = elementValueChange.getRightValue().toString();
                        String[] oldPathArray = oldPath.split("\\\\");
                        String[] newPathArray = newPath.split("\\\\");
                        int fileTypeStart = oldPathArray[oldPathArray.length - 1].indexOf(".");
                        String oldConstructorName = oldPathArray[oldPathArray.length - 1].substring(0, fileTypeStart);
                        String newConstructorName = newPathArray[oldPathArray.length - 1].substring(0, fileTypeStart);
                        String newPackage = oldPathArray[oldPathArray.length - 2];
                        for(UserFiles file: project.getFiles()) {
                            if(file.getFilename() == elementValueChange.getLeftValue()){
                                file.setFilename(newPath);
                                for(int i = 0; i< file.getContent().size(); i++){
                                    String line = file.getContent().get(i);
                                    if(line == null) continue;
                                    if(line.contains("package")) {
                                        for (int arrayIndex = oldPathArray.length - 3; arrayIndex >= 0; arrayIndex--) {
                                            if (oldPathArray[arrayIndex].equals("java")) {
                                                break;
                                            } else {
                                                newPackage = oldPathArray[arrayIndex] + "." + newPackage;
                                            }
                                        }
                                        line = "package " + newPackage+";";
                                        file.getContent().set(i, line);
                                    }else if (line.contains("class ") || line.contains(oldConstructorName)) {
                                        file.getContent().set(i, line.replace(oldConstructorName, newConstructorName));
                                    }
                                }
                            }

                        }
                    }
                }
            }
        }
        return project;
    }


    /**
     * Read structure from folder as list
     *
     * @param folder the folder
     * @return the list
     * @throws IOException the io exception
     */
    private List<File> readStructureFromFolderAsList(final File folder) throws IOException {
        List<File> filePaths = new ArrayList<>();
        String path = String.valueOf(folder);
        try (Stream<Path> paths = Files.walk(Paths.get(path))) {
            paths.filter(Files::isRegularFile).map(Path::toFile).forEach(file ->{ if(file.toString().endsWith("Impl.java")) {filePaths.add(file);}});
        }
        return filePaths;
    }

    /**
     * Read content of files and map as string.
     *
     * @param fileList the file paths
     * @return the string
     */
    private String readContentOfFilesAsString(List<File> fileList, Project project) throws JsonProcessingException {
        BufferedReader reader;
        ObjectMapper objectMapper = new ObjectMapper();
        for (File userFiles: fileList) {
            System.out.println(userFiles);
            try {
                UserFiles userFile = new UserFiles();
                reader = new BufferedReader(new FileReader(file));
                ArrayList<String> fileStringList = new ArrayList<>();
                String line = reader.readLine();
                fileStringList.add(line);
                while (line != null) {
                    line = reader.readLine();
                    fileStringList.add(line);
                }
                reader.close();
                userFile.setFilename(String.valueOf(file));
                userFile.setContent(fileStringList);
                project.addFile(userFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return objectMapper.writeValueAsString(project);
    }

    /**
     * Write string to file.
     *
     * @param rootNodeAsString the root node as string
     * @throws IOException the io exception
     */
    private void writeStringToUserContent(String rootNodeAsString) throws IOException {
        FileWriter fileWriter = new FileWriter(this.file);
        System.out.println(rootNodeAsString);
        fileWriter.write(rootNodeAsString);
        fileWriter.close();
    }


    /**
     * Compare structure diff.
     *
     * @param oldFolderstructure the old folderstructure
     * @param newFolderstructure the new folderstructure
     * @return the diff
     */
    private Diff compareStructure(List<String> oldFolderstructure, List<String> newFolderstructure ){
        Javers javers = JaversBuilder.javers().withListCompareAlgorithm(ListCompareAlgorithm.LEVENSHTEIN_DISTANCE).build();
        return javers.compare(oldFolderstructure, newFolderstructure);
    }
}




