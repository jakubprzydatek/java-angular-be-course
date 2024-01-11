package com.example.javaangularbe.product;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
public class BookApi {

    @GetMapping("/books")
    public List<Book> get() {
        return List.of(new Book("Sienkiewicz", "Krzyżacy"), new Book("Mickiewicz", "Dziady"));
    }
}
