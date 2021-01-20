package baseball.mongo


import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoCursor
import com.mongodb.MongoClient
import com.mongodb.MongoClientURI
import com.mongodb.client.MongoDatabase
import com.mongodb.client.result.DeleteResult
import groovy.json.*
import org.bson.Document
import org.bson.conversions.Bson
import org.bson.types.ObjectId

import mjs.common.utils.BsonConverter
import mjs.common.utils.LogUtils


/**
 * Created by Dad on 6/14/16.
 */
class MongoManager {

    MongoClient client = null
    MongoDatabase db = null

    void open(String database) {
        open("localhost", 27017, database)
    }

    void open(String host, int port, String database) {
        //client = new MongoClient(host, port)
        //database = client.getDB(database)
        MongoClientURI uri  = new MongoClientURI("mongodb://$host:$port/$database")
        client = new MongoClient(uri)
        db = client.getDatabase(uri.getDatabase())
    }

    void close() {
        client.close()
        client = null
        db = null
    }

    ObjectId generateObjectId() {
        return new ObjectId()
    }

    ObjectId addToCollection(String collection, Object obj) {
        def id = generateObjectId()
        obj["_id"] = id
        //println "Adding new object to collection $collection."
        LogUtils.println(obj, "   ", true)
        //String json = new JsonBuilder(obj).toPrettyString()
        String json = new JsonBuilder(obj).toString()
        //println "addToCollection [$collection]  $json"
        privateAddToCollection(collection, json)
        return obj["_id"]
    }

    private void privateAddToCollection(String collection, String json) {
        MongoCollection<Document> table = db.getCollection(collection)
        Document doc = Document.parse(json)
        table.insertOne(doc)
    }

    /*
    ObjectId updateOne(String collection, Object obj) {
        obj._id = generateObjectId()
        println "Adding new object to collection $collection."
        LogUtils.println(obj, "   ", true)
        String json = new JsonBuilder(obj).toPrettyString()
        privateAddToCollection(collection, json)
        MongoCollection<Document> table = db.getCollection(collection)
        Bson doc = Document.parse(json)
        table.update(doc);
    }
    */

    long getCount(String collection) {
        MongoCollection<Document> table = db.getCollection(collection)
        return table.count()
    }

    long deleteOne(String collection, Map filterMap) {
        MongoCollection<Document> table = db.getCollection(collection)
        Bson filter = new BsonConverter().objectToBson(filterMap)
        DeleteResult result = table.deleteOne(filter)
        // Return the number of rows deleted.
        result.deletedCount
    }

    long deleteMany(String collection, Map filterMap) {
        MongoCollection<Document> table = db.getCollection(collection)
        Bson filter = new BsonConverter().objectToBson(filterMap)
        DeleteResult result = table.deleteMany(filter)
        // Return the number of rows deleted.
        result.deletedCount
    }

    long deleteAll(String collection) {
        MongoCollection<Document> table = db.getCollection(collection)
        Bson filter = new BsonConverter().objectToBson([:])
        DeleteResult result = table.deleteMany(filter)
        // Return the number of rows deleted.
        result.deletedCount
    }

    List findAll(String collection) {
        MongoCollection<Document> table = db.getCollection(collection)
        MongoCursor<Document> iter = table.find().iterator()
        def list = []
        JsonSlurper jsonSlurper = new JsonSlurper()
        while (iter.hasNext()) {
            Document nextDoc = iter.next()
            Object obj = jsonSlurper.parseText(nextDoc.toJson())
            list << new BsonConverter().reconstructBean(obj);
        }
        list
    }

    List find(String collection, Map filterMap) {
        BsonConverter bsonConverter = new BsonConverter()
        Bson filter = bsonConverter.objectToBson(filterMap)

        MongoCollection<Document> table = db.getCollection(collection)
        MongoCursor<Document> iter = table.find(filter).iterator()
        def list = []
        JsonSlurper jsonSlurper = new JsonSlurper()
        while (iter.hasNext()) {
            Document nextDoc = iter.next()
            String json = nextDoc.toJson()
            //println "find [$collection]  $json"
            Object obj = jsonSlurper.parseText(json)
            //LogUtils.println(obj, "   ", true)
            list << bsonConverter.reconstructBean(obj)
        }
        list
    }

    List find(String key, Object value) {
    }
}
