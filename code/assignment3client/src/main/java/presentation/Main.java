package presentation;

import client.Client;

import java.io.IOException;

public class Main {

    public static void main(String [] args)
    {
        Controller controller =new Controller();
        try {
            Client client = new Client(5555,"localhost", controller);
            client.openConnection();
        } catch (IOException e) {
            controller.display("error starting client");
        }

    }
}
