package com.oop.lab3.model;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;


@Entity 
@Table(name = "organizations") 
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // имя организации
    @Column(name = "name", nullable = false, length = 150)
    private String name;

    // связь 1:n с таблицей apis
    // одна организация -> много api
    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, orphanRemoval = true) 
    private List<Api> apis = new ArrayList<>();

    // пустой конструктор обязателен для гибернета
    protected Organization() {
    }

    // конструктор с именем организации
    public Organization(String name) {
        this.name = name;
    }
}
