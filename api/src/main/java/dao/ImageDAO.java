package dao;

import model.Book;
import model.Image;
import util.MoveType;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ws.rs.NotFoundException;
import java.util.Collections;
import java.util.List;

@Stateless
public class ImageDAO {

    @EJB
    private DAO dao;

    @TransactionAttribute
    public Image create(long bookId, Image transientImage) {
        Book book = dao.find(Book.class, bookId);
        if (null == book) {
            throw new NotFoundException("Book id " + bookId + " not found");
        }
        transientImage.setBook(book);
        Image image = dao.create(transientImage);
        book.getImages().add(image);
        return image;
    }

    public Image find(long id) {
        Image image = dao.find(Image.class, id);
        if (null == image) {
            throw new NotFoundException("Image id " + id + " not found");
        }
        return image;
    }

    public List<Image> list(long bookId) {
        Book book = dao.find(Book.class, bookId);
        if (book == null) {
            throw new NotFoundException("Book with id " + bookId + " not found");
        }
        return Collections.unmodifiableList(book.getImages());
        // TODO: sort
    }

    @TransactionAttribute
    public Image changeSort(long id, MoveType moveType) {
        Image image = dao.find(Image.class, id);
        if (image == null) {
            throw new NotFoundException("Image id " + id + " not found");
        }
        //TODO: sort

        return image;
    }

    @TransactionAttribute
    public void delete(long id) {
        Image image = this.find(id);
        Book book = image.getBook();
        book.getImages().remove(image);
        dao.delete(Image.class, id);
    }

}
