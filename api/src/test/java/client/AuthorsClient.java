package client;

import model.Author;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("v1/authors")
@Produces(MediaType.APPLICATION_JSON)
public interface AuthorsClient {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(
            Author properties
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
            Author properties
    );

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") long id);
}
