package user_code_resolver;

import java.util.ArrayList;
import java.util.List;

/**
 * class for creating the JSON Object
 *
 * @author Jonas Knebel
 * @version 1.0 Initial creation
 */
public class Project {
    private List<UserFile> files = new ArrayList<>();

    public Project(List<UserFile> files) {
        this.files = files;
    }

    public Project() {
    }


    public List<UserFile> getFiles() {
        return this.files;
    }

    public void setFiles(List<UserFile> files) {
        this.files = files;
    }

    public String toString() {
        return "Project(files=" + this.getFiles() + ")";
    }
}
