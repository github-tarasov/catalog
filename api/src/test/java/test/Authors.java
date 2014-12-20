package test;

import client.AuthHeadersRequestFilter;
import client.AuthorsClient;
import model.Author;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.jboss.resteasy.api.validation.ViolationReport;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.junit.Test;
import util.Page;
import util.PageImpl;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class Authors {

    private static final String REST_SERVICE_URL = "http://localhost:8080/api";
    private static final String AUTHENTICATION_USER = "admin";
    private static final String AUTHENTICATION_PASSWORD = "admin";

    private static final String AUTHOR = "Richard Dallaway";

    private AuthorsClient buildClient() {
        ResteasyClient client = new ResteasyClientBuilder().build();
        client.register(new AuthHeadersRequestFilter(AUTHENTICATION_USER, AUTHENTICATION_PASSWORD));
        ResteasyWebTarget target = client.target(REST_SERVICE_URL);
        return target.proxy(AuthorsClient.class);
    }

    @Test
    public void crud() {
        // 1. Save a new author
        Author author = new Author();
        author.setName(AUTHOR);
        AuthorsClient client1 = this.buildClient();
        Response response1 = client1.create(author);
        response1.close();
        assertEquals("Bad response status", Response.Status.CREATED.getStatusCode(), response1.getStatus());
        Pattern p = Pattern.compile("^" + REST_SERVICE_URL + "/v1/authors/(\\d+)$");
        Matcher m = p.matcher(response1.getLocation().toString());
        assertTrue("Bad response resource location", m.matches());
        String authorId = m.group(1);
        assertNotNull("Author ID is null", authorId);

        // 2. Fetch author by id
        AuthorsClient client2 = this.buildClient();
        Response response2 = client2.find(Long.valueOf(authorId));
        Author author2 = response2.readEntity(Author.class);
        response2.close();
        assertNotNull("Author is null", author2);
        assertEquals("Bad name", AUTHOR, author2.getName());

        // 3. Fetch all authors
        AuthorsClient client3 = this.buildClient();
        Response response3 = client3.list(1, 10, "");
        assertEquals("Bad response status", Response.Status.OK.getStatusCode(), response3.getStatus());
        Page<Author> authors = response3.readEntity(new GenericType<PageImpl<Author>>() {
        });
        response3.close();
        assertTrue("Empty response", authors.getTotalNumberOfElements() >= 1);
        // TODO: search author

        // 4. Update author
        String newName = AUTHOR + "NEW";
        author2.setName(newName);
        AuthorsClient client4a = this.buildClient();
        Response response4a = client4a.update(author2.getId(), author2);
        response4a.close();
        assertEquals("Bad response status", Response.Status.OK.getStatusCode(), response4a.getStatus());

        AuthorsClient client4b = this.buildClient();
        Response response4b = client4b.find(author2.getId());
        Author author4 = response4b.readEntity(Author.class);
        response4b.close();
        assertNotNull(author4);
        assertEquals(author4.getName(), newName, author4.getName());

        // 5. Delete author
        AuthorsClient client5a = this.buildClient();
        Response response5a = client5a.delete(author4.getId());
        response5a.close();
        assertEquals("Bad response status", Response.Status.OK.getStatusCode(), response5a.getStatus());

        AuthorsClient client5b = this.buildClient();
        Response response5b = client5b.find(Long.valueOf(authorId));
        response5b.close();
        assertEquals("Bad response status", Response.Status.NOT_FOUND.getStatusCode(), response5b.getStatus());
    }

    @Test
    public void validationError() {
        Author author = new Author();
        StringBuilder badName = new StringBuilder("");
        for (int i = 1; i <= 201; i++) {
            badName.append("N");
        }
        author.setName(badName.toString());

        AuthorsClient client = this.buildClient();
        Response response = client.create(author);
        assertEquals("Bad response status", Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals(
                "Bad response message",
                "The author's name must be between 2 and 200 chars long",
                response.readEntity(ViolationReport.class).getParameterViolations().get(0).getMessage()
        );
        response.close();
    }

    @Test
    public void notFoundError() {
        AuthorsClient client = this.buildClient();
        Response response = client.find(Long.MAX_VALUE);
        assertEquals("Bad response status", Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        response.close();
    }

}