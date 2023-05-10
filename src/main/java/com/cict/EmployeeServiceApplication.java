package com.cict;


import com.cict.prject_codegen.dao.AddressRepository;
import com.cict.prject_codegen.dao.AuthorRepository;
import com.cict.prject_codegen.dao.BookRepository;
import com.cict.prject_codegen.model.Address;
import com.cict.prject_codegen.model.Author;
import com.cict.prject_codegen.model.Book;
import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class EmployeeServiceApplication {


	public static void main(String[] args) {
		SpringApplication.run(EmployeeServiceApplication.class, args);
	}

	@Bean
	public ModelMapper modelMapper() {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setAmbiguityIgnored(true);
		modelMapper.getConfiguration().setSkipNullEnabled(true);
		return modelMapper;
	}


	@Bean
	public CommandLineRunner commandLineRunner(AuthorRepository authorRepository, BookRepository bookRepository, AddressRepository addressRepository) {
		return args -> {
			System.out.println("Hello World");

			Address address = new Address("Address 1");
			addressRepository.save(address);

			Author author1 = new Author("John Doe", "johndoe@example.com");
			author1.setAddress(address);
			authorRepository.save(author1);

			Author author2 = new Author("Jane Doe", "Janedoe@example.com");
			authorRepository.save(author2);

			Book book1 = new Book("the great escape 1", author1);
			bookRepository.save(book1);

			Book book2 = new Book("the great escape 2", author1);
			bookRepository.save(book2);

			bookRepository.save(book2);
		};
	}
}
