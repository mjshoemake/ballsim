package mjs.common.model;



/**
 * A DataLayerException is an CoreException used specifically for the
 * data access layer found in com.accenture.core.model.
 */
public class DataLayerException extends mjs.common.exceptions.CoreException
{
   /**
    * Constructor.
    *
    * @param s
    */
   public DataLayerException(String s)
   {
      super(s);
   }

   /**
    * Constructor.
    *
    * @param s
    * @param e
    */
   public DataLayerException(String s, Exception e)
   {
      super(s, e);
   }

}
