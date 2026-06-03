package OMAC_Webapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HourLog {

    private int id;
    private String omacId;
    private LocalDate logDate;
    private double hours;
    private String description;
}