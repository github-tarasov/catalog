package util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Iterator;
import java.util.List;

@JsonIgnoreProperties({"totalPages", "firstPage", "lastPage"})
public interface Page<Entity> extends Iterable<Entity> {
    List<Entity> getContent();

    void setContent(List<Entity> content);

    int getPageNumber();

    void setPageNumber(int pageNumber);

    int getPageSize();

    void setPageSize(int pageSize);

    long getTotalNumberOfElements();

    void setTotalNumberOfElements(long totalNumberOfElements);

    int getTotalPages();

    boolean hasNextPage();

    boolean hasPreviousPage();

    boolean isFirstPage();

    boolean isLastPage();

    @Override
    Iterator<Entity> iterator();
}
