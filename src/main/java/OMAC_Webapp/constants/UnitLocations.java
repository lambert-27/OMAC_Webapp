package OMAC_Webapp.constants;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Stores the available OMAC unit locations as constants for registration and
 * display.
 */
public final class UnitLocations {

    public static final String ANTRIM = "Antrim";
    public static final String ARMAGH = "Armagh";
    public static final String CARLOW = "Carlow";
    public static final String CLARE = "Clare";
    public static final String CORK = "Cork";
    public static final String DOWN = "Down";
    public static final String DUBLIN = "Dublin";
    public static final String GALWAY = "Galway";
    public static final String KERRY = "Kerry";
    public static final String KILDARE = "Kildare";
    public static final String KILKENNY = "Kilkenny";
    public static final String LAOIS = "Laois";
    public static final String LEITRIM = "Leitrim";
    public static final String LIMERICK = "Limerick";
    public static final String LOUTH = "Louth";
    public static final String MAYO = "Mayo";
    public static final String MEATH = "Meath";
    public static final String OFFALY = "Offaly";
    public static final String ROSCOMMON = "Roscommon";
    public static final String SLIGO = "Sligo";
    public static final String TIPPERARY = "Tipperary";
    public static final String TYRONE = "Tyrone";
    public static final String WATERFORD = "Waterford";
    public static final String WESTMEATH = "Westmeath";
    public static final String WEXFORD = "Wexford";
    public static final String WICKLOW = "Wicklow";

    public static final List<String> ALL_LOCATIONS = Collections.unmodifiableList(Arrays.asList(
            ANTRIM,
            ARMAGH,
            CARLOW,
            CLARE,
            CORK,
            DOWN,
            DUBLIN,
            GALWAY,
            KERRY,
            KILDARE,
            KILKENNY,
            LAOIS,
            LEITRIM,
            LIMERICK,
            LOUTH,
            MAYO,
            MEATH,
            OFFALY,
            ROSCOMMON,
            SLIGO,
            TIPPERARY,
            TYRONE,
            WATERFORD,
            WESTMEATH,
            WEXFORD,
            WICKLOW
    ));

    private UnitLocations() {
    }
}
