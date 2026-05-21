package OMAC_Webapp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HourLog {

    private int id;
    private String firstName;
    private String lastName;
    private String level;
    private LocalDate logDate;
    private double hours;
    private String description;
}