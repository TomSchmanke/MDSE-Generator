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
    /**
     * Copies a file from a given source to a given target. The source
     * String needs to point to a file. The target can contain a filename.
     * It also works if target only points to a directory.
     *
     * @param source source path pointing to file
     * @param target target path pointing to target file or directory
     * @throws IOException file not found or locked
     */
    public void copyFile(String source, String target) throws IOException {
        Path targetPath = Path.of(target);

        if (!Files.exists(targetPath)) {
            Files.copy(Path.of(source), targetPath);
        }
    }
}
