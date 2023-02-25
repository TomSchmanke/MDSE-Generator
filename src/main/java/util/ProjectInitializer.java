package util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Class which holds methods to download a pre-configured sprint-boot project as zip file from
 * <a href="https://start.spring.io">start.spring.io</a> and methods to unzip the downloaded file
 *
 * @author Tom Schmanke
 * @version 1.0 Initial creation
 */
public class ProjectInitializer {

    private static final String SPRING_INIT_BASE_URL = "https://start.spring.io/starter.zip";

    /**
     * Method makes a configured call to a spring initializr endpoint to download a zip file which contains the
     * configured base structure and base files of a new Spring Boot project.
     * The configurations can be set via the parameters.
     *
     * @param groupId      GroupId of the generated Spring Boot project
     * @param artifactId   ArtifactId of the generated Spring Boot project
     * @param name         Name of the generated Spring Boot project
     * @param description  Description of the generated Spring Boot project
     * @param javaVersion  Java version of the generated Spring Boot project
     * @param bootVersion  Spring Boot version of the generated Spring Boot project
     * @param dependencies Keywords for further dependencies of the generated Spring Boot project
     * @return The name of the zip file or null when no file was generated
     * @throws IOException Signals that an I/O exception of some sort has occurred. This class is the general class of
     *                      exceptions produced by failed or interrupted I/O operations.
     */
    public String loadGeneratedFilesFromSpringInitializer(String groupId, String artifactId, String name,
                                                          String description, String javaVersion, String bootVersion,
                                                          List<String> dependencies) throws IOException {
        URL url = new URL(SPRING_INIT_BASE_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        Map<String, String> parameters = new HashMap<>();
        parameters.put("type", "maven-project");
        parameters.put("language", "java");
        parameters.put("bootVersion", bootVersion);
        parameters.put("groupId", groupId);
        parameters.put("artifactId", artifactId);
        parameters.put("name", name);
        parameters.put("description", description);
        parameters.put("packaging", "jar");
        parameters.put("javaVersion", javaVersion);
        parameters.put("dependencies", String.join(",", dependencies));

        connection.setDoOutput(true);
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
        out.flush();
        out.close();


        if (connection.getResponseCode() >= 300) {
            Reader streamReader = new InputStreamReader(connection.getErrorStream());
            System.out.println(streamReader.read());
        } else {
            try (InputStream in = connection.getInputStream()) {
                Path target = Paths.get("", parameters.get("name") + ".zip").toAbsolutePath();
                Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
                return parameters.get("name") + ".zip";
            }
        }
        return null;
    }


    /**
     * Method takes a zip file as parameter and unzip the file in the target directory
     *
     * @param inputFile  The path to the zip file
     * @param targetFile The path to the target directory
     * @throws IOException Signals that an I/O exception of some sort has occurred. This class is the general class of
     *                      exceptions produced by failed or interrupted I/O operations.
     */
    public void unzipFile(String inputFile, String targetFile) throws IOException {
        File destDir = new File(targetFile);

        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(new FileInputStream(inputFile));
        ZipEntry zipEntry = zis.getNextEntry();
        while (zipEntry != null) {
            File newFile = newFileFromZip(destDir, zipEntry);
            if (zipEntry.isDirectory()) {
                if (!newFile.isDirectory() && !newFile.mkdirs()) {
                    throw new IOException("Failed to create directory " + newFile);
                }
            } else {
                // fix for Windows-created archives
                File parent = newFile.getParentFile();
                if (!parent.isDirectory() && !parent.mkdirs()) {
                    throw new IOException("Failed to create directory " + parent);
                }

                // write file content
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
            }
            zipEntry = zis.getNextEntry();
        }

        zis.closeEntry();
        zis.close();
    }

    /**
     * Creates a file based on an entry in a zip file
     *
     * @param destinationDir Target directory to place the file
     * @param zipEntry       Entry in zip file
     * @return The created file
     * @throws IOException Signals that an I/O exception of some sort has occurred. This class is the general class of
     *                      exceptions produced by failed or interrupted I/O operations.
     */
    private File newFileFromZip(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }

    /**
     * Creates a directory based on a directory in a zip file
     *
     * @param destinationDir Target directory to place the new directory
     * @param newDirectory   Directory in zip file
     * @return true/false if the directory could be created
     */
    public boolean newDirectoryFromPath(String destinationDir, String newDirectory) {
        File theDir = new File(destinationDir + newDirectory);
        if (!theDir.exists()) {
            return theDir.mkdirs();
        }
        return false;
    }

}
