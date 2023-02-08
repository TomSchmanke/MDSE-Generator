package util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Util class to move files from one place to another
 *
 * @author Laura Schmidt
 * @version 1.0 Initial implementation
 */
public class FileCopier {
    public void copyFile(String source, String target) throws IOException {
        Path targetPath = Path.of(target);

        if (!Files.exists(targetPath)) {
            Files.copy(Path.of(source), targetPath);
        }
    }
}
