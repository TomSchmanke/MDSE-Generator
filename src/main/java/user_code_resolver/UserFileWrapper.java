package user_code_resolver;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper class for a list of {@link UserFile} Objects
 *
 * @author Jonas Knebel
 * @version 1.0 Initial creation
 */
public class UserFileWrapper {
    private List<UserFile> files = new ArrayList<>();

    /**
     * constructs an instance of the Project class
     *
     * @param files
     */
    public UserFileWrapper(List<UserFile> files) {
        this.files = files;
    }

    /**
     * default constructor
     */
    public UserFileWrapper() {
    }

    /**
     * This returns the value of the field files
     *
     * @return the value of the field files
     */
    public List<UserFile> getFiles() {
        return files;
    }

    /**
     * This sets the value of the field files
     *
     * @param files sets the value of the field files
     */
    public void setFiles(List<UserFile> files) {
        this.files = files;
    }
}
