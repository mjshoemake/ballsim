 package mjs.model

//import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table
import javax.persistence.Transient;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * This is the data object or suitcase for a User. This data object
 * should not contain any business logic. This class is used by the
 * AuthenticationManager.
 */
 @Entity
 @Table(name="users")
 @JsonIgnoreProperties(ignoreUnknown = true)
class User extends ModelLoggable {
   /**
    * The user ID for this user. This is how users should be
    * referenced in the database.
    */
   @Id
   @GeneratedValue
   int user_pk = -1

     /**
    * The user's first name.
    */
   String fname = ""

   /**
    * The user's last name.
    */
   String lname = ""

   /**
    * The user login ID.
    */
   String username = ""

   /**
    * The user's password.
    */
   String password = ""

   /**
    * Is the login enabled for this user?
    */
   String login_enabled = "Y"

   @Transient
   public getName() {
       if (fname != null && ! fname.equals("") && lname != null && lname.equals("")) {
           return fname + " " + lname;
       } else if (fname != null && ! fname.equals("")) {
           return fname;
       } else if (lname != null && lname.equals("")) {
           return lname;
       } else {
           return "";
       }
   }


}
