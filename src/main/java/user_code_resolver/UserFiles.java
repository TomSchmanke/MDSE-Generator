package user_code_resolver;

import lombok.*;

import java.util.List;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserFiles {
    private String filename;
    private List<String> content;
}