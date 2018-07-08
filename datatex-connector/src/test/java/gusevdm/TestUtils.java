package gusevdm;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Ignore;

/** Some useful test utils. */

@Ignore
public final class TestUtils {

    private static final JSONParser JSON_PARSER = new JSONParser();

    private TestUtils() {
    }

    public static JSONObject parseJSON(String jsonString) throws ParseException {
        return (JSONObject) JSON_PARSER.parse(jsonString);
    }

    public static <R> R parseJSON(String jsonString, Class<R> resultClass) throws ParseException {
        return resultClass.cast(JSON_PARSER.parse(jsonString));
    }

}
