package com.cict.prject_codegen.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class Book extends BaseEntity  {
    /*@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;*/

    private String title;

    @JsonIgnoreProperties({"books"})
    @ManyToOne // dont cascade since you are adding details. cascade should be on the OneToMany side
                // Many To One By Default in will Fetch Eager
    @JoinColumn(name = "author_id", nullable = false)
    //Many Details To One Header
    private Author author;

    public Book(String title, Author author) {
        this.title = title;
        this.author = author;
    }

    //List of updatable fields
    public void updateFrom(Book entity) {
        this.title = entity.getTitle();
        this.author = entity.getAuthor();
    }
}
