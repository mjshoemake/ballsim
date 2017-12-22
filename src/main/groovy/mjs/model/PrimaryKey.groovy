package mjs.model

/**
 * This is the data object or suitcase for an HTTP response to the caller.
 */
class PrimaryKey extends ModelLoggable {

    /**
     * The primary key to be returned to the caller.
     */
    String primaryKey = ""

    /**
     * Constructor.
     */
    PrimaryKey(def pkValue) {
        this.primaryKey = pkValue
    }
}
