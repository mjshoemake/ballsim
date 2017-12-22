package com.accenture.core.model.fielddef;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.accenture.core.utils.*;


/**
 * This takes an XML file and converts it into an object, or converts
 * objects into XML. This allows the actions to return XML data in the
 * result to the controller.
 *
 * @author   mshoemake
 */
public class FieldDefMappingLoader extends Loggable
{
   /**
    * Constructor.
    *
    */
   public FieldDefMappingLoader()
   {
      super();
   }

   /**
    * Loads field definition mapping file and returns it as a
    * hashtable where the field name is the hashtable key and the
    * field type is the lookup value. The field type should be one of
    * the following values and is case insensitive: <pre>
    *    key
    *    string
    *    int
    *    long
    *    float
    *    date
    *    boolean
    * </pre>
    *
    * @param mappingFile        Description of Parameter
    * @return                   Description of Return Value
    * @exception CoreException  Description of Exception
    */
   public Hashtable loadMapping(String mappingFile) throws CoreException {
      String packageName = "com.accenture.core.model.fielddef";

      if (mappingFile == null)
         throw new CoreException("Error occured loading mapping file.  MappingFile is null.");

      try
      {
         InputStream stream = this.getClass().getResourceAsStream(mappingFile);
         if (stream == null)
            throw new CoreException("Error occured loading mapping file.  InputStream is null.");

         String mapping = FileUtils.inputStreamToString(stream);
         java.net.URL url = CastorObjectConverter.class.getResource("/mapping/FieldDefMapping.xml");
         FieldDefinitionList defs = (FieldDefinitionList)CastorObjectConverter.convertXMLToObject(mapping, FieldDefinitionList.class, url);
         return populateHashtable(defs);
      }
      catch (java.lang.Exception e)
      {
         throw new CoreException("Error loading database table field type definitions.", e);
      }
   }

    private Hashtable populateHashtable(FieldDefinitionList list) {
        Hashtable result = new Hashtable();
        ArrayList input = list.getField();
        for (int i=0; i <= input.size()-1; i++) {
            FieldDefinition def = (FieldDefinition)input.get(i);
            result.put(def.getName(), def);
        }
        return result;
    }

}
