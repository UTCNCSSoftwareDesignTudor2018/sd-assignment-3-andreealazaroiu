package server;

import dataAccess.entities.Article;
import dataAccess.entities.Writer;
import dataAccess.repositories.ArticleRepository;
import dataAccess.repositories.FormatClass;
import dataAccess.repositories.WriterRepository;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import static server.Response.*;

public  class Server implements Runnable
{
    private ServerSocket serverSocket = null;
    private Thread connectionListener;
    private int port;
    private int timeout = 500;
    private int backlog = 10;
    private ThreadGroup clientThreadGroup;
    private boolean readyToStop = false;
    private ServerConsole serverUI;


    public Server(int port, ServerConsole serverUI)
    {
        this.port = port;
        this.serverUI=serverUI;
        this.clientThreadGroup =
                new ThreadGroup("ConnectionToClient threads")
                {
                    
                    public void uncaughtException(
                            Thread thread, Throwable exception)
                    {
                        clientException((ConnectionToClient)thread, exception);
                    }
                };
    }

    final public void listen() throws IOException
    {
        if (!isListening())
        {
            if (serverSocket == null)
            {
                serverSocket = new ServerSocket(getPort(), backlog);
            }

            serverSocket.setSoTimeout(timeout);
            readyToStop = false;
            connectionListener = new Thread(this);
            connectionListener.start();
        }
    }

    final public void stopListening()
    {
        readyToStop = true;
    }

    final synchronized public void close() throws IOException
    {
        if (serverSocket == null)
            return;
        stopListening();
        try
        {
            serverSocket.close();
        }
        finally
        {
            // Close the client sockets of the already connected clients
            Thread[] clientThreadList = getClientConnections();
            for (int i=0; i<clientThreadList.length; i++)
            {
                try
                {
                    ((ConnectionToClient)clientThreadList[i]).close();
                }
                // Ignore all exceptions when closing clients.
                catch(Exception ex) {}
            }
            serverSocket = null;
            serverClosed();
        }
    }

    public void sendToAllClients(Response response,String jsonString)
    {
        Thread[] clientThreadList = getClientConnections();

        for (int i=0; i<clientThreadList.length; i++)
        {
            try
            {
                ((ConnectionToClient)clientThreadList[i]).sendToClient(response,jsonString);
            }
            catch (Exception ex) {}
        }
    }

    final public boolean isListening()
    {
        return (connectionListener != null);
    }


    synchronized final public Thread[] getClientConnections()
    {
        Thread[] clientThreadList = new
                Thread[clientThreadGroup.activeCount()];

        clientThreadGroup.enumerate(clientThreadList);

        return clientThreadList;
    }

    final public int getPort()
    {
        return port;
    }

    final public void run()
    {
        serverStarted();

        try
        {
            while(!readyToStop)
            {
                try
                {
                    Socket clientSocket = serverSocket.accept();
                    synchronized(this)
                    {
                        ConnectionToClient c = new ConnectionToClient(
                                this.clientThreadGroup, clientSocket, this);
                    }
                }
                catch (InterruptedIOException exception)
                {
                }
            }
            serverStopped();
        }
        catch (IOException exception)
        {
            if (!readyToStop)
            {
                listeningException(exception);
            }
            else
            {
                serverStopped();
            }
        }
        finally
        {
            readyToStop = true;
            connectionListener = null;
        }
    }

    protected void clientConnected(ConnectionToClient client) {
        serverUI.display("Client "+client +"has connected");
    }

    synchronized protected void clientDisconnected(
            ConnectionToClient client) {}

    synchronized protected void clientException(
            ConnectionToClient client, Throwable exception) {}

    protected void listeningException(Throwable exception) {}

    protected void serverStarted() {
        serverUI.display("Server is listening for connections");
        sendToAllClients(SERVER_STARTED_LISTENING,"Server started listening");
    }

    protected void serverStopped() {
        serverUI.display("Server has stopped listening for connections");
        sendToAllClients(SERVER_STOPPED_LISTENING,"Server stopped listening");
    }

    protected void serverClosed() {

        serverUI.display("Server has closed");
        sendToAllClients(SERVER_CLOSED,"Server stopped listening");
    }

    protected void handleMessageFromClient(Response response,String jsonString, ConnectionToClient client)
    {
        ArticleRepository repo=new ArticleRepository();
        FormatClass formatClass=new FormatClass();
		switch(response)
		{
			case WRITER: WriterRepository rep=new WriterRepository();
            try {
                Writer writer= formatClass.jsonToWriter(jsonString);
                Writer actualWriter = rep.findWriterByUsername(writer.getUsername(),writer.getPassword());
                if(actualWriter!=null)
                {
                    client.sendToClient(WRITER,new FormatClass().writerToJson(actualWriter));
                } else {
                    client.sendToClient(INVALID_WRITER,"Invalid account");
                }
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    client.sendToClient(INVALID_WRITER,"Invalid writer");
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
			break;
			case CREATE_ARTICLE:

            Article article= null;
            try {
                article = formatClass.jsonToArticle(jsonString);
                repo.insertArticle(article);
				sendToAllClients(REFRESH,"");
            } catch (IOException e) {
                e.printStackTrace();
            }
			break;
			case DELETE_ARTICLE:
            repo.deleteArticle(jsonString);

            try {
                client.sendToClient(DELETE_ARTICLE,"The article was deleted");
				sendToAllClients(REFRESH,"");
            } catch (IOException e) {
                e.printStackTrace();
            }
			break;
			case GET_ALL_ARTICLES:
            List<Article> articles=repo.getArticles();


            try {
                client.sendToClient(GET_ALL_ARTICLES,formatClass.articlesToJson(articles));
            } catch (IOException e) {
                e.printStackTrace();
            }
			break;
			case GET_ARTICLE:
            Article article2=repo.findArticle(jsonString);

            try {
                client.sendToClient(GET_ARTICLE,formatClass.articleToJson(article2));
            } catch (IOException e) {
                e.printStackTrace();
                serverUI.display("Error sending article");
            }
			break;
			case CLIENT_DISCONNECT:
			    serverUI.display("Client"+client+"has disconnected");
                try {
                    client.close();
                } catch (IOException e) {
                    serverUI.display("Error disconnectiong client " + client);
                }
                break;
				
		}


    }


    public void handleMessageFromServerUI(String message)
    {
        try {

            if (message.equals("stop")) {
                if (isListening()) {
                    serverUI.display("Server stopped listening for connections.");
                    stopListening();
                } else {
                    serverUI.display("Server already stopped.");
                }
                return;
            }
            if (message.equals("close")) {
                this.sendToAllClients(SERVER_CLOSED,"Server Closed");
                close();
                for (Thread t : getClientConnections()) {
                    try {
                        ((ConnectionToClient)t).close();
                    } catch (Exception e) {}
                }
                return;
            }
            if (message.equals("start")) {
                if (isListening()) {
                    serverUI.display("Server is already started.");
                }
                listen();
                return;
            }
            if (message.equals("getport")) {
                serverUI.display(getPort()+"");
                return;
            }
            this.sendToAllClients(SERVER_INFO, message);
            return;

        }catch(Exception e)
        {
            serverUI.display("Cannot send message");
        }
    }



}
