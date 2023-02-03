package util;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.StringEscapeUtils;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.Diff;
import org.javers.core.diff.ListCompareAlgorithm;

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
        Diff s= compareStructure(convertOldProjectStructureToList(jsonNode),convertNewProjectStructureToList(fileList));
        System.out.println(s);
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




