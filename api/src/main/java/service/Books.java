package service;

import com.fasterxml.jackson.annotation.JsonView;
import dao.BookDAO;
import model.Book;
import model.Image;
import util.*;
import util.application.Views;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;

@Path("v1/books")
@Produces(MediaType.APPLICATION_JSON)
public class Books {

    @EJB
    private BookDAO dao;

    @Context
    private UriInfo uriInfo;

    @RolesAllowed("ADMIN")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(
            @Valid Book properties
    ) {
        Book book = dao.create(properties);
        final URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(book.getId())).build();
        return Response.created(uri).build();
    }

    @PermitAll
    @GET
    @Path("{id}")
    @JsonView(Views.WithoutIgnoredGetters.class)
    public Response find(@PathParam("id") long id) {
        Book book = dao.find(id);
        for (Image image : book.getImages()) {
            image.setSmallHref(this.imageHref(book.getId(), image.getId(), ImageSize.SMALL));
            image.setBigHref(this.imageHref(book.getId(), image.getId(), ImageSize.BIG));
        }
        return Response.ok(book).build();
    }

    @PermitAll
    @GET
    @JsonView(Views.WithoutIgnoredGetters.class)
    public Response list(
            @QueryParam("pageNumber") @DefaultValue("1") int pageNumber,
            @QueryParam("pageSize") @DefaultValue("10") int pageSize,
            @QueryParam("query") @DefaultValue("") String query
    ) {
        PageRequest pageRequest = new PageRequestImpl(pageNumber, pageSize);
        Page<Book> books = dao.list(pageRequest, query);

        for (Book book : books.getContent()) {
            for (Image image : book.getImages()) {
                image.setSmallHref(this.imageHref(book.getId(), image.getId(), ImageSize.SMALL));
                image.setBigHref(this.imageHref(book.getId(), image.getId(), ImageSize.BIG));
            }
        }
        GenericEntity<Page<Book>> bookWrapper = new GenericEntity<Page<Book>>(books) {
        };
        return Response.ok(bookWrapper).build();
    }

    @RolesAllowed("ADMIN")
    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response update(
            @PathParam("id") long id,
            @Valid Book properties
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

    @RolesAllowed("ADMIN")
    @PUT
    @Path("{bookId}/authors/{authorId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addAuthorToBook(
            @PathParam("bookId") long bookId,
            @PathParam("authorId") long authorId
    ) {
        dao.addAuthor(bookId, authorId);
        return Response.ok().build();
    }

    @RolesAllowed("ADMIN")
    @DELETE
    @Path("{bookId}/authors/{authorId}")
    public Response deleteAuthorFromBook(
            @PathParam("bookId") long bookId,
            @PathParam("authorId") long authorId
    ) {
        dao.deleteAuthor(bookId, authorId);
        return Response.ok().build();
    }

    private URI imageHref(long bookId, long imageId, ImageSize size) {
        return uriInfo.getBaseUriBuilder().path(Images.class).path(Images.class, "find")
                .resolveTemplate("bookId", bookId)
                .resolveTemplate("imageId", imageId)
                .queryParam("size", size)
                .build();
    }
}
