package dao;

import model.Author;
import util.Page;
import util.PageRequest;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ws.rs.NotFoundException;

@Stateless
public class AuthorDAO {

    @EJB
    private DAO dao;

    @TransactionAttribute
    public Author create(Author transientAuthor) {
        return dao.create(transientAuthor);
    }

    public Author find(long id) {
        Author author = dao.find(Author.class, id);
        if (null == author) {
            throw new NotFoundException("Author id " + id + " not found");
        }
        return author;
    }

    public Page<Author> list(PageRequest pageRequest, String query) {
        return dao.list(Author.class, pageRequest, query);
    }

    @TransactionAttribute
    public Author update(long id, Author transientAuthor) {
        this.find(id);
        transientAuthor.setId(id);
        return dao.update(transientAuthor);
    }

    @TransactionAttribute
    public void delete(long id) {
        this.find(id);
        dao.delete(Author.class, id);
    }

}
