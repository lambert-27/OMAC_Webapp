package OMAC_Webapp.constants;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Stores the training level labels in one constants class so ViewModels do not
 * duplicate hard-coded dropdown values.
 */
public final class TrainingLevelConstants {

    public static final String CARDIAC_FIRST_RESPONSE = "CFR - Cardiac First Response";
    public static final String FIRST_AID_RESPONSE = "FAR - First Aid Response";
    public static final String EMERGENCY_FIRST_RESPONDER = "EFR - Emergency First Responder";
    public static final String EMERGENCY_MEDICAL_TECHNICIAN = "EMT - Emergency Medical Technician";
    public static final String PARAMEDIC = "P - Paramedic";
    public static final String ADVANCED_PARAMEDIC = "AP - Advanced Paramedic";

    public static final List<String> ALL_LEVELS = Collections.unmodifiableList(Arrays.asList(
            CARDIAC_FIRST_RESPONSE,
            FIRST_AID_RESPONSE,
            EMERGENCY_FIRST_RESPONDER,
            EMERGENCY_MEDICAL_TECHNICIAN,
            PARAMEDIC,
            ADVANCED_PARAMEDIC
    ));

    private TrainingLevelConstants() {
    }
}
