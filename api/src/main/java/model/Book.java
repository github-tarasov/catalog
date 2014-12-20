package model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

@Entity
@Table(indexes = {@Index(columnList = "title")})
public class Book extends Model {

    @Column(nullable = false, length = 400)
    @Size(min = 2, max = 400, message = "The book's title must be between {min} and {max} chars long")
    private String title;

    @Type(type = "text")
    @Column(nullable = false, length = 100000)
    @Size(min = 2, max = 100000, message = "The book's description must be between {min} and {max} chars long")
    private String description;

    @Column(nullable = false)
    private Calendar releaseDate;

    @Column(nullable = false, precision = 7, scale = 2)
    private BigDecimal price;

    @OneToMany(mappedBy = "book", fetch = FetchType.EAGER, cascade = {CascadeType.REMOVE, CascadeType.DETACH})
    @OrderColumn(name = "sort")
    @JsonManagedReference("book")
    private List<Image> images;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "BookAuthor",
            joinColumns = {@JoinColumn(name = "book_id")},
            inverseJoinColumns = {@JoinColumn(name = "author_id")})
    private Set<Author> authors;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Calendar getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Calendar releaseDate) {
        this.releaseDate = releaseDate;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public Set<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(Set<Author> authors) {
        this.authors = authors;
    }
}
