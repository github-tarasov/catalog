package dao;

import util.Page;
import util.PageImpl;
import util.PageRequest;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Stateless
public class DAO {

    @PersistenceContext(unitName = "catalog")
    private EntityManager em;

    public <T> T create(T t) {
        em.persist(t);
        return t;
    }

    public <T> T find(Class<T> tClass, long id) {
        return em.find(tClass, id);
    }

    public <T> Page<T> list(Class<T> tClass, PageRequest pageRequest, String query) {
        // TODO: add query
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();

        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        countQuery.select(criteriaBuilder.count(countQuery.from(tClass)));
        long totalNumberOfElements = em.createQuery(countQuery).getSingleResult();

        long firstResult = (pageRequest.getPageNumber() - 1) * pageRequest.getPageSize();
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(tClass);
        Root<T> from = criteriaQuery.from(tClass);
        CriteriaQuery<T> select = criteriaQuery.select(from);
        TypedQuery<T> typedQuery = em.createQuery(select);
        typedQuery.setFirstResult((int) firstResult); // TODO: long => int :(
        typedQuery.setMaxResults(pageRequest.getPageSize());
        List<T> content = typedQuery.getResultList();

        Page<T> page = new PageImpl<T>();
        page.setContent(content);
        page.setPageNumber(pageRequest.getPageNumber());
        page.setPageSize(pageRequest.getPageSize());
        page.setTotalNumberOfElements(totalNumberOfElements);

        return page;
    }

    public <T> T update(T t) {
        return em.merge(t);
    }

    public <T> void delete(Class<T> tClass, long id) {
        em.remove(em.find(tClass, id));
    }

}
