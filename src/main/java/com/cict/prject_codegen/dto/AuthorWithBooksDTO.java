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
public class AuthorWithBooksDTO extends BaseDTO<Author>  {
    private Long id;
    private String name;
    private String email;
    private AddressDTO address;
    private List<BookDTO> books; //user for insert/update query

    public AuthorWithBooksDTO(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public AuthorWithBooksDTO from (Author author) {
        this.id = author.getId();
        this.name = author.getName();
        this.email = author.getEmail();

        return this;
    }
}