import org.w3c.dom.Element;

public class Book {

    private String name;
    private String author;


    Book(Element element) {
        super();
        name = getTagValue("name", element);
        author = getTagValue("author", element);
    }

    @Override
    public String toString() {
        return "book [name=" + name + ", author=" + author + "]";
    }

    private static String getTagValue(String tag, Element element) {
        return element.getElementsByTagName(tag).item(0).getTextContent();
    }
}
