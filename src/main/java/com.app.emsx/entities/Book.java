package com.app.emsx.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "books")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String genre;
    
    @Column(name = "publication_date", columnDefinition = "DATE")
    private LocalDate publicationDate;
    
    private String editorial;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private Author author;

    @OneToOne(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private Inventory inventory;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private List<Loan> loans = new ArrayList<>();
}


