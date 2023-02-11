package user_code_resolver;

import java.util.ArrayList;
import java.util.List;

public class Project {
    private List<UserFiles> files = new ArrayList<>();

    public Project(List<UserFiles> files) {
        this.files = files;
    }

    public Project() {
    }

    public void addFile(UserFiles file) {
        files.add(file);
    }

    public List<UserFiles> getFiles() {
        return this.files;
    }

    public void setFiles(List<UserFiles> files) {
        this.files = files;
    }

    public String toString() {
        return "Project(files=" + this.getFiles() + ")";
    }
}
