package dataAccess.repositories;

import com.mongodb.*;
import dataAccess.entities.Article;

import java.util.ArrayList;
import java.util.List;

public class ArticleRepository {


    public void insertArticle(Article article)
    {
        ConnectionFactory cn=new ConnectionFactory();
        DB database=cn.getDatabase();
        DBCollection articles=database.getCollection("article");

        DBObject newArticle=new BasicDBObject ("title", article.getTitle())
                                            .append("abstract", article.getArticleAbstract())
                                            .append("body",article.getBody())
                                            .append("writer",article.getWriter().getUsername());

        articles.insert(newArticle);

    }

    public void deleteArticle(String article)
    {
        ConnectionFactory cn=new ConnectionFactory();
        DB database=cn.getDatabase();
        DBCollection articles=database.getCollection("article");

        DBObject query = new BasicDBObject("title", article);
        DBCursor cursor =articles.find(query);
        articles.remove(cursor.one());

    }

    public Article findArticle(String article)
    {

        ConnectionFactory cn=new ConnectionFactory();
        WriterRepository wr=new WriterRepository();
        DB database=cn.getDatabase();
        DBCollection articles=database.getCollection("article");

        DBObject query = new BasicDBObject("title", article);
        DBCursor cursor =articles.find(query);
        DBObject obj=cursor.one();
        return new Article((String)obj.get("title"),(String) obj.get("abstract"),(String) obj.get("body"),wr.findWriterByUsername((String)obj .get("writer")));


    }

    public List<Article> getArticles()
    {
        ConnectionFactory cn=new ConnectionFactory();
        DB database=cn.getDatabase();
        DBCollection articles=database.getCollection("article");
        WriterRepository wr=new WriterRepository();
        List<Article> arts=new ArrayList<>();
        DBCursor cursor = articles.find();
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();
            arts.add(new Article((String)obj.get("title"),(String)obj.get("abstract"),(String)obj.get("body"),wr.findWriterByUsername((String)obj.get("writer"))));
        }
        System.out.println(arts);
        return arts;
    }



}
