package baseball.mongo

import org.bson.types.ObjectId

/**
 * This class represents the ObjectId of a Mongo data object.
 */
class MongoObjectId {

    final String value = ""

    MongoObjectId(ObjectId id) {
        value = id.toHexString()
    }
}
