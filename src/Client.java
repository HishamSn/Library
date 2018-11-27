import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) throws IOException {
        try {
            Scanner scn = new Scanner(System.in);

            InetAddress ip = InetAddress.getByName("localhost");
            Socket socket = new Socket(ip, 5056);

            DataInputStream receivedStream = new DataInputStream(socket.getInputStream());
            DataOutputStream sendStream = new DataOutputStream(socket.getOutputStream());

            boolean loop = true;

            while (loop) {
                System.out.println("\n\n*****************************************************************************" +
                        "\nWhat do you want? " +
                        "\n\t1 to Fetch Book " +
                        "\n\t2 to Fetch All Books" +
                        "\n\t(exit) to close client side " +
                        "\n\nEnter your choose.");

                String type = scn.nextLine();
                sendStream.writeUTF(type);

                switch (type.toLowerCase()) {

                    case "1": {
                        System.out.println("\nPlease insert book name : ");
                        sendStream.writeUTF(scn.nextLine());
                        break;
                    }
                    case "exit": {
                        loop = false;
                        break;
                    }
                    default: {

                        break;
                    }
                }

                if (loop) {
                    System.out.println(receivedStream.readUTF());
                }
            }

            scn.close();
            receivedStream.close();
            sendStream.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
