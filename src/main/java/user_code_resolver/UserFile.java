package user_code_resolver;

import java.util.ArrayList;
import java.util.List;

public class UserFile {
    private String filename = "";
    private List<String> content = new ArrayList<>();

    public UserFile(String filename, List<String> content) {
        this.filename = filename;
        this.content = content;
    }

    public UserFile() {
    }

    public String getFilename() {
        return this.filename;
    }

    public List<String> getContent() {
        return this.content;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setContent(List<String> content) {
        this.content = content;
    }

    public String toString() {
        return "UserFiles(filename=" + this.getFilename() + ", content=" + this.getContent() + ")";
    }
}
