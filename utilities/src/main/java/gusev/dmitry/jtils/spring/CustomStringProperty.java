package gusev.dmitry.jtils.spring;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.env.PropertySource;

/**
 * Custom implementation of property source class for Spring context. We need this custom property
 * for set up our value (programmatically) after loading xml Spring context config, but before
 * loading application (Spring) context.
 * Used with Spring 4.x/5.x
 * Created by gusevdm on 19/02/2017.
*/

public class CustomStringProperty extends PropertySource<String> {

    private final Log LOG = LogFactory.getLog(CustomStringProperty.class);

    private String propertyName;
    private String propertyValue;

    /***/
    public CustomStringProperty(String propertySourceName, String propertyName, String propertyValue) {
        super(propertySourceName);

        LOG.debug(String.format("CustomStringProperty constructor(): name [%s], property [%s], value [%s].",
                propertySourceName, propertyName, propertyValue));

        if (StringUtils.isBlank(propertyName)) {
            throw new IllegalArgumentException("Property name can't be empty/null!");
        }

        this.propertyName  = propertyName;
        this.propertyValue = propertyValue;
    }

    public String getPropertyName() {
        return propertyName;
    }

    @Override
    public String getProperty(String name) {
        String propValue;
        if (this.propertyName.equals(name)) { // we will change value only for our property
            LOG.info(String.format("Changing value: property [%s] new value: [%s].", this.propertyName, this.propertyValue));
            propValue = this.propertyValue;
        } else {
            propValue = null;
        }
        return propValue;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .appendSuper(super.toString())
                .append("propertyName55", propertyName)
                .append("propertyValue55", propertyValue)
                .toString();
    }

}