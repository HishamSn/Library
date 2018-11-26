import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	public static void main(String[] args) throws IOException {
		ServerSocket serverSocket = new ServerSocket(5056);
		while (true) {
			Socket s = null;
			System.out.println("wait for connections");

			try {
				// socket object to receive incoming client requests
				s = serverSocket.accept();

				System.out.println("A new client is connected : " + s);

				// obtaining input and out streams
				DataInputStream dis = new DataInputStream(s.getInputStream());
				DataOutputStream dos = new DataOutputStream(s.getOutputStream());

				System.out.println("Assigning new thread for this client");

				// create a new thread object
//				Scanner scanner = new Scanner(System.in);
				Thread t = new ClientHandler(s, dis, dos);

				// Invoking the start() method
				t.start();

			} catch (Exception e) {
				s.close();
				e.printStackTrace();
			}
		}
	}
}
