package by.bsu.mmf.badthrowgame.server;



import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class ServerRunner{
    public static void main(String[] args) {

        try {
            ServerSocket serverSocket = new ServerSocket(8080);
            System.out.println("Server has been initialized on port " + serverSocket.getLocalPort() + "\n");
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Client " + socket.getInetAddress() + " has been connected" + "\n");

                new ServerHandler(socket).start();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }
}
