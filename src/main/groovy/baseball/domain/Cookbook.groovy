 package baseball.domain


 import javax.persistence.Entity
 import javax.persistence.GeneratedValue
 import javax.persistence.Id

 //import javax.persistence.Column;
 import javax.persistence.Table

 /**
 * This is the data object or suitcase for a Meal. This data object
 * should not contain any business logic.
 */
 @Entity
 @Table(name="cookbooks")
class Cookbook extends ModelLoggable {
     @Id
     @GeneratedValue
     int cookbooks_pk = -1
     String name = ""
 }
