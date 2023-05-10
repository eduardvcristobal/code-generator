package com.cict.prject_codegen.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

@Data
@Entity
@NoArgsConstructor
public class Address extends  BaseEntity{
    private String street;

    public Address(String street) {
        this.street = street;
    }

    /*@JsonIgnoreProperties({"address"})
    @OneToMany(mappedBy = "address", fetch = FetchType.LAZY)
    private List<Author> author;*/
}
