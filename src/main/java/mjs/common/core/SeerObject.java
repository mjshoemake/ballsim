package mjs.common.core;


import mjs.common.exceptions.CoreException;
import mjs.common.exceptions.ModelException;
import mjs.common.utils.BeanUtils;
import org.apache.log4j.Logger;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class SeerObject {

    /**
     * The log4j logger to use when writing log messages.
     */
    protected static final Logger log = Logger.getLogger("Core");

    /**
     * List of property descriptors for the target object for which to provide
     * introspection.
     */
    private Map<String,PropertyDescriptor> propertyMap = new HashMap<String, PropertyDescriptor>();

    /**
     * The class for the target object for which to provide introspection.
     */
    protected Class entityClass;

    /**
     * Constructor.
     * @param entityClass Class
     */
    public SeerObject(String entityClass) {
        try {
            this.entityClass = Class.forName(entityClass);
        } catch (ClassNotFoundException e) {
            log.error("Failed to initialize service. Unable to find the specified class (" + entityClass + ").", e);
        }

        try {
            // Get the property descriptors for this bean and put them in
            // a map.
            PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(this.entityClass);
            for (PropertyDescriptor pd : pds) {
                if (! pd.getName().endsWith("Class")) {
                    log.debug("   " + pd.getName() + " / " + pd.getDisplayName());
                    propertyMap.put(pd.getName(), pd);
                }
            }
        } catch (CoreException e) {
            log.error("Failed to extract bean properties from the specified class (" + this.entityClass.getName() + ").");
        }
    }

    /**
     * Uses reflection to get the value of the specified property from the
     * specified bean.
     * @param bean
     * @param propertyName
     * @return Object
     * @throws mjs.common.exceptions.ModelException
     */
    public String getPropertyValue(Object bean, String propertyName) throws ModelException {
        // Get the getter method for this property.
        String[] tokens = propertyName.split("\\+");
        StringBuilder builder = new StringBuilder();
        if (tokens.length > 0) {
            int i = 0;
            log.debug("Processing bean properties:");
            for (String next : tokens) {
                Object[] args = new Object[0];
                log.debug("   " + next);
                PropertyDescriptor pd = propertyMap.get(next);
                Method method = pd.getReadMethod();
                if (method != null) {
                    try {
                        String value = method.invoke(bean, args).toString();
                        if (i > 0) {
                            builder.append(" ");
                        }
                        builder.append(value);
                    } catch (Exception e) {
                        throw new ModelException("Failed to get property '" + propertyName + "' from the specified bean. " + e.getMessage(), e);
                    }
                }
                i++;
            }
            return builder.toString();
        } else {
            return "entity";
        }
    }

    /**
     * Uses reflection to get the value of the specified property from the
     * specified bean.
     * @param bean
     * @param propertyName
     * @return Object
     * @throws mjs.common.exceptions.ModelException
     */
    public String getPropertyType(String propertyName) throws ModelException {
        // Get the property type.
        String[] tokens = propertyName.split("\\+");
        StringBuilder builder = new StringBuilder();
        if (tokens.length > 0) {
            int i = 0;
            log.debug("Processing bean properties:");
            for (String next : tokens) {
                Object[] args = new Object[0];
                log.debug("   " + next);
                PropertyDescriptor pd = propertyMap.get(next);
                Class type = pd.getPropertyType();
                if (i > 0) {
                    builder.append(" ");
                }
                builder.append(type.getSimpleName());
                i++;
            }
            return builder.toString();
        } else {
            return "entity";
        }
    }

}
