package client;

import entities.Article;
import entities.FormatClass;
import presentation.Controller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;

import static client.Response.*;

public class Client implements Runnable {

    private Socket clientSocket;
    private Thread clientReader;
    private boolean readyToStop= false;
    private String host;
    private int port;
    Controller controller;
    private ObjectOutputStream outputFromClient;
    private ObjectInputStream inputFromServer;

    public Client(int port, String host, Controller controller) throws IOException {
        this.host = host;
        this.port = port;
        this.controller = controller;
        controller.setClient(this);
        openConnection();
    }

    final public void openConnection() throws IOException
    {
        if(isConnected())
            return;
        try
        {
            clientSocket= new Socket(host, port);
            outputFromClient = new ObjectOutputStream(clientSocket.getOutputStream());
            inputFromServer = new ObjectInputStream(clientSocket.getInputStream());
            System.out.println("open connection ok");
        }
        catch (IOException ex)
        {
            try
            { closeAll(); }
            catch (Exception exc) { }

            throw ex; // Rethrow the exception.
        }

        clientReader = new Thread(this);
        readyToStop = false;
        clientReader.start();
        controller.update();
    }

    final public void sendToServer(Response response,String jsonString) throws IOException
    {
        if (clientSocket == null || outputFromClient == null)
            throw new SocketException("socket does not exist");

        System.out.println("socket ok");
        System.out.println(response);
        System.out.println(jsonString);
        outputFromClient.writeObject(new FormatClass().responseToJson(response));
        outputFromClient.writeObject(jsonString);
    }

    final public void closeConnection() throws IOException
    {
        readyToStop= true;
        try
        { closeAll(); }
        finally
        { connectionClosed(); }
    }

    final public boolean isConnected()
    { return clientReader!=null && clientReader.isAlive(); }

    final public int getPort()
    { return port; }

    final public void setPort(int port)
    { this.port = port; }

    final public String getHost()
    { return host; }

    final public void setHost(String host)
    { this.host = host; }

    final public InetAddress getInetAddress()
    { return clientSocket.getInetAddress(); }

    protected void connectionClosed() {  controller.display("Disconnected from server.");}

    protected void connectionException(Exception exception) {}

    protected void connectionEstablished() {}

    protected  void handleMessageFromServer(Response response,String jsonString){

        FormatClass formatClass=new FormatClass();
        switch(response)
        {
            case SERVER_CLOSED:
                controller.display("Terminating client.");
                quit();
                break;
            case SERVER_STOPPED_LISTENING:
                controller.display("Server has stopped listening for connections.");

                break;
            case SERVER_STARTED_LISTENING:
                controller.display("Server has started listening for connections.");
                break;
            case WRITER:
                try {
                    controller.loginOk(formatClass.jsonToWriter(jsonString));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case INVALID_WRITER:
                controller.displayAccountError("Invalid user");
                break;
            case GET_ARTICLE:
                Article article= null;
                try {
                    article = formatClass.jsonToArticle(jsonString);
                    controller.displayArticle(article);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case GET_ALL_ARTICLES:
                List<Article> articles= null;
                try {
                    articles = formatClass.jsonToArticles(jsonString);
                } catch (IOException e) {
                    e.printStackTrace();
                }
               // System.out.println(articles);
                controller.displayArticles(articles);
                break;
            case REFRESH:
                controller.update();
                break;
            case CREATE_ARTICLE:controller.createArticle();
                break;
        }


    }

    public void handleMessageFromController(String message)
    {
        FormatClass formatClass=new FormatClass();
    try
        {
            if (message.equals("quit")) {
                if (isConnected()) {
                    sendToServer(CLIENT_DISCONNECT,"");
                    quit();
                }
                System.exit(0);
                return;
            }
            if (message.equals("logoff")) {
                try {
                    sendToServer(CLIENT_DISCONNECT,"Log off");
                    closeConnection();
                } catch (Throwable t) {
                    controller.display("Could not close connection: " + t);
                }
                return;
            }
            if(message.equals("getArticle"))
            { sendToServer(GET_ARTICLE,controller.getArticle()); }

            if(message.equals("getAllArticles"))
            {
                //System.out.println("getallarticles in client");
                sendToServer(GET_ALL_ARTICLES,"Get Articles");}

            if(message.equals("deleteArticle"))
            { sendToServer(DELETE_ARTICLE,controller.getSelectedArticle()); }

            if(message.equals("createArticle"))
            {  sendToServer(CREATE_ARTICLE,formatClass.articleToJson(controller.createArticle())); }

            if(message.equals("login"))
            { sendToServer(WRITER,formatClass.writerToJson(controller.getWriter())); }

            if (message.equals("gethost")) {
                controller.display(getHost()+"");
                return;
            }
            if (message.equals("getport")) {
                controller.display(getPort()+"");
                return;
            }
        }
        catch(Exception e)
        { controller.display("Could not send message to server.");
        System.out.println(e);}
    }

    private void closeAll() throws IOException
    {
        try
        {
            if (clientSocket != null)
                clientSocket.close();

            if (outputFromClient != null)
                outputFromClient.close();

            if (inputFromServer != null)
                inputFromServer.close();
        }
        finally
        {
            outputFromClient = null;
            inputFromServer = null;
            clientSocket = null;
        }
    }

    public void quit()
    {
        try
        { closeConnection(); }
        catch(IOException e) {}
        System.exit(0);
    }
    public void run() {

        connectionEstablished();
        Response response;
        String jsonString;
        try
        {
            while(!readyToStop)
            {
                response = new FormatClass().jsonToResponse(inputFromServer.readObject().toString());
                jsonString=inputFromServer.readObject().toString();
                handleMessageFromServer(response,jsonString);
            }
        }
        catch (Exception exception)
        {
            if(!readyToStop)
            {
                try
                { closeAll(); }
                catch (Exception ex) { }
                connectionException(exception);
            }
        }
        finally
        { clientReader = null; }
    }
}
