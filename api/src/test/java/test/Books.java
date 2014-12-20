package test;

import client.AuthHeadersRequestFilter;
import client.AuthorsClient;
import client.BooksClient;
import model.Author;
import model.Book;
import model.Image;
import org.jboss.resteasy.api.validation.ViolationReport;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.junit.Test;
import util.Page;
import util.PageImpl;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

public class Books {

    private static final String REST_SERVICE_URL = "http://localhost:8080/api";
    private static final String AUTHENTICATION_USER = "admin";
    private static final String AUTHENTICATION_PASSWORD = "admin";

    private static final String TITLE = "Lift Cookbook";
    private static final String DESCRIPTION = "If you need help building web applications with the Lift framework, this cookbook provides scores of concise, ready-to-use code solutions. You’ll find recipes for everything from setting up a coding environment to creating REST web services and deploying your application to production.\n" +
            "\n" +
            "Built on top of the Scala JVM programming language, Lift takes a different—yet ultimately easier—approach to development than MVC frameworks such as Rails. Each recipe in this book includes a discussion of how and why each solution works, not only to help you complete the task at hand, but also to illustrate how Lift works.";
    private static final Calendar RELEASE_DATE = new GregorianCalendar(2013, 11, 24);
    private static final BigDecimal PRICE = new BigDecimal("23.23");

    private static final String IMAGE_FILE = "lift.jpg";

    private static final String AUTHOR = "Richard Dallaway";


    private Book bookMock() {
        Book book = new Book();
        book.setTitle(TITLE);
        book.setDescription(DESCRIPTION);
        book.setReleaseDate(RELEASE_DATE);
        book.setPrice(PRICE);

        Author author = new Author();
        author.setName(AUTHOR);
        Set<Author> authors = new HashSet<Author>();
        authors.add(author);
        book.setAuthors(authors);

        return book;
    }


    private BooksClient buildClient() {
        ResteasyClient client = new ResteasyClientBuilder().build();
        client.register(new AuthHeadersRequestFilter(AUTHENTICATION_USER, AUTHENTICATION_PASSWORD));
        ResteasyWebTarget target = client.target(REST_SERVICE_URL);
        return target.proxy(BooksClient.class);
    }

    private AuthorsClient buildAuthorsClient() {
        ResteasyClient client = new ResteasyClientBuilder().build();
        client.register(new AuthHeadersRequestFilter(AUTHENTICATION_USER, AUTHENTICATION_PASSWORD));
        ResteasyWebTarget target = client.target(REST_SERVICE_URL);
        return target.proxy(AuthorsClient.class);
    }

    @Test
    public void crud() {
        // 1. Save a new book
        Book book = bookMock();
        BooksClient client1 = this.buildClient();
        Response response1 = client1.create(book);
        System.out.println(response1.readEntity(String.class));
        response1.close();
        assertEquals("Bad response status", Response.Status.CREATED.getStatusCode(), response1.getStatus());
        Pattern p = Pattern.compile("^" + REST_SERVICE_URL + "/v1/books/(\\d+)$");
        Matcher m = p.matcher(response1.getLocation().toString());
        assertTrue("Bad response resource location", m.matches());
        String bookId = m.group(1);
        assertNotNull("Book ID is null", bookId);

        // 2. Fetch book by id
        BooksClient client2 = this.buildClient();
        Response response2 = client2.find(Long.valueOf(bookId));
        Book book2 = response2.readEntity(Book.class);
        response2.close();
        assertNotNull("Book is null", book2);
        assertEquals("Bad title", TITLE, book2.getTitle());
        assertEquals("Bad description", DESCRIPTION, book2.getDescription());
        assertEquals("Bad releaseDate", RELEASE_DATE.getTime(), book2.getReleaseDate().getTime());
        assertEquals("Bad price", PRICE, book2.getPrice());
        assertEquals("Bad authors count", 1, book2.getAuthors().size());
        Author author2 = (Author) book2.getAuthors().toArray()[0];
        assertNotNull("Author is null", author2);
        assertTrue("Bad id", author2.getId() > 0);
        assertEquals("Bad name", AUTHOR, author2.getName());

        // 3. Fetch all books
        BooksClient client3 = this.buildClient();
        Response response3 = client3.list(1, 10, "");
        assertEquals("Bad response status", Response.Status.OK.getStatusCode(), response3.getStatus());
        Page<Book> books = response3.readEntity(new GenericType<PageImpl<Book>>() {
        });
        response3.close();
        assertTrue("Empty response", books.getTotalNumberOfElements() >= 1);
        // TODO: search book

        // 4. Update book
        String newTitle = TITLE + "NEW";
        String newDescription = DESCRIPTION + "NEW";
        Calendar newReleaseDate = (GregorianCalendar) RELEASE_DATE.clone();
        newReleaseDate.add(Calendar.MONTH, 1);
        BigDecimal newPrice = PRICE.add(new BigDecimal("1.11"));

        book2.setTitle(newTitle);
        book2.setDescription(newDescription);
        book2.setReleaseDate(newReleaseDate);
        book2.setPrice(newPrice);
        book2.setImages(null);

        BooksClient client4a = this.buildClient();
        Response response4a = client4a.update(book2.getId(), book2);
        response4a.close();
        assertEquals("Bad response status", Response.Status.OK.getStatusCode(), response4a.getStatus());

        BooksClient client4b = this.buildClient();
        Response response4b = client4b.find(book2.getId());
        Book book4 = response4b.readEntity(Book.class);
        response4b.close();
        assertNotNull(book4);
        assertEquals("bad book's property", newTitle, book4.getTitle());
        assertEquals("bad book's property", newDescription, book4.getDescription());
        assertEquals("bad book's property", newReleaseDate.getTime(), book4.getReleaseDate().getTime());
        assertEquals("bad book's property", newPrice, book4.getPrice());

        // 5. Delete a book
        BooksClient client5a = this.buildClient();
        Response response5a = client5a.delete(book4.getId());
        response5a.close();
        assertEquals("Bad response status", Response.Status.OK.getStatusCode(), response5a.getStatus());

        BooksClient client5b = this.buildClient();
        Response response5b = client5b.find(Long.valueOf(bookId));
        response5b.close();
        assertEquals("Bad response status", Response.Status.NOT_FOUND.getStatusCode(), response5b.getStatus());
    }

    @Test
    public void validationError() {
        Book book = bookMock();
        StringBuilder badTitle = new StringBuilder("");
        for (int i = 1; i <= 401; i++) {
            badTitle.append("T");
        }
        book.setTitle(badTitle.toString());

        BooksClient client = this.buildClient();
        Response response = client.create(book);
        assertEquals("Bad response status", Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals(
                "Bad response message",
                "The book's title must be between 2 and 400 chars long",
                response.readEntity(ViolationReport.class).getParameterViolations().get(0).getMessage()
        );
        response.close();
    }

    @Test
    public void notFoundError() {
        BooksClient client = this.buildClient();
        Response response = client.find(Long.MAX_VALUE);
        response.close();
        assertEquals("Bad response status", Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void booksAndAuthors() {
        // Create test book
        Book book = bookMock();
        BooksClient client1 = this.buildClient();
        Response response1 = client1.create(book);
        response1.close();
        assertEquals("Bad response status", Response.Status.CREATED.getStatusCode(), response1.getStatus());
        Pattern pBook = Pattern.compile("^" + REST_SERVICE_URL + "/v1/books/(\\d+)$");
        Matcher mBook = pBook.matcher(response1.getLocation().toString());
        assertTrue("Bad response resource location", mBook.matches());
        long bookId = Long.valueOf(mBook.group(1));
        assertNotNull("Book ID is null", bookId);

        // Create test author
        Author author = new Author();
        author.setName(AUTHOR);
        AuthorsClient client2 = this.buildAuthorsClient();
        Response response2 = client2.create(author);
        response2.close();
        assertEquals("Bad response status", Response.Status.CREATED.getStatusCode(), response2.getStatus());
        Pattern pAuthor = Pattern.compile("^" + REST_SERVICE_URL + "/v1/authors/(\\d+)$");
        Matcher mAuthor = pAuthor.matcher(response2.getLocation().toString());
        assertTrue("Bad response resource location", mAuthor.matches());
        long authorId = Long.valueOf(mAuthor.group(1));
        assertNotNull("Author ID is null", authorId);

        // Add author to book
        BooksClient client3a = this.buildClient();
        Response response3a = client3a.addAuthorToBook(bookId, authorId);
        response3a.close();
        assertEquals("Bad response status", Response.Status.OK.getStatusCode(), response3a.getStatus());

        BooksClient client3b = this.buildClient();
        Response response3b = client3b.find(bookId);
        Book book3 = response3b.readEntity(Book.class);
        response3b.close();
        Object[] authors3 = book3.getAuthors().toArray();
        assertEquals("Bad author's set", 2, authors3.length);
        assertTrue(
                "Author not found",
                ((Author) authors3[0]).getId() == authorId
                        || ((Author) authors3[1]).getId() == authorId
        );

        // Delete author from book
        BooksClient client4a = this.buildClient();
        Response response4a = client4a.deleteAuthorFromBook(bookId, authorId);
        response4a.close();
        assertEquals("Bad response status", Response.Status.OK.getStatusCode(), response4a.getStatus());

        BooksClient client4b = this.buildClient();
        Response response4b = client4b.find(bookId);
        Book book4 = response4b.readEntity(Book.class);
        response4b.close();
        assertNotNull("Book is null", book4);
        Object[] authors4 = book4.getAuthors().toArray();
        assertEquals("Bad author's set", 1, authors4.length);
        assertTrue(
                "Author not deleted",
                ((Author) authors4[0]).getId() != authorId
        );

        // Delete author
        AuthorsClient client5 = this.buildAuthorsClient();
        Response response5 = client5.delete(authorId);
        response5.close();
        assertEquals("Bad response status", Response.Status.OK.getStatusCode(), response5.getStatus());

        // Delete book
        BooksClient client6 = this.buildClient();
        Response response6 = client6.delete(book4.getId());
        response6.close();
        assertEquals("Bad response status", Response.Status.OK.getStatusCode(), response6.getStatus());
    }

}
