
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = new ServerSocket(5056);
        File fXmlFile = new File("Library.xml");

        while (true) {
            Socket socket = null;

            try {
                socket = serverSocket.accept();

                Thread t = new ClientHandler(socket, fXmlFile);
                t.start();

            } catch (Exception e) {
                if (socket != null) {
                    socket.close();
                }
                e.printStackTrace();
            }
        }
    }
}
