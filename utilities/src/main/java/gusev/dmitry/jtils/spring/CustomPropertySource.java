package gusev.dmitry.jtils.spring;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.env.PropertySource;

/**
 * Custom implementation of property source class for Spring context. We need this
 * custom property for set up our value (programmatically) after loading xml config,
 * but before loading application (spring) context.
 * Used with Spring 4.x (4.0.1.RELEASE)
 * @author Gusev Dmitry (gusevd)
 * @version 1.0 (DATE: 10.02.14)
*/

public class CustomPropertySource extends PropertySource<String> {

    private final Log log = LogFactory.getLog(CustomPropertySource.class);

    private String propertyName;
    private String propertyValue;

    /***/
    public CustomPropertySource(String propertySourceName, String propertyName, String propertyValue) {
        super(propertySourceName);
        log.debug(String.format("CustomPropertySource constructor(): name [%s], property [%s], value [%s].",
                propertySourceName, propertyName, propertyValue));
        this.propertyName  = propertyName;
        this.propertyValue = propertyValue;
    }

    @Override
    public String getProperty(String name) {
        String propValue;
        if (this.propertyName.equals(name)) { // we will change value only for our property
            log.info(String.format("Property [%s] new value: [%s].", this.propertyName, this.propertyValue));
            propValue = this.propertyValue;
        } else {
            propValue = null;
        }
        return propValue;
    }

}