package user_code_resolver;

import java.util.ArrayList;
import java.util.List;

/**
 * class for creating the Elements in the JSON Object
 *
 * @author Jonas Knebel
 * @version 1.0 initial creation
 */
public class UserFile {
    private String filename = "";
    private List<String> content = new ArrayList<>();

    /**
     * default constructor
     */
    public UserFile() {
    }

    /**
     * This returns the value of the field filename
     *
     * @return the value of the field filename
     */
    public String getFilename() {
        return filename;
    }

    /**
     * This sets the value of the field filename
     *
     * @param filename sets the value of the field filename
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * This returns the value of the field content
     *
     * @return the value of the field content
     */
    public List<String> getContent() {
        return content;
    }

    /**
     * This sets the value of the field content
     *
     * @param content sets the value of the field content
     */
    public void setContent(List<String> content) {
        this.content = content;
    }


}
