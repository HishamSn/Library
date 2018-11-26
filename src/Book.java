import java.io.Serializable;

public class Book implements Serializable {
	private static final long serialVersionUID = 1L;

	String name = null;
	String author = null;

	public Book() {
		super();
	}

	public Book(String name, String author) {
		super();

		this.name = name;
		this.author = author;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	@Override
	public String toString() {
		return "Book [name=" + name + ", author=" + author + "]";
	}

}
