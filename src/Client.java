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

            // getting localhost ip
            InetAddress ip = InetAddress.getByName("localhost");

            // establish the connection with server port 5056
            Socket s = new Socket(ip, 5056);

            // obtaining input and out streams
            DataInputStream dis = new DataInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());

            // the following loop performs the exchange of information between client and
            // client handler
            while (true) {
                System.out.println(dis.readUTF());
                String tosend = scn.nextLine();
                dos.writeUTF(tosend);
                if (tosend.equals("1")) {
                    System.out.println("please insert book name :");
                    String searchKeywork=scn.nextLine();
                    dos.writeUTF(searchKeywork);

                }

                if(tosend.equals("3")){
                    System.out.println(dis.readUTF());
                    dos.writeUTF(scn.nextLine());

                    System.out.println(dis.readUTF());
                    dos.writeUTF(scn.nextLine());
                }

                if (tosend.equals("Exit")) {
                    System.out.println("Closing this connection : " + s);
                    s.close();
                    System.out.println("Connection closed");
                    break;
                }

                String received = dis.readUTF();
                System.out.println(received);

            }

            // closing resources
            scn.close();
            dis.close();
            dos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
