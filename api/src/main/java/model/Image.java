package model;

import com.fasterxml.jackson.annotation.*;
import util.application.Views;


import javax.persistence.*;
import java.net.URI;

@Entity
public class Image extends Model {

    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "book_id")
    @JsonBackReference("book")
    private Book book;

    @Column(nullable = false)
    @JsonIgnore
    private int sort;

    // TASK:
    //  - Store images in DB
    //  - Use "LAZY" loading images in API
    //  - Images can uploading when create Image

    @Basic(fetch = FetchType.LAZY)
    @Lob
    @Column(nullable = false)
    @JsonView(Views.IgnoreGetter.class)
    private byte[] small;

    @Basic(fetch = FetchType.LAZY)
    @Lob
    @Column(nullable = false)
    @JsonView(Views.IgnoreGetter.class)
    private byte[] big;

    @Transient
    private URI smallHref;

    @Transient
    private URI bigHref;

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }


    // JsonIgnoreGetter
    //@JsonIgnore
    public byte[] getSmall() { return small; }

    //@JsonIgnore(false)
    public void setSmall(byte[] small) {
        this.small = small;
    }

    // JsonIgnoreGetter
    //@JsonIgnore
    public byte[] getBig() { return big; }

    //@JsonIgnore(false)
    public void setBig(byte[] big) {
        this.big = big;
    }


    public URI getSmallHref() {
        return smallHref;
    }

    // JsonIgnoreSetter
    public void setSmallHref(URI smallHref) {
        this.smallHref = smallHref;
    }

    public URI getBigHref() {
        return bigHref;
    }

    // JsonIgnoreSetter
    public void setBigHref(URI bigHref) {
        this.bigHref = bigHref;
    }
}
