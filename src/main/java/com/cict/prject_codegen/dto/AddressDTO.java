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
public class AddressDTO extends BaseDTO<Address>  {
    private String street;

    public AddressDTO(Long id, String street) {
        setId(id);
        this.street = street;
    }
}