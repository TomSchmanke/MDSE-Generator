package util;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.Diff;
import org.javers.core.diff.ListCompareAlgorithm;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
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
        List<File> fileList = readStructureFromFolderAsList(folder);

        String fileContent = readContentOfFilesAndMapAsString(fileList);
        //writeStringToFile(fileContent);
        Diff s= compareStructure(convertOldProjectStructureToList(),convertNewProjectStructureToList(fileList));
        System.out.println(s);

    }
    private List<String> convertOldProjectStructureToList() throws IOException {
        JsonNode jsonNode = readJSONFileAsJsonNode().get("ProjectStructure");
        ObjectMapper mapper = new ObjectMapper();
        String jsonNodeAsString = jsonNode.toString();
        List<String> stringList = mapper.readValue(jsonNodeAsString,List.class);
        return stringList;
    }

    private List<String> convertNewProjectStructureToList(List<File> fileList) throws IOException {
        List<String> fileListAsString = new ArrayList<>();
        for(File file: fileList){
            fileListAsString.add(String.valueOf(file).replace("\\\\", "\\\\\\\\"));

        }
        return fileListAsString;
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
            paths.filter(Files::isRegularFile).map(Path::toFile).forEach(file -> filePaths.add(file));
        }
        return filePaths;
    }

    /**
     * Read content of files and map as string.
     *
     * @param fileList the file paths
     * @return the string
     */
    private String readContentOfFilesAndMapAsString(List<File> fileList) throws JsonProcessingException {
        BufferedReader reader;
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();
        ArrayNode arrayNode = mapper.createArrayNode();
        for (File file : fileList) {
            try {
                reader = new BufferedReader(new FileReader(file));
                ArrayList<String> fileStringList = new ArrayList<>();
                String line = reader.readLine();
                while (line != null) {
                    line = reader.readLine();
                    fileStringList.add(line);
                }
                reader.close();
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




