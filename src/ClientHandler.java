import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler extends Thread {

    private DataInputStream receivedStream;
    private DataOutputStream sendStream;
    private final Socket socket;
    private final File file;

    private DocumentBuilderFactory documentBuilderFactory;
    private DocumentBuilder documentBuilder;
    private Document document;
    private boolean loop = true;


    ClientHandler(Socket socket, File file) {
        this.socket = socket;
        this.file = file;
        try {
            receivedStream = new DataInputStream(socket.getInputStream());
            sendStream = new DataOutputStream(socket.getOutputStream());
            documentBuilderFactory = DocumentBuilderFactory.newInstance();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        while (loop) {
            try {
                String received = receivedStream.readUTF();
                switch (received.toLowerCase()) {

                    case "1": {
                        fetchBooks();
                        break;
                    }
                    case "2": {
                        fetchAllBooks();
                        break;
                    }
                    case "exit": {
                        loop = false;
                        exit();
                        break;
                    }
                    default: {
                        sendStream.writeUTF("Invalid input");
                    }

                }
            } catch (IOException | ParserConfigurationException | SAXException e) {
                e.printStackTrace();
            }
        }

    }


    private void fetchBooks() throws ParserConfigurationException, IOException, SAXException {

        String keyWard = receivedStream.readUTF();
//        System.out.println("The client requested the " + keyWard + " book");

        documentBuilder = documentBuilderFactory.newDocumentBuilder();
        document = documentBuilder.parse(file);
        document.getDocumentElement().normalize();
//        System.out.println("Root element :" + document.getDocumentElement().getNodeName());

        NodeList nodeList = document.getElementsByTagName("Book");

        String send = "No book found";
        for (int index = 0; index < nodeList.getLength(); index++) {
            Node node = nodeList.item(index);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                if (element.getElementsByTagName("name").item(0).getTextContent().equals(keyWard)) {
                    Book book = new Book(element);
                    send = "Here is your " + book.toString();
                }
            }
        }

        sendStream.writeUTF(send);
    }

    private void fetchAllBooks() throws ParserConfigurationException, IOException, SAXException {

//        System.out.println("\nThe client requested the all books ");

        documentBuilder = documentBuilderFactory.newDocumentBuilder();
        document = documentBuilder.parse(file);
        document.getDocumentElement().normalize();
//        System.out.println("Root element :" + document.getDocumentElement().getNodeName());

        StringBuilder send = new StringBuilder();
        NodeList nodeList = document.getDocumentElement().getElementsByTagName("Book");
        for (int temp = 0; temp < nodeList.getLength(); temp++) {
            Node node = nodeList.item(temp);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Book book = new Book((Element) node);
                send.append(book.toString()).append("\n");
            }
        }

        sendStream.writeUTF(send.toString());
    }

    private void exit() throws IOException {
        this.receivedStream.close();
        this.sendStream.close();
        this.socket.close();
    }
}
