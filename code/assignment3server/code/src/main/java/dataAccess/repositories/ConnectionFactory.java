package dataAccess.repositories;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

import java.util.Collection;

public class ConnectionFactory {


    public static MongoClient mongoClient=new MongoClient("localhost",27017);

    public static DB getDatabase()
    {
        return mongoClient.getDB("articledatabase");
    }

}
