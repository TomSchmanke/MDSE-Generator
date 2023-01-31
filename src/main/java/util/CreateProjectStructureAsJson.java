package util;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class CreateProjectStructureAsJson {
    private List<File> filePaths = new ArrayList<>();
    public CreateProjectStructureAsJson(File folder) throws IOException {
        listFilesForFolderStructure(folder);
    }

    private void listFilesForFolderStructure(final File folder) throws IOException {
        try (Stream<Path> paths = Files.walk(Paths.get(String.valueOf(folder)))) {
            paths.filter(Files::isRegularFile).map(Path::toFile).forEach(file -> filePaths.add(file));
            }
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode rootNode = mapper.createObjectNode();
            BufferedReader reader;
            for(File file: filePaths) {
                try {
                    reader = new BufferedReader(new FileReader(file));
                    ArrayList<String> fileStringList = new ArrayList<>();
                    String line = reader.readLine();
                    while (line != null) {

                        line = reader.readLine();
                        if(line.startsWith("//MDSD-GENERATED ")){
                            fileStringList.add(line);
                        }

                    }
                    reader.close();
                    ObjectMapper objectMapper = new ObjectMapper();
                    String jsonString = objectMapper.writeValueAsString(fileStringList);
                    JsonNode jsonNode = objectMapper.readValue(jsonString, JsonNode.class);
                    rootNode.set(file.toString(), jsonNode);
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        writeJSONNodeToFile(mapper.writeValueAsString(rootNode));
        System.out.println(rootNode);
    }

    private void writeJSONNodeToFile(String rootNodeAsString) throws IOException {
        File file = new File("output.json");
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(rootNodeAsString);
        fileWriter.close();
    }
}




