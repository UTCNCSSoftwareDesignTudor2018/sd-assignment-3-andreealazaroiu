import com.fasterxml.jackson.core.JsonProcessingException;
import dataAccess.entities.Article;
import dataAccess.entities.Writer;
import dataAccess.repositories.ArticleRepository;
import dataAccess.repositories.FormatClass;
import dataAccess.repositories.WriterRepository;
import org.junit.Test;
import org.junit.jupiter.api.Disabled;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Testing {


    @Test
    @Disabled
    public void testInsert()
    {
        Article a =new Article("In lumea apei","pasari si oameni","In lumea apei ...",new Writer("Lazaroiu Andreea","andreealazaroiu","111"));

        ArticleRepository ar=new ArticleRepository();
        ar.insertArticle(a);
    }

    @Test
    @Disabled
    public void testDelete()
    {
        Article a =new Article("In lumea apei","pasari si oameni","In lumea apei ...",new Writer("Lazaroiu Andreea","andreealazaroiu", "111"));

        ArticleRepository ar=new ArticleRepository();
        ar.deleteArticle("In lumea apei");
    }

    @Test
    @Disabled
    public void testFind(){

        Article a =new Article("In lumea apei","pasari si oameni","In lumea apei ...",new Writer("Lazaroiu Andreea","andreealazaroiu", "11"));

        ArticleRepository ar=new ArticleRepository();

        assertEquals(a,ar.findArticle("In lumea apei"));
    }


    @Test
    @Disabled
    public void testWriter()
    {
        WriterRepository wr=new WriterRepository();
        System.out.println(wr.findWriterByUsername("andreealazaroiu"));
    }

    @Test
    @Disabled
    public void testFormatClass()
    {
        Article a =new Article("In lumea apei","pasari si oameni","In lumea apei ...",new Writer("Lazaroiu Andreea","andreealazaroiu","11"));
        FormatClass formatClass=new FormatClass();
        try {
            System.out.println(formatClass.articleToJson(a));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Disabled
    public void testFormat() throws IOException {
        FormatClass formatClass=new FormatClass();
        Article a =new Article("In lumea apei","pasari si oameni","In lumea apei ...",new Writer("Lazaroiu Andreea","andreealazaroiu","11"));
        String j=formatClass.articleToJson(a);

        System.out.println(formatClass.jsonToArticle(j).getTitle());
    }
}
