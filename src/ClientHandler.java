import java.beans.XMLEncoder;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ClientHandler extends Thread {

	final DataInputStream dis;
	final DataOutputStream dos;
	final Socket s;
	// put the path of the xml file you want to process
	private static final String SERIALIZED_FILE_NAME = "C:\\Users\\Hisham Snaimeh\\Desktop\\Library\\Library.xml";

	// Constructor
	public ClientHandler(Socket s, DataInputStream dis, DataOutputStream dos) {
		this.s = s;
		this.dis = dis;
		this.dos = dos;
	}

	@Override
	public void run() {

		final File file = new File(SERIALIZED_FILE_NAME);
		try {
			if (file.createNewFile()) {
				System.out.println(SERIALIZED_FILE_NAME + " File Created");
			} else
				System.out.println("File " + SERIALIZED_FILE_NAME + " already exists");
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		String received;
		String keyWard=null;
		String toreturn;
		while (true) {
			try {
				// Ask user what he wants
				dos.writeUTF("What do you want?[1 to FetchBook |2 to FetchAllBooks |3 to inputNewBook]..\n"
						+ "Type Exit to terminate connection.");

				// receive the answer from client
				received = dis.readUTF();
				if(received.equals("1")){
					keyWard = dis.readUTF();
				}

				if (received.equals("Exit")) {
					System.out.println("Client " + this.s + " sends exit...");
					System.out.println("Closing this connection.");
					this.s.close();
					System.out.println("Connection closed");
					break;
				}

				// write on output stream based on the
				// answer from the client
				switch (received) {

				case "1":

					File xmlFile = new File(SERIALIZED_FILE_NAME);
					DocumentBuilderFactory dbFactoryXml = DocumentBuilderFactory.newInstance();
					DocumentBuilder dBuilderInstance = dbFactoryXml.newDocumentBuilder();
					Document docInstanec = dBuilderInstance.parse(xmlFile);
					docInstanec.getDocumentElement().normalize();
					NodeList nodeList = docInstanec.getElementsByTagName("Book");
					if (fetchBook(keyWard, nodeList)) ;

					break;

				case "2":

					File inputFile = new File(SERIALIZED_FILE_NAME);
					DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
					DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
					Document doc = dBuilder.parse(inputFile);
					doc.getDocumentElement().normalize();
					NodeList nList = doc.getElementsByTagName("Book");
					List<Book> books = new ArrayList<Book>();

					for (int temp = 0; temp < nList.getLength(); temp++) {
						Node nNode = nList.item(temp);
						if (nNode.getNodeType() == Node.ELEMENT_NODE) {
							Book book = new Book();
							Element eElement = (Element) nNode;
							book.setName(eElement.getElementsByTagName("Name").item(0).getTextContent());
							book.setAuthor(eElement.getElementsByTagName("Author").item(0).getTextContent());
							books.add(book);
							dos.writeUTF(book.toString());
						}
					}

					dos.writeUTF("All Books Fetched");
					break;

				case "3":
//					Scanner s = new Scanner(System.in);
//					System.out.println("please insert book name :");
//					String bookName = s.next();
//					System.out.println("please insert author name :");
//					String authorName = s.next();

					dos.writeUTF("please insert book name :");
					String bookName = dis.readUTF();
					dos.writeUTF("please insert author name :");
					String authorName = dis.readUTF();

					try {
						DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
						DocumentBuilder documentBuilder;

						documentBuilder = documentBuilderFactory.newDocumentBuilder();

						Document document = documentBuilder.parse(SERIALIZED_FILE_NAME);
						Element root = document.getDocumentElement();
						Collection<Book> booksFetched = new ArrayList<Book>();
						Book book = new Book(bookName, authorName);

						Element newServer = document.createElement("Book");

						Element name = document.createElement("Name");
						name.appendChild(document.createTextNode(book.getName()));
						newServer.appendChild(name);

						Element port = document.createElement("Author");
						port.appendChild(document.createTextNode(book.getAuthor()));
						newServer.appendChild(port);

						root.appendChild(newServer);

						DOMSource source = new DOMSource(document);
						TransformerFactory transformerFactory = TransformerFactory.newInstance();
						Transformer transformer = transformerFactory.newTransformer();
						StreamResult result = new StreamResult(SERIALIZED_FILE_NAME);
						transformer.transform(source, result);
					} catch (ParserConfigurationException e1) {
						e1.printStackTrace();
					} catch (SAXException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (TransformerConfigurationException e) {
						e.printStackTrace();
					} catch (TransformerException e) {
						e.printStackTrace();
					}
					toreturn = "Book Added Successfully";
					dos.writeUTF(toreturn);
					s.close();
					break;
				default:
					dos.writeUTF("Invalid input");
					break;
				}
			} catch (IOException | ParserConfigurationException | SAXException e) {
				e.printStackTrace();
			}
		}

		try {
			this.dis.close();
			this.dos.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean fetchBook(String keyWard, NodeList nodeList) throws IOException {
		for (int temp = 0; temp < nodeList.getLength(); temp++) {
			Node nNode = nodeList.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				if (eElement.getElementsByTagName("Name").item(0).getTextContent().equals(keyWard)) {
					Book book = new Book();
					book.setName(eElement.getElementsByTagName("Name").item(0).getTextContent());
					book.setAuthor(eElement.getElementsByTagName("Author").item(0).getTextContent());

					dos.writeUTF("here is your book" + book.toString());
					return true ;
				}else{
					dos.writeUTF("No book found");

					return true;
				}
			}
		}
		return false;
	}

}
