package by.bsu.mmf.badthrowgame.server;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


public class Server {

    private Socket clientSocket;
    private ObjectOutputStream outStream;
    private ObjectInputStream inStream;

    public Server(Socket socket) throws IOException {
        clientSocket = socket;
        outStream = new ObjectOutputStream(clientSocket.getOutputStream());
        inStream = new ObjectInputStream(clientSocket.getInputStream());
    }

    public ObjectOutputStream getOutStream() {
        return outStream;
    }

    public ObjectInputStream getInStream() {
        return inStream;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }
}
