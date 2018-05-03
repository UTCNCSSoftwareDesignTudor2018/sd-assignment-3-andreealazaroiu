package dataAccess.repositories;

import com.mongodb.*;
import dataAccess.entities.Article;
import dataAccess.entities.Writer;
import org.bson.Document;

import java.util.List;

public class WriterRepository {

    public Writer findWriterByUsername(String username,String password)
    {
        ConnectionFactory c=new ConnectionFactory();
        DB database=c.getDatabase();
        DBCollection writers=database.getCollection("writer");


        DBObject query = new BasicDBObject();
        query.put("username",username);
        query.put("password",password);
        DBCursor cursor= writers.find(query);
        DBObject obj=cursor.one();
        Writer w=new Writer((String)obj.get("name"),(String) obj.get("username"),(String) obj.get("password"));
        return w;

    }
    public Writer findWriterByUsername(String username)
    {
        ConnectionFactory c=new ConnectionFactory();
        DB database=c.getDatabase();
        DBCollection writers=database.getCollection("writer");


        DBObject query = new BasicDBObject();
        query.put("username",username);
        DBCursor cursor= writers.find(query);
        DBObject obj=cursor.one();
        Writer w=new Writer((String)obj.get("name"),(String) obj.get("username"),(String) obj.get("password"));
        return w;

    }
}
