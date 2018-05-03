package server;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ServerConsole {


    final public static int DEFAULT_PORT = 5555;

   Server server;

    public ServerConsole(int port) {
        try {
            server = new Server(port, this);
        } catch(Throwable t) {
            System.out.println("Error! Can't setup server.");
            System.exit(1);
        }

    }


    public void accept()
    {
        try
        {
            BufferedReader fromConsole = new BufferedReader(new InputStreamReader(System.in));
            String message="";

            while (!message.equals("#quit"))
            {
                message = fromConsole.readLine();
                server.handleMessageFromServerUI(message);
            }
        }
        catch (Exception ex)
        {
            System.out.println
                    ("Unexpected error while reading from console!");
        }
    }

    public void display(String message)
    {
        System.out.println(message);
    }

    private void startServer() throws Exception {
        server.listen();
    }

    public static void main(String[] args)
    {
        int port = 0;

        try
        {
            port = Integer.parseInt(args[0]);
        }
        catch(Throwable t)
        {
            port = DEFAULT_PORT;
        }
        ServerConsole console= new ServerConsole(port);
        try
        {
            console.startServer(); //Start listening for connections
        }
        catch (Exception ex)
        {
            System.out.println("ERROR - Could not listen for clients!");
        }
        console.accept();
    }

}
