package gusevdm.luxms.model;

import gusevdm.luxms.model.LuxDataType;
import org.json.simple.JSONObject;

import java.util.List;

/***/
public interface LuxModelInterface {

    JSONObject getAsJSON();    // get object as a JSON

    LuxDataType getDataType(); // get type of object

    //String[] getCSVHeader();   // header for CSV file for loading

}
