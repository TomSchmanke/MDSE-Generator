package util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Util class to move files from one place to another
 *
 * @author Laura Schmidt
 * @version 1.0 Initial implementation
 */
public class FileCopier {
    public void copyFile(String sourcePath, String targetPath) throws IOException {
        Files.copy(Path.of(sourcePath), Path.of(targetPath), StandardCopyOption.REPLACE_EXISTING);
    }
}
