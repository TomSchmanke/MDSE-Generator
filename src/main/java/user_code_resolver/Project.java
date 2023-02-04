package user_code_resolver;

import lombok.*;


import java.util.ArrayList;
import java.util.List;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Project {
    private List<UserFiles> files = new ArrayList<>();

    public void addFile(UserFiles file){
        files.add(file);
    }
}