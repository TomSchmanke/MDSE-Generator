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
public class ProjectInitializerImpl {
    private static final String SPRING_INIT_BASE_URL = "https://start.spring.io/starter.zip";

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


    public void unzipFile(String in, String target) throws IOException {
        File destDir = new File(target);

        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(new FileInputStream(in));
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

    private File newFileFromZip(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }

    public boolean newDirectoryFromPath(String destinationDir, String newDirectory) {
        File theDir = new File(destinationDir + newDirectory);
        if (!theDir.exists()) {
            return theDir.mkdirs();
        }
        return false;
    }

}

