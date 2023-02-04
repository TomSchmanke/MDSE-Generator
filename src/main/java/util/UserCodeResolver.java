package util;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.Change;
import org.javers.core.diff.Diff;
import org.javers.core.diff.ListCompareAlgorithm;
import org.javers.core.diff.changetype.PropertyChange;
import org.javers.core.diff.changetype.ReferenceChange;
import org.javers.core.diff.changetype.ValueChange;
import org.javers.core.diff.changetype.container.CollectionChange;
import org.javers.core.diff.changetype.container.ContainerElementChange;
import org.javers.core.diff.changetype.container.ListChange;
import org.javers.core.metamodel.annotation.Value;

import java.io.*;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

import static org.javers.core.diff.changetype.PropertyChangeType.PROPERTY_VALUE_CHANGED;

/**
 * The type User code resolver.
 */
public class UserCodeResolver {

    private File file = new File("output.json");

    /**
     * Instantiates a new User code resolver.
     *
     * @param folder the folder
     * @throws IOException the io exception
     */
    public UserCodeResolver(File folder) throws IOException {
        //writeStringToFile(readContentOfFilesAsString(readStructureFromFolderAsList(folder)));
        JsonNode jsonNode = readJSONFileAsJsonNode();
        writeUserContentInFiles(jsonNode, folder);
    }

    /**
     * Convert old project structure to list<String>.
     *
     * @return the list
     * @throws IOException the io exception
     */
    private List<String> convertOldProjectStructureToList(JsonNode jsonNode) throws IOException {;
        ObjectMapper mapper = new ObjectMapper();
        String jsonNodeAsString = jsonNode.get("ProjectStructure").toString();
        List<String> stringList = mapper.readValue(jsonNodeAsString,List.class);
        return stringList;
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

    private void writeUserContentInFiles(JsonNode jsonNode, File folder) throws IOException {
        List<File> fileList = readStructureFromFolderAsList(folder);
        Diff changes= compareStructure(convertOldProjectStructureToList(jsonNode),convertNewProjectStructureToList(fileList));
       if (changes.hasChanges()) {
            jsonNode = updateNamesOfImplFiles(jsonNode, changes);
        }
        Iterator<String> fieldNames = jsonNode.fieldNames();
        while(fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            if(!fieldName.equals("ProjectStructure")) {
                String filePath = fieldName;
                Path path = Paths.get(filePath);
                if(Files.notExists(path)){
                    Files.createFile(path);
                }

                List<String> codeLines = new ArrayList<>();
                for (JsonNode node :jsonNode.get(fieldName)) {
                    if (!node.isNull() && !node.asText().isEmpty()) {
                        codeLines.add(node.asText());
                    } else {
                        codeLines.add("");
                    }
                }
                try (FileWriter writer = new FileWriter(filePath)) {
                    for (String line : codeLines) {
                        writer.write(line + System.lineSeparator());
                    }
                } catch (IOException e) {
                    System.out.println("An error occurred while writing the file: " + e.getMessage());
                }
            }
        }
    }

    private JsonNode updateNamesOfImplFiles(JsonNode jsonNode, Diff diff) {
        for (Change change : diff.getChanges()) {
            if (change instanceof ValueChange) {
                ValueChange valueChange = (ValueChange) change;
                System.out.println("Old Value: " + valueChange.getLeft());
                System.out.println("New Value: " + valueChange.getRight());
            } else if (change instanceof ReferenceChange) {
                ReferenceChange referenceChange = (ReferenceChange) change;
                System.out.println("Old Reference: " + referenceChange.getLeft());
                System.out.println("New Reference: " + referenceChange.getRight());
            } else if (change instanceof CollectionChange) {
                CollectionChange collectionChange = (CollectionChange) change;
                System.out.println("Affected Element: " + collectionChange.getAffectedGlobalId().value());
                System.out.println("Change Type: " + collectionChange.getChangeType());
                if (collectionChange.getChangeType() == PROPERTY_VALUE_CHANGED) {
                    ObjectMapper mapper = new ObjectMapper();
                    ObjectNode rootNode = mapper.createObjectNode();
                    System.out.println(collectionChange.getChanges());
                    String oldPath = convertListElementToPath(collectionChange.getLeft().toString());
                    String newPath = convertListElementToPath(collectionChange.getRight().toString());
                    String[] oldPathArray = oldPath.split("\\\\");
                    String[] newPathArray = newPath.split("\\\\");
                    ArrayNode classNode = (ArrayNode) jsonNode.get(oldPath);
                    Integer packageLine = 0;
                    List<Integer> changesIntegerList = new ArrayList<>();
                    int fileTypeStart = oldPathArray[oldPathArray.length - 1].indexOf(".");
                    String constructorName = oldPathArray[oldPathArray.length - 1].substring(0, fileTypeStart);
                    String newConstructorName = newPathArray[oldPathArray.length - 1].substring(0, fileTypeStart);
                    for (JsonNode line : classNode) {
                        if (line.textValue() != null && (line.textValue().contains("package") || line.textValue().contains(" class ") || line.textValue().contains(constructorName))) {
                            changesIntegerList.add(packageLine);
                        }
                        packageLine++;
                    }
                    for (int index : changesIntegerList) {
                        String lineStringValue = classNode.get(index).textValue();
                        if (lineStringValue.contains("package")) {
                            String newPackage = oldPathArray[oldPathArray.length - 2];

                            for (int arrayIndex = oldPathArray.length - 3; arrayIndex >= 0; arrayIndex--) {
                                System.out.println(oldPathArray[arrayIndex]);
                                if (oldPathArray[arrayIndex].equals("java")) {
                                    break;
                                } else {
                                    newPackage = oldPathArray[arrayIndex] + "." + newPackage;
                                }
                            }
                            lineStringValue = "package " + newPackage + ";";
                        } else
                            lineStringValue.replace(constructorName, newConstructorName);
                            JsonNode newJsonNode = mapper.convertValue(lineStringValue, JsonNode.class);
                            classNode.set(index, newJsonNode);
                    }
                    return mapper.convertValue(classNode, JsonNode.class);
                }
            }

        }
        return jsonNode;
    }

    private String convertListElementToPath(String toString) {
        return toString.replace("[","").replace("]", "");
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
    private String readContentOfFilesAsString(List<File> fileList) throws JsonProcessingException {
        BufferedReader reader;
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();
        ArrayNode arrayNode = mapper.createArrayNode();
        for (File file : fileList) {
            try {
                reader = new BufferedReader(new FileReader(file));
                ArrayList<String> fileStringList = new ArrayList<>();
                String line = reader.readLine();
                fileStringList.add(line);
                while (line != null) {
                    line = reader.readLine();
                    fileStringList.add(line);
                }
                reader.close();
                System.out.println(fileStringList);
                ObjectMapper objectMapper = new ObjectMapper();
                String jsonString = objectMapper.writeValueAsString(fileStringList);
                JsonNode jsonNode = objectMapper.readValue(jsonString, JsonNode.class);
                rootNode.put("ProjectStructure", arrayNode.add(String.valueOf(file)));
                rootNode.put(file.toString(), jsonNode);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return mapper.writeValueAsString(rootNode);
    }

    /**
     * Write string to file.
     *
     * @param rootNodeAsString the root node as string
     * @throws IOException the io exception
     */
    private void writeStringToFile(String rootNodeAsString) throws IOException {
        FileWriter fileWriter = new FileWriter(this.file);
        fileWriter.write(rootNodeAsString);
        fileWriter.close();
    }


    /**
     * Read json file as json node json node.
     *
     * @return the json node
     * @throws IOException the io exception
     */
    private JsonNode readJSONFileAsJsonNode() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        try(FileReader fileReader = new FileReader(this.file)) {
            JsonNode jsonNode = mapper.readTree(fileReader);
            return jsonNode;
        }
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




