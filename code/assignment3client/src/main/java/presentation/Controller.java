package presentation;

import client.Client;
import entities.Article;
import entities.Writer;

import java.util.List;

public class Controller {

    public ChooseForm chooseForm;
    public ReaderForm readerForm;
    public WriterForm writerForm;
    public WriterForm2 writerForm2;
    Client client;
    public Controller()
    {
        this.chooseForm =new ChooseForm(this);
        this.readerForm=new ReaderForm(this);
        this.writerForm=new WriterForm(this);
        this.writerForm2=new WriterForm2(this);
        this.chooseForm.runFrame(this.writerForm,this.readerForm);
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void display(String message) {


    }

    public void displayArticle(Article a) {
        readerForm.displayArticle(a);

    }

    public void displayArticles(List<Article> a)
    {

        //System.out.println("controller display article");
        readerForm.displayAllArticles(a);
        writerForm2.displayAllArticles(a);
    }
    public void update()
    {
       //System.out.println("update in controller");
        client.handleMessageFromController("getAllArticles");
    }

    public String getArticle()
    {
        return readerForm.getArticleTitle();
    }

    public void getArticleRequest() {
        client.handleMessageFromController("getArticle");
    }

    public Writer getWriter()
    {
        return writerForm.getWriter();
    }

    public void loginOk(Writer writer) {
        writerForm.setVisible(false);
        writerForm2.setVisible(true);
        writerForm2.setWriter(writer);
    }

    public void displayAccountError(String message) {
        writerForm.displayError(message);
    }

    public Article createArticle()
    {
        return writerForm2.createArticle();
    }

    public String getSelectedArticle() {
        return writerForm2.getSelectedArticle();
    }

    public void disconnectClient() {
        client.handleMessageFromController("quit");
    }

    public void loginRequest() {
        client.handleMessageFromController("login");
    }

    public void createRequest() {
        client.handleMessageFromController("createArticle");
    }

    public void deleteRequest() {
        client.handleMessageFromController("deleteArticle");
    }

}
