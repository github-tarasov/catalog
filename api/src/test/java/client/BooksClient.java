package client;

import model.Book;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("v1/books")
@Produces(MediaType.APPLICATION_JSON)
public interface BooksClient {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(
            Book properties
    );

    @GET
    @Path("{id}")
    public Response find(@PathParam("id") long id);

    @GET
    public Response list(
            @QueryParam("pageNumber") int pageNumber,
            @QueryParam("pageSize") int pageSize,
            @QueryParam("query") String query
    );

    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response update(
            @PathParam("id") long id,
            Book properties
    );

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") long id);


    @PUT
    @Path("{bookId}/authors/{authorId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addAuthorToBook(
            @PathParam("bookId") long bookId,
            @PathParam("authorId") long authorId
    );

    @DELETE
    @Path("{bookId}/authors/{authorId}")
    public Response deleteAuthorFromBook(
            @PathParam("bookId") long bookId,
            @PathParam("authorId") long authorId
    );

}
