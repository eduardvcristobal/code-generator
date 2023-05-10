package com.cict.prject_codegen.dto;

import com.cict.prject_codegen.base.EntityRelater;
import com.cict.prject_codegen.model.Address;
import com.cict.prject_codegen.model.Author;
import com.cict.prject_codegen.model.Book;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.modelmapper.ModelMapper;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AuthorDTO extends BaseDTO<Author>  {
    private Long id;
    private String name;
    private String email;
    private Address address;

    private List<Book> books; //use for select query per column
    private List<BookDTO> booksDto; //user for insert/update query

    public AuthorDTO(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    //if no ID it will create, else it will update
    //use this if the OneToMany relationship is cascade=cascadeType.ALL
    //if you entity.getBooks() is null, it will be deleted.
    //if you entity.getBooks() then set the books, it will delete the missing ID and insert and update it will be updated
    //if you entity.getBooks() and you add empty Id it will be inserted
    @Override
    public Author to(Author entity, ModelMapper modelMapper) {
        EntityRelater.relate(entity, entity.getBooks(), (book, author) -> book.setAuthor(author));
        return entity;
    }
}