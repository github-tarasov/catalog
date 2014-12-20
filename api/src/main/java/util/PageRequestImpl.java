package util;

public class PageRequestImpl implements PageRequest {
    private int pageNumber;
    private int pageSize;

    public PageRequestImpl(int pageNumber, int pageSize) {
        if (pageNumber <= 0) {
            throw new IllegalArgumentException("pageNumber must be greater than 0");
        }
        if (pageSize <= 0) {
            throw new IllegalArgumentException("pageSize must be greater than 0");
        }
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }


    @Override
    public int getPageNumber() {
        return pageNumber;
    }

    @Override
    public int getPageSize() {
        return pageSize;
    }
}
