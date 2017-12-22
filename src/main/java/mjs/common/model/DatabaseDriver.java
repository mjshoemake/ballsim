
package mjs.common.model;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.*;

import com.accenture.core.model.PaginatedList;
import com.accenture.core.model.fielddef.FieldDefMappingLoader;
import com.accenture.core.model.fielddef.FieldDefinition;
import com.accenture.core.model.fielddef.FieldDefinitionList;
import com.accenture.core.utils.BeanUtils;
import com.accenture.core.utils.CoreException;
import com.accenture.core.utils.FormatUtils;
import com.accenture.core.utils.LogUtils;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Bean;


/**
 * AbstractDatabaseDriver is used to encapsulate the creation of
 * connections to a database, the issuance of SQL statements and the
 * map of ResultSets to beans. Drivers can manage connections serially
 * or do a connection pool.
 */
public class DatabaseDriver
{
   /**
    * The Log4J logger used by this object.
    */
   protected Logger log = Logger.getLogger("Core");

   /**
    * The logger used to log out the result set information. This
    * allows the tracing of the result set data to be managed
    * separately from the standard tracing from the model.
    */
   private Logger logResults = Logger.getLogger("ResultSet");

   /**
    * The logger used to log out the result set information. This
    * allows the tracing of the result set data to be managed
    * separately from the standard tracing from the model.
    */
   private Logger logPerf = Logger.getLogger("Performance");

   /**
    * The connection manager for the connection pool.
    */
   private ConnectionManager connectionMgr;

   /**
    * The name of the project. This is used by the connection pool.
    */
   private String project = null;

   /**
    * This is intended to cache the mapping data rather than reloading
    * from the server each time.
    */
   private Hashtable mappingCache = new Hashtable();

   /**
    * This takes a mapping XML file and converts it into a Hashtable
    * for use by the rest of the system.
    */
   private FieldDefMappingLoader mappingLoader = new FieldDefMappingLoader();


   /**
    * Constructor.
    *
    * @param props
    * @throws DataLayerException
    */
   public DatabaseDriver(Properties props) throws DataLayerException
   {
      connectionMgr = ConnectionManager.getInstance(props);
      project = props.getProperty("project");
   }

   /**
    * This method uses the provided database Connection to open a
    * Statement and execute the specified SQL string.
    *
    * @param sql                     String
    * @param con                     Connection
    * @return                        int - The number of rows affected
    * by the query.
    * @exception DataLayerException
    */
   public int executeStatement(String sql, Connection con) throws DataLayerException
   {
      Statement stmt = null;

      if (con == null)
         throw new DataLayerException("Unable to execute SQL statement.  Connection is null.");

      log.debug("SQL: " + sql);
      try
      {
         stmt = con.createStatement();
         int result = stmt.executeUpdate(sql);
         return result;
      }
      catch (Exception e)
      {
         throw new DataLayerException(e.getMessage(), e);
      }
      finally
      {
         try
         {
            stmt.close();
         }
         catch (Exception e)
         {
            throw new DataLayerException(e.getMessage(), e);
         }
      }
   }

   /**
    * This method uses introspection to traverses through a ResultSet
    * and match properties from the provided class type to a column
    * from the database. If a match is found, the object property is
    * set with the field value from the ResultSet. This method loads
    * multiple copies of the provided object and stores them in the
    * PercList.
    *
    * @param rs                      ResultSet
    * @param mapping                 Hashtable
    * @param data                    PaginatedList
    * @return                        boolean True if successful,
    * otherwise false.
    * @exception DataLayerException
    */
   public boolean populateBeanList(ResultSet rs, Hashtable mapping, PaginatedList data) throws DataLayerException
   {
      try
      {
         int i = 0;
         long startTime = new Date().getTime();

         Class type = data.getDataType();
         PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(type, mapping);

         rs.next();
         while (! rs.isAfterLast())
         {
            i++;
            Object obj = generateBean(rs, mapping, type.newInstance(), pds);
            data.add(obj);
         }

         //if (logResults.isDebugEnabled())
         //{
         //   String[] lines = LogUtils.dataToStrings(data);
         //   for (int C = 0; C <= lines.length - 1; C++)
         //      logResults.debug("   " + lines[C]);
         //}

         if (logPerf.isDebugEnabled())
         {
            long endTime = new Date().getTime();
            String duration = LogUtils.longToDuration(endTime - startTime);
            logPerf.debug("Bean list populated.  duration: "+duration);
         }

         if (data.size() == 0)
            return false;
      }
      catch (DataLayerException dle)
      {
         throw dle;
      }
      catch (Exception e)
      {
         throw new DataLayerException("Error populating bean list from result set. " + e.getMessage(), e);
      }

      return true;
   }

   
   
   /**
    * This method uses introspection to traverses through a ResultSet
    * and match properties from the provided class type to a column
    * from the database. If a match is found, the object property is
    * set with the field value from the ResultSet. This method loads
    * multiple copies of the provided object and stores them in the
    * PercList. The difference between this method and the above is
    * that this will only populate maxRecords number of beans into the
    * list. The max Records of the list should be set to the instance
    * when created. eg. list = new PaginatedList(Bean.class,
    * 10,500);//here 500 is the max records and 10 is the page size
    *
    * @param rs                      ResultSet
    * @param mapping                 Hashtable
    * @param data                    PaginatedList
    * @return                        boolean True if successful,
    * otherwise false.
    * @exception DataLayerException
    */
   public boolean populateBeanListMaxRecords(ResultSet rs,
         Hashtable mapping, PaginatedList data)
          throws DataLayerException
   {
      try
      {
         int maxRecords = data.getMaxRecords();
         int i = 0;

         // if max records = 0 then dont add beans to list
         if (maxRecords > 0)
         {
            Class type = data.getDataType();
            PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(type, mapping);

            //max records is not 0 so go ahead have fun populating:)!
            while (rs.next())
            {
               //check to see if maxRecords number of beans have been added
               if (i == maxRecords)
               {
                  // max records have been added so stop iterating
                  break;
               }
               else
               {
                  //max number of records have not been added so continue adding
                  i++;
                  Object obj = generateBean(rs, mapping, type.newInstance(), pds);

                  data.add(obj);
               }
            }
         }

         if (logResults.isDebugEnabled())
         {
            String[] lines = LogUtils.dataToStrings(data);
            for (int C = 0; C <= lines.length - 1; C++)
               logResults.debug("   " + lines[C]);
         }   

         if (data.size() == 0)
            return false;
      }
      catch (DataLayerException dle)
      {
         throw dle;
      }
      catch (Exception e)
      {
         throw new DataLayerException("Error populating bean list from result set.", e);
      }
      return true;
   }
   //end of populateBeanListMaxRecords

   /**
    * Using introspection this method will traverse through the
    * ResultSet and look for properties from the provided Object that
    * match a field from the database. If a match is found that
    * property is set with the field value from the ResultSet.
    *
    * @param rs                   ResultSet
    * @param mapping              Hashtable
    * @param type                 Class
    * @return                     Object
    * @throws DataLayerException
    */
   public Object populateBean(ResultSet rs, Hashtable mapping, Class type) throws DataLayerException
   {
      try
      {
         return populateBean(rs, mapping, type, type.newInstance());
      }
      catch (DataLayerException e)
      {
         throw e;
      }
      catch (Exception e)
      {
         throw new DataLayerException("Error populating bean (probably error during instantiation of " + type.getName() + ".", e);
      }
   }

   /**
    * Using introspection this method will traverse through the
    * ResultSet and look for properties from the provided Object that
    * match a field from the database. If a match is found that
    * property is set with the field value from the ResultSet.
    *
    * @param rs                   ResultSet
    * @param mapping              Hashtable
    * @param type                 Class
    * @param gotoNextRow          boolean
    * @return                     Object
    * @throws DataLayerException
    */
   public Object populateBean(ResultSet rs, Hashtable mapping, Class type, boolean gotoNextRow) throws DataLayerException
   {
      try
      {
         return populateBean(rs, mapping, type, type.newInstance(), gotoNextRow);
      }
      catch (DataLayerException e)
      {
         throw e;
      }
      catch (Exception e)
      {
         throw new DataLayerException("Error populating bean (probably error during instantiation of " + type.getName() + ".", e);
      }
   }

   /**
    * Using introspection this method will traverse through the
    * ResultSet and look for properties from the provided Object that
    * match a field from the database. If a match is found that
    * property is set with the field value from the ResultSet.
    *
    * @param rs                      ResultSet
    * @param mapping                 Hashtable
    * @param type                    Class
    * @param bean                    Object (the object to populate)
    * @return                        Object
    * @exception DataLayerException
    */
   public Object populateBean(ResultSet rs, Hashtable mapping, Class type, Object bean) throws DataLayerException
   {
      try
      {
         return populateBean(rs, mapping, type, bean, true);
      }
      catch (DataLayerException e)
      {
         throw e;
      }
      catch (Exception e)
      {
         throw new DataLayerException("Error populating bean (probably error during instantiation of " + type.getName() + ".", e);
      }
   }

   /**
    * Using introspection this method will traverse through the
    * ResultSet and look for properties from the provided Object that
    * match a field from the database. If a match is found that
    * property is set with the field value from the ResultSet.
    *
    * @param rs                      ResultSet
    * @param mapping                 Hashtable
    * @param type                    Class
    * @param bean                    Object (the object to populate)
    * @param gotoNextRow             boolean
    * @return                        Object
    * @exception DataLayerException
    */
   public Object populateBean(ResultSet rs, Hashtable mapping, Class type, Object bean, boolean gotoNextRow) throws DataLayerException
   {
      try
      {
         long startTime = new Date().getTime();
         PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(type, mapping);
         if (gotoNextRow)
         {
            if (rs.next())
            {
               bean = generateBean(rs, mapping, bean, pds);
               String[] lines = LogUtils.dataToStrings(bean);
            }
            else
            {
               // No rows found.  Return null.
               bean = null;
            }
         }
         else
         {
            if (! rs.isAfterLast())
            {
               bean = generateBean(rs, mapping, bean, pds);
               String[] lines = LogUtils.dataToStrings(bean);
            }
            else
            {
               // No rows found.  Return null.
               bean = null;
            }
         }

         if (logPerf.isDebugEnabled())
         {
            long endTime = new Date().getTime();
            String duration = LogUtils.longToDuration(endTime - startTime);
            logPerf.debug("      Bean populated.  duration: "+duration);
         }
         return bean;
      }
      catch (DataLayerException dle)
      {
         throw dle;
      }
      catch (Exception e)
      {
         throw new DataLayerException("Error populating bean from result set.", e);
      }
   }

   /**
    * Create and populate a bean with the data from the specified
    * result set.
    */
   //private Object generateBean(ResultSet rs, Hashtable mapping, Class type, Object bean, PropertyDescriptor[] pds) throws DataLayerException
   private Object generateBean(ResultSet rs, Hashtable mapping, Object bean, PropertyDescriptor[] pds) throws DataLayerException
   {
      String fieldName = null;
      String fieldType = null;
      String propertyName = null;
      FieldDefinition fieldDef = null;
      Object[] args = new Object[1];
      boolean mapFound = false;
      String idField = "";
      String mapkey = "";
      String childtype = "";
      String childtypeMapping = "";
      String idValue = "";
      Method mapMethod = null;

      try
      {
         if (rs == null)
            throw new DataLayerException("Error generating bean from result set.  Result set is null.");

         if (mapping == null)
            throw new DataLayerException("Error generating bean from result set.  Mapping is null.");

         Properties cols = getColumnNames(rs);

         if (pds == null || pds.length == 0)
            throw new DataLayerException("Bean Descriptors are missing.");

         // Loop through the properties of the bean.
          for (int i = 0; i < pds.length; i++) {
              propertyName = pds[i].getName();
              if (!cols.containsKey(propertyName)) {
                  // This isn't a field in the result set.  Go to next item.
                  continue;
              }

              // Get the setter method for this property.
              Method method = pds[i].getWriteMethod();

              // Use hashtable to figure out the type.
              fieldDef = (FieldDefinition) (mapping.get(propertyName));
              fieldType = fieldDef.getType().toLowerCase();
              fieldName = propertyName;

              if (fieldType == null) {
                  // Field not found in mapping.  Throw exception.
                  throw new DataLayerException("Error loading bean.  Datatype for fieldname " + fieldName + " not found in mapping file.");
              } else if (fieldType.equals("map")) {
                  mapFound = true;
                  idField = fieldDef.getId();
                  mapkey = fieldDef.getMapkey();
                  childtype = fieldDef.getChildtype();
                  idValue = rs.getString(idField);
                  childtypeMapping = fieldDef.getChildtypeMapping();
                  mapMethod = method;
                  log.debug("Map found.");
              } else if (fieldType.equals("string")) {
                  // String.  Do formatting.  No conversion required.
                  args[0] = FormatUtils.formatString(rs.getString(fieldName), fieldDef);
              } else if (fieldType.equals("boolean")) {
                  // Read as a boolean.
                  log.debug("Formatting " + propertyName.toLowerCase() + " (boolean)...");

                  String value = rs.getString(fieldName).toLowerCase();
                  Boolean booleanValue = null;

                  if (value.startsWith("t") || value.startsWith("y") || value.startsWith("1"))
                      booleanValue = new Boolean(true);
                  else if (value.startsWith("f") || value.startsWith("n") || value.startsWith("0"))
                      booleanValue = new Boolean(false);
                  else
                      throw new DataLayerException("Error parsing value for field " + fieldName + ".  Unexpected value for type boolean (" + rs.getString(fieldName) + ".  Valid values are T or F, Y or N, 1 or 0.");

                  if (String.class.isAssignableFrom(pds[i].getPropertyType()))
                      args[0] = FormatUtils.formatBoolean(booleanValue, fieldDef);
                  else if (pds[i].getPropertyType().getName().equals("boolean"))
                      args[0] = booleanValue;
                  else
                      throw new DataLayerException("Error parsing value for field " + fieldName + ".  Unexpected value for type boolean (" + rs.getString(fieldName) + ".  Incorrect data type in bean (expected boolean or String).");
              } else if (fieldType.equals("int")) {
                  // Read as an int.
                  log.debug("Formatting " + propertyName.toLowerCase() + " (int)...");

                  Integer intValue = new Integer(rs.getInt(fieldName));

                  if (String.class.isAssignableFrom(pds[i].getPropertyType()))
                      args[0] = FormatUtils.formatInteger(intValue, fieldDef);
                  else if (Integer.class.isAssignableFrom(pds[i].getPropertyType()))
                      args[0] = intValue;
                  else if (pds[i].getPropertyType().getName().equals("int"))
                      args[0] = intValue;
                  else
                      throw new DataLayerException("Error parsing value for field " + fieldName + ".  Unexpected value for type int (" + rs.getString(fieldName) + ".  Incorrect data type in bean (expected int or String).");
              } else if (fieldType.equals("key")) {
                  // Read as an string.
                  log.debug("Formatting " + propertyName.toLowerCase() + " (key)...");

                  String stringValue = rs.getString(fieldName);

                  if (String.class.isAssignableFrom(pds[i].getPropertyType()))
                      args[0] = FormatUtils.formatString(stringValue, fieldDef);
                  else if (Integer.class.isAssignableFrom(pds[i].getPropertyType()))
                      args[0] = new Integer(Integer.parseInt(stringValue));
                  else
                      throw new DataLayerException("Error parsing value for field " + fieldName + ".  Unexpected value for type key (" + rs.getString(fieldName) + ".  Incorrect data type in bean (expected int or String).");
              } else if (fieldType.equals("long")) {
                  // Read as a long.
                  log.debug("Formatting " + propertyName.toLowerCase() + " (long)...");

                  Long longValue = new Long(rs.getLong(fieldName));

                  if (String.class.isAssignableFrom(pds[i].getPropertyType()))
                      args[0] = FormatUtils.formatLong(longValue, fieldDef);
                  else if (Integer.class.isAssignableFrom(pds[i].getPropertyType()))
                      args[0] = longValue;
                  else
                      throw new DataLayerException("Error parsing value for field " + fieldName + ".  Unexpected value for type long (" + rs.getString(fieldName) + ".  Incorrect data type in bean (expected long or String).");
              } else if (fieldType.equals("double") || fieldType.equals("float")) {
                  // Read as a double.
                  log.debug("Formatting " + propertyName.toLowerCase() + " (float)...");

                  BigDecimal floatValue = rs.getBigDecimal(fieldName);

                  if (String.class.isAssignableFrom(pds[i].getPropertyType()))
                      args[0] = FormatUtils.formatBigDecimal(floatValue, fieldDef);
                  else if (BigDecimal.class.isAssignableFrom(pds[i].getPropertyType()))
                      args[0] = floatValue;
                  else
                      throw new DataLayerException("Error parsing value for field " + fieldName + ".  Unexpected value for type float (" + rs.getString(fieldName) + ".  Incorrect data type in bean (expected BigDecimal or String).");
              } else if (fieldType.equals("date")) {
                  // Read as a Date.
                  log.debug("Formatting " + propertyName.toLowerCase() + " (date)...");

                  Date dateValue = rs.getTimestamp(fieldName);

                  if (String.class.isAssignableFrom(pds[i].getPropertyType()))
                      args[0] = FormatUtils.formatDate(dateValue, fieldDef);
                  else if (Date.class.isAssignableFrom(pds[i].getPropertyType()))
                      args[0] = dateValue;
                  else
                      throw new DataLayerException("Error parsing value for field " + fieldName + ".  Unexpected value for type date (" + rs.getString(fieldName) + ".  Incorrect data type in bean (expected Date or String).");
              } else {
                  // Throw exception.  Invalid data type.
                  throw new DataLayerException("Error parsing value for field " + fieldName + ".  Invalid field type: " + fieldType + ".");
              }

              if (args[0] == null)
                  continue;

              if (fieldType.equals("map")) {
                  // Skip calling set method.  Not time yet.  Need to create the map object.
              } else {
                  if (method == null)
                      throw new DataLayerException("Error generating bean from result set.  Write method for property " + fieldType + " is null.");
                  method.invoke(bean, args);
              }
          }

         if (mapFound) {
             Map positions = loadMap(rs, idField, idValue, mapkey, childtype, childtypeMapping, cols);
             args[0] = positions;
             mapMethod.invoke(bean, args);
             log.debug("Bean processed: " + idValue);
         } else {
             rs.next();
         }

         return bean;
      }
      catch (Exception e)
      {
         log.debug("   Processing property: " + fieldName + " (" + fieldType + ")...");
          log.error(e.getMessage(), e);
         throw new DataLayerException("Error loading bean (column: " + fieldName + ", type: " + fieldType + "). " + e.getMessage(), e);
      }
   }

    private Map loadMap(ResultSet rs, String idField, String idValue, String mapkey, String childtype, String childtypeMapping, Properties cols) throws DataLayerException
    {
        String fieldName = null;
        String fieldType = null;
        String propertyName = null;
        FieldDefinition fieldDef = null;
        Object[] args = new Object[1];
        Class childClass = null;
        Map<String, Object> result = new HashMap();

        try {
            childClass = Class.forName(childtype);
        } catch (ClassNotFoundException ex) {
            throw new DataLayerException("Error generating bean from result set.  Map creation: Child Type from FieldDefinition is not a valid class.");
        }

        try
        {
            if (idField == null || idField.equals(""))
                throw new DataLayerException("Error generating bean from result set.  Map creation: ID from FieldDefinition is empty.");

            if (mapkey == null || mapkey.equals(""))
                throw new DataLayerException("Error generating bean from result set.  Map creation: MapKey from FieldDefinition is empty.");

            if (idValue == null || idValue.equals(""))
                throw new DataLayerException("Error generating bean from result set.  Map creation: ID value from current row is empty.");

            if (childtype == null || childtype.equals(""))
                throw new DataLayerException("Error generating bean from result set.  Map creation: Child type from FieldDefinition is empty.");

            if (childtypeMapping == null || childtypeMapping.equals(""))
                throw new DataLayerException("Error generating bean from result set.  Map creation: Child type mapping from FieldDefinition is empty.");

            if (rs == null)
                throw new DataLayerException("Error generating bean from result set.  Result set is null.");

            // Get mapping file.
            Hashtable mapping = loadMapping(childtypeMapping);

            if (mapping == null || mapping.size() == 0)
                throw new DataLayerException("Error generating bean from result set.  Map creation: No fields found for child type mapping.");

            // Get property descriptors.
            PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(childClass, mapping);

            if (pds == null || pds.length == 0)
                throw new DataLayerException("Bean Descriptors are missing.");

            // Loop through the properties of the bean.
            while (! rs.isAfterLast() && rs.getString(idField).equals(idValue)) {
                Object bean = childClass.newInstance();
                for (int i = 0; i < pds.length; i++)
                {
                    propertyName = pds[i].getName();
                    if (! cols.containsKey(propertyName))
                    {
                        // This isn't a field in the result set.  Go to next item.
                        continue;
                    }

                    // Get the setter method for this property.
                    Method method = pds[i].getWriteMethod();

                    // Use hashtable to figure out the type.
                    fieldDef = (FieldDefinition)(mapping.get(propertyName));
                    fieldType = fieldDef.getType().toLowerCase();
                    fieldName = propertyName;

                    if (fieldType == null)
                    {
                        // Field not found in mapping.  Throw exception.
                        throw new DataLayerException("Error loading bean.  Datatype for fieldname " + fieldName + " not found in mapping file.");
                    }
                    else if (fieldType.equals("string"))
                    {
                        // String.  Do formatting.  No conversion required.
                        args[0] = FormatUtils.formatString(rs.getString(fieldName), fieldDef);
                    }
                    else if (fieldType.equals("boolean"))
                    {
                        // Read as a boolean.
                        log.debug("Formatting " + propertyName.toLowerCase() + " (boolean)...");

                        String value = rs.getString(fieldName).toLowerCase();
                        Boolean booleanValue = null;

                        if (value.startsWith("t") || value.startsWith("y") || value.startsWith("1"))
                            booleanValue = new Boolean(true);
                        else if (value.startsWith("f") || value.startsWith("n") || value.startsWith("0"))
                            booleanValue = new Boolean(false);
                        else
                            throw new DataLayerException("Error parsing value for field " + fieldName + ".  Unexpected value for type boolean (" + rs.getString(fieldName) + ".  Valid values are T or F, Y or N, 1 or 0.");

                        if (String.class.isAssignableFrom(pds[i].getPropertyType()))
                            args[0] = FormatUtils.formatBoolean(booleanValue, fieldDef);
                        else if (pds[i].getPropertyType().getName().equals("boolean"))
                            args[0] = booleanValue;
                        else
                            throw new DataLayerException("Error parsing value for field " + fieldName + ".  Unexpected value for type boolean (" + rs.getString(fieldName) + ".  Incorrect data type in bean (expected boolean or String).");
                    }
                    else if (fieldType.equals("int"))
                    {
                        // Read as an int.
                        log.debug("Formatting " + propertyName.toLowerCase() + " (int)...");

                        Integer intValue = new Integer(rs.getInt(fieldName));

                        if (String.class.isAssignableFrom(pds[i].getPropertyType()))
                            args[0] = FormatUtils.formatInteger(intValue, fieldDef);
                        else if (Integer.class.isAssignableFrom(pds[i].getPropertyType()))
                            args[0] = intValue;
                        else if (pds[i].getPropertyType().getName().equals("int"))
                            args[0] = intValue;
                        else
                            throw new DataLayerException("Error parsing value for field " + fieldName + ".  Unexpected value for type int (" + rs.getString(fieldName) + ".  Incorrect data type in bean (expected int or String).");
                    }
                    else if (fieldType.equals("key"))
                    {
                        // Read as an string.
                        log.debug("Formatting " + propertyName.toLowerCase() + " (key)...");

                        String stringValue = rs.getString(fieldName);

                        if (String.class.isAssignableFrom(pds[i].getPropertyType()))
                            args[0] = FormatUtils.formatString(stringValue, fieldDef);
                        else if (Integer.class.isAssignableFrom(pds[i].getPropertyType()))
                            args[0] = new Integer(Integer.parseInt(stringValue));
                        else
                            throw new DataLayerException("Error parsing value for field " + fieldName + ".  Unexpected value for type key (" + rs.getString(fieldName) + ".  Incorrect data type in bean (expected int or String).");
                    }
                    else if (fieldType.equals("long"))
                    {
                        // Read as a long.
                        log.debug("Formatting " + propertyName.toLowerCase() + " (long)...");

                        Long longValue = new Long(rs.getLong(fieldName));

                        if (String.class.isAssignableFrom(pds[i].getPropertyType()))
                            args[0] = FormatUtils.formatLong(longValue, fieldDef);
                        else if (Integer.class.isAssignableFrom(pds[i].getPropertyType()))
                            args[0] = longValue;
                        else
                            throw new DataLayerException("Error parsing value for field " + fieldName + ".  Unexpected value for type long (" + rs.getString(fieldName) + ".  Incorrect data type in bean (expected long or String).");
                    }
                    else if (fieldType.equals("double") || fieldType.equals("float"))
                    {
                        // Read as a double.
                        log.debug("Formatting " + propertyName.toLowerCase() + " (float)...");

                        BigDecimal floatValue = rs.getBigDecimal(fieldName);

                        if (String.class.isAssignableFrom(pds[i].getPropertyType()))
                            args[0] = FormatUtils.formatBigDecimal(floatValue, fieldDef);
                        else if (BigDecimal.class.isAssignableFrom(pds[i].getPropertyType()))
                            args[0] = floatValue;
                        else
                            throw new DataLayerException("Error parsing value for field " + fieldName + ".  Unexpected value for type float (" + rs.getString(fieldName) + ".  Incorrect data type in bean (expected BigDecimal or String).");
                    }
                    else if (fieldType.equals("date"))
                    {
                        // Read as a Date.
                        log.debug("Formatting " + propertyName.toLowerCase() + " (date)...");

                        Date dateValue = rs.getTimestamp(fieldName);

                        if (String.class.isAssignableFrom(pds[i].getPropertyType()))
                            args[0] = FormatUtils.formatDate(dateValue, fieldDef);
                        else if (Date.class.isAssignableFrom(pds[i].getPropertyType()))
                            args[0] = dateValue;
                        else
                            throw new DataLayerException("Error parsing value for field " + fieldName + ".  Unexpected value for type date (" + rs.getString(fieldName) + ".  Incorrect data type in bean (expected Date or String).");
                    }
                    else
                    {
                        // Throw exception.  Invalid data type.
                        throw new DataLayerException("Error parsing value for field " + fieldName + ".  Invalid field type: " + fieldType + ".");
                    }

                    if (args[0] == null)
                        continue;

                    if (method == null)
                        throw new DataLayerException("Error generating bean from result set.  Write method for property " + fieldType + " is null.");

                    method.invoke(bean, args);
                }
                result.put(rs.getString(mapkey), bean);
                rs.next();
            }

            return result;
        }
        catch (Exception e)
        {
            log.debug("   Processing property: " + fieldName + " (" + fieldType + ")...");
            throw new DataLayerException("Error loading bean (column: " + fieldName + ", type: " + fieldType + ")." + e.getMessage(), e);
        }
    }

   /**
    * This method gathers the meta data form an active ResultSet and
    * loads all the field names in a Properties object. <p>
    *
    * These field names will be compared to the property names of an
    * PercBean while the ResultSet is being processed.
    *
    * @param rs                      ResultSet
    * @return                        Properties
    * @exception DataLayerException
    */
   public Properties getColumnNames(ResultSet rs) throws DataLayerException
   {
      Properties cols = new Properties();
      String key = "";
      String field = "";

      try
      {
         ResultSetMetaData rmd = rs.getMetaData();
         int numcols = rmd.getColumnCount();

         for (int i = 1; i <= numcols; i++)
         {
            field = rmd.getColumnName(i);
            key = rmd.getColumnLabel(i);

            cols.setProperty(key, field);
         }
      }
      catch (Exception e)
      {
         throw new DataLayerException("Error trying to extract column names from the result set.", e);
      }

      return cols;
   }

   /**
    * The name of the project. This is used by the connection pool.
    *
    * @return   String
    */
   public String getProject()
   {
      return project;
   }

   /**
    * The name of the project. This is used by the connection pool.
    *
    * @param newProject  String
    */
   public void setProject(String newProject)
   {
      project = newProject;
   }

   /**
    * Retrives a connection from the Connection Pool Manager.
    *
    * @return                        Connection
    * @exception DataLayerException
    */
   public synchronized Connection getConnection() throws DataLayerException
   {
      if (connectionMgr == null)
         throw new DataLayerException("Unable to get connection from the connection pool.  ConnectionManager is null.");

      // Using a 30s timeout so a null connection will not
      // be returned. In the future the application
      // will handle a null connection and the timeout
      // value will be provided via configuration files
      return connectionMgr.getConnection(project, 30000);
   }

   /**
    * Releases the Connection object. Required by the
    * DatabaseTransaction class.
    *
    * @param con  Connection
    */
   public synchronized void releaseConnection(Connection con)
   {
      connectionMgr.freeConnection(project, con);
   }

   /**
    * Loads field definition mapping file and returns it as a
    * hashtable where the field name is the hashtable key and the
    * field type is the lookup value. The field type should be one of
    * the following values and is case insensitive: <pre>
    *    string
    *    int
    *    long
    *    double
    *    date
    *    boolean
    * </pre> If the mapping data for a file already exists in the
    * cache, it will return the data from the cache.
    *
    * @param mappingFile        Description of Parameter
    * @return                   Description of Return Value
    * @exception CoreException  Description of Exception
    */
   public Hashtable loadMapping(String mappingFile) throws CoreException
   {
      if (mappingCache.containsKey(mappingFile))
      {
         // Load from cache rather than performing the file I/O.
         Hashtable oldMapping = (Hashtable)mappingCache.get(mappingFile);

         return oldMapping;
      }
      else
      {
         // Not in cache.  Load from disk.
         log.debug("Loading mapping Hashtable from disk...  Not found in cache.");

         Hashtable newMapping = mappingLoader.loadMapping(mappingFile);

         mappingCache.put(mappingFile, newMapping);
         return newMapping;
      }
   }
}

// Change Log:
//
// 01/10/2005  MJS   epSAP Minor Enhancements 2001 Phase 1
