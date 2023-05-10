package com.cict.prject_codegen.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Author extends BaseEntity {
    private String name;
    private String email;

    @OneToOne
    @JoinColumn (name = "address_id")
    private Address address;

    //OneToMany should not have @JoinColumn. Coz there is no foreign key in the table. Just use mappedBy
    @JsonIgnoreProperties({"author"})
    @OneToMany(mappedBy = "author", cascade=CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Book> books;

    public Author(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public void addBook(Book book) {
        book.setAuthor(this);
        books.add(book);
    }
}
