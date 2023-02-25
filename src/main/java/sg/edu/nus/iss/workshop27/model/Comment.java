package sg.edu.nus.iss.workshop27.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    private String c_id;
    private String user;
    private int rating;
    private String c_text;
    private int gid;
}
