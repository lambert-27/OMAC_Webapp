package OMAC_Webapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private String omacId;
    private String firstName;
    private String lastName;
    private String level;
}