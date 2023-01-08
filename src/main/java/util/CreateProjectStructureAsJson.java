package util;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
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
            for(File file: filePaths) {
                rootNode.set(file.toString(), null);
            }
        System.out.println(rootNode);
        }
    }




