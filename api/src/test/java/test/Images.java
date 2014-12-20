package test;

import client.AuthHeadersRequestFilter;
import client.BooksClient;
import client.ImagesClient;
import model.Author;
import model.Book;
import model.Image;
import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.junit.Test;
import util.ImageSize;
import util.MoveType;

import javax.ws.rs.core.Response;
import java.io.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

public class Images {

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

    private ImagesClient buildClient() {
        ResteasyClient client = new ResteasyClientBuilder().build();
        client.register(new AuthHeadersRequestFilter(AUTHENTICATION_USER, AUTHENTICATION_PASSWORD));
        ResteasyWebTarget target = client.target(REST_SERVICE_URL);
        return target.proxy(ImagesClient.class);
    }

    private BooksClient buildBooksClient() {
        ResteasyClient client = new ResteasyClientBuilder().build();
        client.register(new AuthHeadersRequestFilter(AUTHENTICATION_USER, AUTHENTICATION_PASSWORD));
        ResteasyWebTarget target = client.target(REST_SERVICE_URL);
        return target.proxy(BooksClient.class);
    }


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

    private byte[] imageBytesMock() {
        Image image = new Image();
        File file = new File(getClass().getClassLoader().getResource(IMAGE_FILE).getFile());
        byte[] bFile = new byte[(int) file.length()];
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            fileInputStream.read(bFile);
        } catch (IOException e) {
            fail("Can't read image from resource");
        }
        return bFile;
    }

    @Test
    public void crud() {
        // Create test book
        Book book = bookMock();
        BooksClient client0 = this.buildBooksClient();
        Response response0 = client0.create(book);
        //System.out.println(response0.readEntity(String.class));
        response0.close();
        assertEquals("Bad response status", Response.Status.CREATED.getStatusCode(), response0.getStatus());
        Pattern p0 = Pattern.compile("^" + REST_SERVICE_URL + "/v1/books/(\\d+)$");
        Matcher m0 = p0.matcher(response0.getLocation().toString());
        assertTrue("Bad response resource location", m0.matches());
        long bookId = Long.valueOf(m0.group(1));
        assertNotNull("Book ID is null", bookId);
        book.setId(bookId);

        // 1. Save a new image
        byte[] imageBytes = imageBytesMock();
        ImagesClient client1 = this.buildClient();
        Response response1 = client1.create(bookId, imageBytes);
        response1.close();
        assertEquals("Bad response status", Response.Status.CREATED.getStatusCode(), response1.getStatus());
        Pattern p = Pattern.compile("^" + REST_SERVICE_URL + "/v1/books/(\\d+)/images/(\\d+)$");
        Matcher m = p.matcher(response1.getLocation().toString());
        assertTrue("Bad response resource location", m.matches());
        assertEquals("Bad response resource location", bookId, (long)Long.valueOf(m.group(1)));
        long imageId = Long.valueOf(m.group(2));
        assertNotNull("Image ID is null", imageId);

        // 2. Fetch image
        ImagesClient client2 = this.buildClient();
        Response response2 = client2.find(bookId, imageId, ImageSize.BIG);
        assertEquals("Bad response status", Response.Status.OK.getStatusCode(), response2.getStatus());
        InputStream is = response2.readEntity(InputStream.class);
        byte[] imageBytes2 = new byte[0];
        try {
            imageBytes2 = IOUtils.toByteArray(is);
        } catch (IOException e) {
            fail("Can't read image from api");
        }
        response2.close();
        // TODO: test response image
        //assertArrayEquals("Bad image content", imageBytes2, imageBytes);
        // TODO: test small image

        // 3. Change image order
        // 3a. Add new image
        byte[] imageBytes3 = imageBytesMock();
        ImagesClient client3a = this.buildClient();
        Response response3a = client3a.create(bookId, imageBytes3);
        response3a.close();
        assertEquals("Bad response status", Response.Status.CREATED.getStatusCode(), response3a.getStatus());

        ImagesClient client3b = this.buildClient();
        Response response3b = client3b.changeSort(bookId, imageId, MoveType.DOWN);
        assertEquals("Bad response status", Response.Status.OK.getStatusCode(), response3b.getStatus());
        // TODO: assert UP/DOWN and changed order

        // 4. Delete image
        ImagesClient client4a = this.buildClient();
        Response response4a = client4a.delete(bookId, imageId);
        response4a.close();
        assertEquals("Bad response status", Response.Status.OK.getStatusCode(), response4a.getStatus());

        ImagesClient client4b = this.buildClient();
        Response response4b = client4b.find(bookId, imageId, ImageSize.BIG);
        response4b.close();
        assertEquals("Bad response status", Response.Status.NOT_FOUND.getStatusCode(), response4b.getStatus());

        // Delete test book
        BooksClient clientEnd = this.buildBooksClient();
        Response responseEnd = clientEnd.delete(bookId);
        responseEnd.close();
        assertEquals("Bad response status", Response.Status.OK.getStatusCode(), responseEnd.getStatus());
    }

    @Test
    public void notFoundError() {
        ImagesClient client = this.buildClient();
        Response response = client.find(Long.MAX_VALUE, Long.MAX_VALUE, ImageSize.BIG);
        response.close();
        assertEquals("Bad response status", Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }


}
