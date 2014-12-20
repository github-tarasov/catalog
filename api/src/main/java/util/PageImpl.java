package util;

import java.util.Iterator;
import java.util.List;

public class PageImpl<Entity> implements Page<Entity>, Iterable<Entity> {
    private List<Entity> content;
    private int pageNumber;
    private int pageSize;
    private long totalNumberOfElements;

    public PageImpl() {
    }

    public List<Entity> getContent() {
        return content;
    }

    public void setContent(List<Entity> content) {
        this.content = content;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        assert pageNumber >= 0;
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        assert pageSize >= 0;
        this.pageSize = pageSize;
    }

    public long getTotalNumberOfElements() {
        return totalNumberOfElements;
    }

    public void setTotalNumberOfElements(long totalNumberOfElements) {
        assert totalNumberOfElements >= 0;
        this.totalNumberOfElements = totalNumberOfElements;
    }

    @Override
    public int getTotalPages() {
        if (getTotalNumberOfElements() == 0) {
            return 0;
        }
        if (getPageSize() == 0) {
            return 1;
        }
        int totalPages = (int) (getTotalNumberOfElements() / getPageSize());
        if (getTotalNumberOfElements() % getPageSize() > 0) {
            totalPages++;
        }
        return totalPages;
    }

    @Override
    public boolean hasNextPage() {
        return (getPageNumber() < getTotalPages());
    }

    @Override
    public boolean hasPreviousPage() {
        return (getPageNumber() > 1);
    }

    @Override
    public boolean isFirstPage() {
        return (getPageNumber() == 1);
    }

    @Override
    public boolean isLastPage() {
        return (getPageNumber() == getTotalPages());
    }

    @Override
    public Iterator<Entity> iterator() {
        return content.iterator();
    }
}
