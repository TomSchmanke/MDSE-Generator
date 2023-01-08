package util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class InitializeProject {

    private static final String SPRING_INIT_BASE_URL = "https://start.spring.io/starter.zip";

    public String loadGeneratedFilesFromSpringInitializer() throws IOException {
        URL url = new URL(SPRING_INIT_BASE_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        Map<String, String> parameters = new HashMap<>();
        parameters.put("type", "maven-project");
        parameters.put("language", "java");
        parameters.put("bootVersion", "3.0.0");
        parameters.put("groupId", "de.generator");
        parameters.put("artifactId", "generated-application");
        parameters.put("name", "generated-application");
        parameters.put("description", "Generated basic build for Spring Boot");
        parameters.put("packageName", "de.generator.generated-application");
        parameters.put("packaging", "jar");
        parameters.put("javaVersion", "17");
        parameters.put("dependencies", "devtools,web,security,data-jpa");

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
            File newFile = newFile(destDir, zipEntry);
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

    private File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }

}
