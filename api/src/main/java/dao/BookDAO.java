package dao;

import model.Author;
import model.Book;
import util.Page;
import util.PageRequest;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ws.rs.NotFoundException;

@Stateless
public class BookDAO {

    @EJB
    private DAO dao;

    @TransactionAttribute
    public Book create(Book transientBook) {
        return dao.create(transientBook);
    }

    public Book find(long id) {
        Book book = dao.find(Book.class, id);
        if (null == book) {
            throw new NotFoundException("Book id " + id + " not found");
        }
        return book;
    }

    public Page<Book> list(PageRequest pageRequest, String query) {
        return dao.list(Book.class, pageRequest, query);
    }

    @TransactionAttribute
    public Book update(long id, Book transientBook) {
        this.find(id);
        transientBook.setId(id);
        return dao.update(transientBook);
    }

    @TransactionAttribute
    public void delete(long id) {
        this.find(id);
        dao.delete(Book.class, id);
    }

    @TransactionAttribute
    public void addAuthor(long bookId, long authorId) {
        Book book = this.find(bookId);
        Author author = dao.find(Author.class, authorId);
        if (author == null) {
            throw new NotFoundException("Author id " + authorId + " not found");
        }
        book.getAuthors().add(author);
    }

    @TransactionAttribute
    public void deleteAuthor(long bookId, long authorId) {
        Book book = this.find(bookId);
        Author author = dao.find(Author.class, authorId);
        if (author == null) {
            throw new NotFoundException("Author id " + authorId + " not found");
        }
        for (Author a : book.getAuthors()) {
            if (a.getId() == authorId) {
                book.getAuthors().remove(a);
                break;
            }
        }
    }


}
