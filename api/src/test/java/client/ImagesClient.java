package client;

import model.Image;
import util.ImageSize;
import util.MoveType;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("v1/books/{bookId}/images")
@Produces(MediaType.APPLICATION_JSON)
public interface ImagesClient {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(
            @PathParam("bookId") long bookId,
            byte[] source
    );

    @GET
    @Path("{imageId}")
    @Produces("image/jpeg")
    public Response find(
            @PathParam("bookId") long bookId,
            @PathParam("imageId") long imageId,
            @QueryParam("size") ImageSize size
    );

    @PUT
    @Path("{imageId}")
    public Response changeSort(
            @PathParam("bookId") long bookId,
            @PathParam("imageId") long imageId,
            @QueryParam("moveType") MoveType moveType

    );

    @DELETE
    @Path("{imageId}")
    public Response delete(
            @PathParam("bookId") long bookId,
            @PathParam("imageId") long imageId
    );
}
