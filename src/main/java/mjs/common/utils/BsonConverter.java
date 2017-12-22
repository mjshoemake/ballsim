package mjs.common.utils;

import mjs.common.exceptions.CoreException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.bson.*;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.beans.PropertyDescriptor;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * This is a utility class for use with Bson and Mongo.  It converts an object or Map or List
 * to it's Bson equivalent so it's compatible with Mongo.
 */
@SuppressWarnings({"rawtypes","deprecation"})
public class BsonConverter {
    /**
     * The log4j logger.
     */
    private static final Logger log = Logger.getLogger("Core");

    /**
     * Convert a bean to an array of String objects that contain the properties of the bean. This is
     * used to log out the details of the bean.
     *
     * @param bean
     *            Object
     * @return String[]
     */
    public static BsonValue objectToBson(Object bean) throws CoreException {
        try {
            return processBean(bean, 0);
        } catch (Exception e) {
            CoreException ex = new CoreException("Error converting bean (" + bean.getClass().getName() + ") to Bson.", e);
            throw ex;
        }
    }

    private static BsonValue processBean(Object bean, int level) throws CoreException {

        if (bean == null) {
            return null;
        }

        try {
            Object[] args = new Object[0];
            String fieldName = null;
            PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(bean.getClass());

            if (bean == null) {
                return null;
            } else if (bean instanceof Integer) {
                return new BsonInt32(((Integer)bean).intValue());
            } else if (bean instanceof Long) {
                return new BsonInt64(((Long)bean).longValue());
            } else if (bean instanceof Float) {
                return new BsonDouble(((Float)bean).doubleValue());
            } else if (bean instanceof Double) {
                return new BsonDouble(((Double)bean).doubleValue());
            } else if (bean instanceof Boolean) {
                return new BsonBoolean(((Boolean)bean).booleanValue());
            } else if (bean instanceof ObjectId) {
                return new BsonObjectId((ObjectId)bean);
            } else if (bean instanceof Date) {
                return new BsonInt64(((Date)bean).getTime());
            } else if (bean instanceof String) {
                return new BsonString(bean.toString());
            } else if (bean instanceof BigDecimal) {
                return new BsonDouble(((BigDecimal)bean).doubleValue());
            } else if (bean instanceof BigInteger) {
                return new BsonInt64(((BigInteger)bean).longValue());
            } else if (bean instanceof Map) {
                BsonDocument result = new BsonDocument();
                Map map = (Map)bean;
                Iterator keys = map.keySet().iterator();
                while (keys.hasNext()) {
                    String key = keys.next().toString();
                    Object val = map.get(key);

                    if (val instanceof Integer ||
                        val instanceof Long ||
                        val instanceof Float ||
                        val instanceof Double ||
                        val instanceof Boolean ||
                        val instanceof Date ||
                        val instanceof String ||
                        val instanceof BigDecimal ||
                        val instanceof BigInteger ||
                        val instanceof Collection ||
                        val instanceof Map) {
                        BsonValue value = processBean(val, level + 1);
                        result.put(key, value);
                    }
                }
                return result;
            } else if (bean instanceof Collection) {
                // Process the collection.
                Collection coll = (Collection) bean;
                Object[] list = coll.toArray();
                BsonArray result = new BsonArray();

                for (int k = 0; k < list.length; k++) {
                    Object val = list[k];
                    if (val instanceof Integer ||
                            val instanceof Long ||
                            val instanceof Float ||
                            val instanceof Double ||
                            val instanceof Boolean ||
                            val instanceof Date ||
                            val instanceof String ||
                            val instanceof BigDecimal ||
                            val instanceof BigInteger ||
                            val instanceof Collection ||
                            val instanceof Map) {
                        BsonValue value = processBean(val, level + 1);
                        result.add(value);
                    }
                }
                return result;
            } else {
                // Loop through the properties of the bean.
                //log.debug(indent(level) + "Extracting bean properties...   count=" + pds.length);
                BsonDocument result = new BsonDocument();
                if (pds == null || pds.length == 0) {
                    return result;
                }

                for (int i = 0; i < pds.length; i++) {
                    fieldName = pds[i].getName();

                    if (! (fieldName.equalsIgnoreCase("class") ||
                            fieldName.equalsIgnoreCase("parent") ||
                            fieldName.equalsIgnoreCase("declaringclass"))) {

                        // Get the getter method for this property.
                        Method method = pds[i].getReadMethod();

                        if (method != null) {
                            Object val = method.invoke(bean, args);

                            // Add indentation to simulate object hierarchy.
                            if (fieldName.endsWith("Class") || fieldName.endsWith("class")) {
                                // Skipping
                            } else {
                                String key = fieldName;
                                if (val instanceof Integer ||
                                    val instanceof Long ||
                                    val instanceof Float ||
                                    val instanceof Double ||
                                    val instanceof Boolean ||
                                    val instanceof Date ||
                                    val instanceof String ||
                                    val instanceof BigDecimal ||
                                    val instanceof BigInteger ||
                                    val instanceof Collection ||
                                    val instanceof Map) {
                                    BsonValue value = processBean(val, level + 1);
                                    result.put(key, value);
                                } else if (val == null) {
                                    // Skip.  Bson can't handle null values.
                                } else if (val instanceof ObjectId) {
                                    BsonValue value = processBean(((ObjectId)val).toHexString(), level + 1);
                                    result.put(key, value);
                                } else {
                                    throw new CoreException("Unrecognized data type: " + val.getClass().getName());
                                }
                            }
                        } else {
                            // No get method found.  Skipping.
                        }
                    }
                }
                return result;
            }
        } catch (Exception e) {
            String className = null;
            if (bean != null)
                className = bean.getClass().getName();
            throw new CoreException("Error converting bean to Bson: "
                                               + className + ".", e);
        }
    }

    private static Object reconstructBean(Object bean) throws CoreException {
        return reconstructBean(bean, 0);
    }

    private static Object reconstructBean(Object bean, int level) throws CoreException {

        if (bean == null) {
            return null;
        }

        try {
            Object[] args = new Object[0];
            Object[] writeArgs = new Object[1];
            String fieldName = null;
            PropertyDescriptor[] pds;

            if (bean instanceof Map) {
                Object result = bean;
                Map map = (Map)bean;
                boolean returnMap = false;
                Object instance = null;
                if (map.containsKey("type")) {
                    // Found a bean to reconstruct. Type attribute means was a POJO.
                    Class type = Class.forName(map.get("type").toString());
                    pds = BeanUtils.getPropertyDescriptors(type);
                    if (pds != null || pds.length > 0) {
                        result = type.newInstance();

                        for (int i = 0; i < pds.length; i++) {
                            fieldName = pds[i].getName();

                            if (!(fieldName.equalsIgnoreCase("class") ||
                                    fieldName.equalsIgnoreCase("parent") ||
                                    fieldName.equalsIgnoreCase("declaringclass"))) {

                                // Get the setter method for this property.
                                Method method = pds[i].getWriteMethod();
                                if (method != null) {
                                    try {
                                        writeArgs[0] = map.get(fieldName);
                                        if (fieldName.equals("_id")) {
                                            Map objId = (Map)writeArgs[0];
                                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

                                            writeArgs[0] = new ObjectId(formatter.parse(objId.get("date").toString()),
                                                                        Integer.parseInt(objId.get("machineIdentifier").toString()),
                                                                        Short.parseShort(objId.get("processIdentifier").toString()),
                                                                        Integer.parseInt(objId.get("counter").toString()));
                                        }
                                        method.invoke(result, writeArgs);
                                    } catch (Exception e) {
                                        throw new CoreException("Failed to call method " + method.getName() + " on " + result.getClass().getName() + " with value " + writeArgs[0] + ".", e);
                                    }
                                } else {
                                    // Read-only method.  Skip it.
                                }
                            }
                        }
                        if (! returnMap) {
                            return result;
                        }
                    }
                }
                Iterator keys = map.keySet().iterator();
                // Look for key/value pairs that need to be reconstructed.
                while (keys.hasNext()) {
                    String key = keys.next().toString();
                    Object val = map.get(key);

                    if (val instanceof Collection ||
                        val instanceof Map) {
                        Object value = reconstructBean(val, level + 1);
                        map.put(key, value);
                    }
                }
                return map;
            } else if (bean instanceof Collection) {
                // Process the collection.
                Collection coll = (Collection) bean;
                ArrayList result = new ArrayList();
                Object[] list = coll.toArray();

                for (int k = 0; k < list.length; k++) {
                    Object val = list[k];
                    if (val instanceof Collection ||
                        val instanceof Map) {
                        val = reconstructBean(val, level + 1);
                    }
                    result.add(val);
                }
                return result;
            } else {
                // Loop through the properties of the bean.
                //log.debug(indent(level) + "Extracting bean properties...   count=" + pds.length);
                pds = BeanUtils.getPropertyDescriptors(bean.getClass());
                if (pds == null || pds.length == 0) {
                    throw new CoreException("Failed to call reconstruct " + bean.getClass().getName() + " because PropertyDescriptors were null or empty.");
                }

                for (int i = 0; i < pds.length; i++) {
                    fieldName = pds[i].getName();

                    if (! (fieldName.equalsIgnoreCase("class") ||
                            fieldName.equalsIgnoreCase("parent") ||
                            fieldName.equalsIgnoreCase("declaringclass"))) {

                        // Get the getter method for this property.
                        Method method = pds[i].getReadMethod();

                        if (method != null) {
                            Object val = method.invoke(bean, args);

                            // Add indentation to simulate object hierarchy.
                            if (fieldName.endsWith("Class") || fieldName.endsWith("class")) {
                                // Skipping
                            } else {
                                String key = fieldName;
                                if (val instanceof Collection ||
                                    val instanceof Map) {
                                    Object value = reconstructBean(val, level + 1);
                                    Method writeMethod = pds[i].getWriteMethod();
                                    writeArgs[0] = value;
                                    method.invoke(bean, writeArgs);
                                }
                            }
                        } else {
                            // No get method found.  Skipping.
                        }
                    }
                }
                return bean;
            }
        } catch (Exception e) {
            String className = null;
            if (bean != null)
                className = bean.getClass().getName();
            throw new CoreException("Error converting bean to Bson: "
                    + className + ".", e);
        }
    }

    /**
     * This method reads a specified number of lines from the bottom of a
     * log file.
     * 
     * @param fileName String - The name and path of the log file to read.
     * @param bytesFromEndToStart int - The number of bytes from the end of the file
     *                                  to start reading from.
     * @param linesToReturn int - The number of lines to return.
     * @return ArrayList<String> - The bottom of the log file.
     * @throws Exception
     */
    public static ArrayList<String> readLogFile(String fileName, 
                                                int bytesFromEndToStart, 
                                                int linesToReturn) throws Exception {     
        ArrayList<String> list = new ArrayList<String>();

        // Open the file.
        File f = new File(fileName);
        long fileLength = f.length();
        RandomAccessFile file = new RandomAccessFile(f, "r");
        
        // Go to the end of the file minus a specified number of bytes.
        if (fileLength - bytesFromEndToStart > 0)
            file.seek(fileLength - bytesFromEndToStart);
        // Clear the first line.  It's probably a partial.
        String line = file.readLine(); 
        // Read the file from this point.
        while( line != null )
        {
            list.add(line);
            // Only keep a certain number of lines.
            if (list.size() > linesToReturn)
                list.remove(0);
            line = file.readLine();
        }

        return list;            
    }
}
