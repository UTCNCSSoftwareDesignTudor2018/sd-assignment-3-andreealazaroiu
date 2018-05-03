package entities;

import client.Response;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FormatClass {

    public Writer jsonToWriter(String jsonString) throws IOException
    {ObjectMapper mapper=new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(jsonString,Writer.class);}

    public Article jsonToArticle(String jsonString) throws IOException
    {ObjectMapper mapper=new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(jsonString,Article.class);}

    public String writerToJson(Writer obj) throws JsonProcessingException {
        ObjectMapper mapper=new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.writeValueAsString(obj);
    }
    public String articleToJson(Article obj) throws JsonProcessingException {
        ObjectMapper mapper=new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.writeValueAsString(obj);
    }
    public String articlesToJson(List<Article> a) throws JsonProcessingException {
        ObjectMapper mapper=new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.writeValueAsString(a);
    }
    public List<Article> jsonToArticles(String a) throws IOException { ObjectMapper mapper=new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        CollectionType javaType = mapper.getTypeFactory().constructCollectionType(List.class, Article.class);
        return mapper.readValue(a,javaType);
    }

    public String responseToJson(Response response) throws JsonProcessingException {
        ObjectMapper mapper=new ObjectMapper();
        return mapper.writeValueAsString(response);
    }

    public Response jsonToResponse(String json) throws IOException {
        ObjectMapper mapper=new ObjectMapper();
        return mapper.readValue(json, Response.class);
    }

}
