package com.cict.prject_codegen.dto;

import com.cict.prject_codegen.base.EntityRelater;
import com.cict.prject_codegen.model.Address;
import com.cict.prject_codegen.model.Author;
import com.cict.prject_codegen.model.Book;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.modelmapper.ModelMapper;

@Getter
@Setter
@NoArgsConstructor
public class BookDTO extends BaseDTO<Book> {
    private Long id;
    private String title;
    private Author author;

    public BookDTO(Long id, String title, Author author) {
        this.id = id;
        this.title = title;
        this.author = author;
    }

    public BookDTO(Long id, String title) {
        this.id = id;
        this.title = title;
    }

    @Override
    public Book to(Book entity, ModelMapper modelMapper) {
        EntityRelater.relate(entity.getAuthor(), entity.getAuthor().getBooks(), (book, author) ->
        {
            book.setAuthor(author);
            author.setAddress(new Address("test"));

        }); //for book and author

        return entity;
    }
}