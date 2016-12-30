package dg.social;

import java.text.SimpleDateFormat;

/**
 * Some common defaults.
 * Created by gusevdm on 12/12/2016.
 */

public final class CommonDefaults {

    private CommonDefaults() {} // can't instantiate utility class

    /** Default encoding for content. */
    public static final String           DEFAULT_ENCODING = "UTF-8";

    /** Default date/time format. */
    public static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");

}
