package gusevdm.luxms.model;

import gusevdm.luxms.model.LuxDataType;
import org.json.simple.JSONObject;

/***/
public interface LuxModelInterface {

    JSONObject getAsJSON();    // get object as a JSON

    LuxDataType getDataType(); // get type of object



}
