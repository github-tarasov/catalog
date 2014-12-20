package service;

import dao.AuthorDAO;
import model.Author;
import util.Page;
import util.PageRequest;
import util.PageRequestImpl;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;

@Path("v1/authors")
@Produces(MediaType.APPLICATION_JSON)
public class Authors {

    @EJB
    private AuthorDAO dao;

    @Context
    private UriInfo uriInfo;

    @RolesAllowed("ADMIN")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(
            @Valid Author properties
    ) {
        Author author = dao.create(properties);
        final URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(author.getId())).build();
        return Response.created(uri).build();
    }

    @PermitAll
    @GET
    @Path("{id}")
    public Response find(@PathParam("id") long id) {
        Author author = dao.find(id);
        return Response.ok(author).build();
    }

    @PermitAll
    @GET
    public Response list(
            @QueryParam("pageNumber") @DefaultValue("1") int pageNumber,
            @QueryParam("pageSize") @DefaultValue("10") int pageSize,
            @QueryParam("query") @DefaultValue("") String query
    ) {
        PageRequest pageRequest = new PageRequestImpl(pageNumber, pageSize);
        Page<Author> authors = dao.list(pageRequest, query);
        GenericEntity<Page<Author>> authorWrapper = new GenericEntity<Page<Author>>(authors) {
        };
        return Response.ok(authorWrapper).build();
    }

    @RolesAllowed("ADMIN")
    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response update(
            @PathParam("id") long id,
            @Valid Author properties
    ) {
        dao.update(id, properties);
        return Response.ok().build();
    }

    @RolesAllowed("ADMIN")
    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") long id) {
        dao.delete(id);
        return Response.ok().build();
    }

}
