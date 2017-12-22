package com.accenture.core.model;

import java.util.ArrayList;
import java.util.Collection;
import com.accenture.core.utils.AbstractArrayList;
import org.apache.log4j.Logger;


/**
 * The list of objects retrieved from the database. This list includes
 * pagination functionality so the list can be retrieved from the
 * database once and paginated from there.
 *
 * @author   mshoemake
 */
public class PaginatedList extends ArrayList
{
   /**
    * The log4j logger to use when writing log messages. This is
    * populated by extracting the logger using the Logger category.
    * The default Logger category is "Model".
    */
   protected Logger log = Logger.getLogger("Model");

   /**
    * The max number of records allowed in the list. Defaults to 1500.
    */
   private int maxRecords = 1500;

   /**
    * The number of records on a page. Defaults to 10.
    */
   private int pageLength = 10;

   /**
    * The first record of the current page.
    */
   private int startOfPage = 0;

   /**
    * The class type used to dynamically load beans into this list.
    */
   private Class dataType = null;


   /**
    * Constructor.
    *
    * @param type           Description of Parameter
    * @param newPageLength  Description of Parameter
    * @param newMaxRecords  Description of Parameter
    */
   public PaginatedList(Class type, int newPageLength, int newMaxRecords)
   {
      super();
      this.dataType = type;
      this.pageLength = newPageLength;
      this.maxRecords = newMaxRecords;
   }

   /**
    * Is the number of items in the list higher than maxRecords?
    *
    * @return   The value of the Overflowed property.
    */
   private boolean isOverflowed()
   {
      return size() >= maxRecords;
   }

   /**
    * Appends items in the specified list to this one.
    *
    * @param list
    * @throws DataLayerException
    */
   public void appendList(Collection list)
   {
      Object items[] = list.toArray();
      int i = 0;

      while ((i < items.length) && ! isOverflowed())
      {
         Object o = items[i];

         add(o);
         i++;
      }
   }

   /**
    * Returns an ArrayList of items on the current page.
    *
    * @return   ArrayList
    */
   public ArrayList getItemsOnCurrentPage()
   {
      ArrayList pageList = new ArrayList();

      // Populate pageList with only the items on this page.
      for (int C = startOfPage; C <= getEndOfPage(); C++)
         pageList.add(this.get(C));
      return pageList;
   }

   /**
    * Returns an ArrayList of all items in this list.
    *
    * @return   ArrayList
    */
   public ArrayList getEntireList()
   {
      return (ArrayList)clone();
   }

   /**
    * The first record of the current page.
    *
    * @return   The value of the StartOfPage property.
    */
   public int getStartOfPage()
   {
      return startOfPage;
   }

   /**
    * The last record of the current page.
    *
    * @return   The value of the EndOfPage property.
    */
   public int getEndOfPage()
   {
      int endOfPage = startOfPage + pageLength - 1;

      if (endOfPage > this.size() - 1)
         endOfPage = this.size() - 1;
      return endOfPage;
   }

   /**
    * The class type used to dynamically load beans into this list.
    *
    * @return   The value of the DataType property.
    */
   public Class getDataType()
   {
      return dataType;
   }

   /**
    * The class type used to dynamically load beans into this list.
    *
    * @param newDataType  The new DataType value.
    */
   public void setDataType(Class newDataType)
   {
      dataType = newDataType;
   }

   /**
    * The max number of records allowed in the list. Defaults to 1500.
    *
    * @param newMaxRecords  The new MaxRecords value.
    */
   public void setMaxRecords(int newMaxRecords)
   {
      maxRecords = newMaxRecords;
   }

   /**
    * The max number of records allowed in the list. Defaults to 1500.
    *
    * @return   The value of the MaxRecords property.
    */
   public int getMaxRecords()
   {
      return maxRecords;
   }

   /**
    * The number of records on a page. Defaults to 10.
    *
    * @return   The value of the PageLength property.
    */
   public int getPageLength()
   {
      return pageLength;
   }

   /**
    * The number of records on a page. Defaults to 10.
    *
    * @param newPageLength  The new PageLength value.
    */
   public void setPageLength(int newPageLength)
   {
      pageLength = newPageLength;
   }

   /**
    * Is this record number on this page?
    *
    * @param recordNum    Description of Parameter
    * @param startOfPage
    * @return             The value of the RecordOnThisPage property.
    */
   private boolean isRecordOnThisPage(int recordNum, int startOfPage)
   {
      return recordNum >= startOfPage && recordNum <= (startOfPage + pageLength - 1);
   }

   /**
    * Go to the specified record number and update the pagination
    * accordingly.
    *
    * @param newRecord
    */
   public void setCurrentRecord(int newRecord)
   {
      if (newRecord >= 0 && newRecord < size())
      {
         if (startOfPage <= newRecord && (startOfPage + pageLength - 1) >= newRecord)
         {
            // Already on the right page.  No change
            return;
         }
         else if (newRecord <= size() - 1 && newRecord >= size() - pageLength)
         {
            // Go to last page.
            int remainder = size() % pageLength;

            if (remainder == 0)
               remainder = 10;
            setStartOfPage(size() - remainder);
         }
         else if (newRecord <= pageLength - 1)
         {
            // Go to first page.
            setStartOfPage(0);
         }
         else if (newRecord >= startOfPage + pageLength)
         {
            // Keep going to next page until you find the right
            // one.
            while (! isRecordOnThisPage(newRecord, startOfPage))
            {
               startOfPage = startOfPage + pageLength;
            }
         }
         else if (newRecord < startOfPage)
         {
            // Keep going to the previous page until you find the
            // right one.
            while (! isRecordOnThisPage(newRecord, startOfPage))
            {
               startOfPage = startOfPage - pageLength;
            }
         }
      }
   }

   /**
    * The first record of the current page.
    *
    * @param newStartOfPage  The new StartOfPage value.
    */
   public void setStartOfPage(int newStartOfPage)
   {
      startOfPage = newStartOfPage;
   }

   /**
    * Moves to the first page.
    *
    * @return                        Description of Return Value
    * @exception DataLayerException
    */
   public boolean firstPage() throws DataLayerException
   {
      if (startOfPage == 0)
      {
         // Already on the first page.
         return false;
      }
      else
      {
         // Go to the first record in the list.
         setCurrentRecord(0);
         return true;
      }
   }

   /**
    * Moves to the previous page.
    *
    * @return                        Description of Return Value
    * @exception DataLayerException
    */
   public boolean lastPage() throws DataLayerException
   {
      if (startOfPage + pageLength >= size())
      {
         // Already on the last page.
         return false;
      }
      else
      {
         // Go to the last record in the list.
         if (pageLength >= size())
         {
            // Last page is the first page.
            setCurrentRecord(0);
         }
         else
         {
            // Go to the first record of the last page.
            int remainder = size() % pageLength;

            if (remainder == 0)
               remainder = 10;
            setCurrentRecord(size() - remainder);
         }

         return true;
      }
   }

   /**
    * Moves the current record pointer to the next record.
    *
    * @return                        Description of Return Value
    */
   public boolean nextPage()
   {
      if (startOfPage + pageLength >= size())
      {
         // Already on the last page.
         return false;
      }
      else
      {
         setStartOfPage(startOfPage + pageLength);
         setCurrentRecord(startOfPage);
         return true;
      }
   }

   /**
    * Moves the current record pointer to the previous record.
    *
    * @return                        Description of Return Value
    */
   public boolean previousPage()
   {
      if (startOfPage == 0)
      {
         // Already on the first page.
         return false;
      }
      else
      {
         setStartOfPage(startOfPage - pageLength);
         setCurrentRecord(startOfPage);
         return true;
      }
   }

   /**
    * Moves the current record pointer to the specified index.
    *
    * @param index
    * @return                        Description of Return Value
    * @exception DataLayerException
    */
   public boolean jump(int index) throws DataLayerException
   {
      // Is this a valid index?
      if (index - 1 >= 0)
      {
         if (index <= size())
         {
            setCurrentRecord(index - 1);
         }
         return true;
      }
      return false;
   }

   /**
    * The current page in the list.
    *
    * @return   int
    */
   public int getCurrentPage()
   {
      int pageNum = (getEndOfPage() + 1) / pageLength;
      int remainder = (getEndOfPage() + 1) % pageLength;

      if (remainder > 0)
         pageNum++;
      return pageNum;
   }

   /**
    * The last page in the list.
    *
    * @return   int
    */
   public int getPageCount()
   {
      int pageNum = size() / pageLength;
      int remainder = size() % pageLength;

      if (remainder > 0)
         pageNum++;
      return pageNum;
   }

}
