package model;

import javax.persistence.*;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(indexes = {@Index(columnList = "name")})
public class Author extends Model {

    @Column(nullable = false, length = 200)
    @Size(min = 2, max = 200, message = "The author's name must be between {min} and {max} chars long")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
