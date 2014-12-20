package service;

import dao.ImageDAO;
import model.Image;
import org.imgscalr.Scalr;
import util.application.BackendServiceBean;
import util.ImageSize;
import util.MoveType;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.imageio.ImageIO;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;

@Path("v1/books/{bookId}/images")
@Produces(MediaType.APPLICATION_JSON)
public class Images {

    @EJB
    private ImageDAO dao;

    @EJB
    private BackendServiceBean service;

    @Context
    private javax.ws.rs.core.UriInfo uriInfo;

    @RolesAllowed("ADMIN")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(
            @PathParam("bookId") long bookId,
            byte[] source
    ) throws IOException {
        Image transientImage = new Image();

        // Create thumbnails
        InputStream in = new ByteArrayInputStream(source);
        BufferedImage bufferedSource = ImageIO.read(in);
        // TODO: config
        for (ImageSize size : ImageSize.values()) {
            int widthResize = (ImageSize.BIG == size ?
                    Integer.valueOf(service.getProperties().getProperty("maxBigThumbnailWidth")) :
                    Integer.valueOf(service.getProperties().getProperty("maxSmallThumbnailWidth"))
            );
            int heightResize = (ImageSize.BIG == size ?
                    Integer.valueOf(service.getProperties().getProperty("maxBigThumbnailHeight")) :
                    Integer.valueOf(service.getProperties().getProperty("maxSmallThumbnailHeight"))
            );

            BufferedImage thumbnailResize =
                    Scalr.resize(bufferedSource, Scalr.Method.AUTOMATIC, Scalr.Mode.AUTOMATIC,
                            widthResize, heightResize, Scalr.OP_ANTIALIAS);
            ByteArrayOutputStream outResize = new ByteArrayOutputStream();

            ImageIO.write(thumbnailResize, "jpg", outResize);
            outResize.flush();
            if (ImageSize.BIG == size) {
                transientImage.setBig(outResize.toByteArray());
            } else {
                transientImage.setSmall(outResize.toByteArray());
            }
        }

        Image image = dao.create(bookId, transientImage);
        final URI uri = uriInfo.getAbsolutePathBuilder().resolveTemplate("book_id", image.getBook().getId()).path(String.valueOf(image.getId())).build();
        return Response.created(uri).build();
    }

    @PermitAll
    @GET
    @Path("{imageId}")
    @Produces("image/jpeg")
    public Response find(
            @PathParam("bookId") long bookId,
            @PathParam("imageId") long imageId,
            @QueryParam("size") @DefaultValue("SMALL") ImageSize size
    ) {
        Image image = dao.find(imageId);
        if (null == image || image.getBook().getId() != bookId) {
            throw new NotFoundException("Image with id " + imageId + " and book_id " + bookId + " not found");
        }
        final byte[] bytes = (size == ImageSize.BIG ? image.getBig() : image.getSmall());
        StreamingOutput responseEntity = new StreamingOutput() {
            @Override
            public void write(OutputStream output) throws IOException, WebApplicationException {
                output.write(bytes);
                output.flush();
            }
        };
        return Response.ok(responseEntity).build();
        // TODO: Add header for cache
    }

    @RolesAllowed("ADMIN")
    @PUT
    @Path("{imageId}")
    public Response changeSort(
            @PathParam("bookId") long bookId,
            @PathParam("imageId") long imageId,
            @QueryParam("moveType") MoveType moveType

    ) {
        Image image = dao.find(imageId);
        if (null == image || image.getBook().getId() != bookId) {
            throw new NotFoundException("Image with id " + imageId + " and book_id " + bookId + " not found");
        }
        dao.changeSort(imageId, moveType);
        return Response.ok().build();
    }

    @RolesAllowed("ADMIN")
    @DELETE
    @Path("{imageId}")
    public Response delete(
            @PathParam("bookId") long bookId,
            @PathParam("imageId") long imageId
    ) {
        Image image = dao.find(imageId);
        if (null == image || image.getBook().getId() != bookId) {
            throw new NotFoundException("Image with id " + imageId + " and book_id " + bookId + " not found");
        }
        dao.delete(imageId);
        return Response.ok().build();
    }
}
