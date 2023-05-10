package com.cict.prject_codegen.dao;

import com.cict.prject_codegen.model.Address;
import com.cict.prject_codegen.model.Author;
import org.springframework.stereotype.Repository;

@Repository()
public interface AddressRepository extends BaseRepository<Address, Long> {
}
