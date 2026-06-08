package OMAC_Webapp.util;

import java.security.SecureRandom;
import java.util.Locale;

public final class OmacIdGenerator {

    private static final String PREFIX = "OMAC";
    private static final int MAX_VALUE = 100000;
    private static final SecureRandom RANDOM = new SecureRandom();

    private OmacIdGenerator() {
    }

    public static String generate() {
        return PREFIX + String.format(Locale.ROOT, "%05d", RANDOM.nextInt(MAX_VALUE));
    }
}
