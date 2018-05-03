package server;

import dataAccess.repositories.FormatClass;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;

public class ConnectionToClient extends Thread
{
    private Server server;
    private Socket clientSocket;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private boolean readyToStop;
    private HashMap savedInfo = new HashMap(10);


    ConnectionToClient(ThreadGroup group, Socket clientSocket,
                       Server server) throws IOException
    {
        super(group,(Runnable)null);
        this.clientSocket = clientSocket;
        this.server = server;

        try
        {
            input = new ObjectInputStream(clientSocket.getInputStream());
            output = new ObjectOutputStream(clientSocket.getOutputStream());
        }
        catch (IOException ex)
        {
            try
            {
                closeAll();
            }
            catch (Exception exc) { }

            throw ex;
        }

        readyToStop = false;
        start();
    }

    final public void sendToClient(Response response, String jsonString) throws IOException
    {
        if (clientSocket == null || output == null)
            throw new SocketException("socket does not exist");

        output.writeObject(new FormatClass().responseToJson(response));
        output.writeObject(jsonString);
    }

    final public void close() throws IOException
    {
        readyToStop = true;
        try
        {
            closeAll();
        }
        finally
        {
            server.clientDisconnected(this);
        }
    }

    final public void run()
    {
        server.clientConnected(this);
        try
        {
            Response response;
            String jsonString;

            while (!readyToStop)
            {

                response = new FormatClass().jsonToResponse(input.readObject().toString());
                jsonString = input.readObject().toString();

                System.out.println(response);
                System.out.println(jsonString);
                server.handleMessageFromClient(response, jsonString, this);
            }
        }
        catch (Exception exception)
        {
            if (!readyToStop)
            {
                try
                {
                    closeAll();
                }
                catch (Exception ex) { }

                server.clientException(this, exception);
            }
        }
    }

    private void closeAll() throws IOException
    {
        try
        {
            if (clientSocket != null)
                clientSocket.close();

            if (output != null)
                output.close();

            if (input != null)
                input.close();
        }
        finally
        {
            output = null;
            input = null;
            clientSocket = null;
        }
    }

    protected void finalize()
    {
        try
        {
            closeAll();
        }
        catch(IOException e) {}
    }
}