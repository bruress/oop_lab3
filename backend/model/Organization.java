package com.oop.lab3.model;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;


@Entity 
@Table(name = "organizations") 
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // имя организации
    @Column(name = "name", nullable = false, length = 150)
    private String name;

    // связь 1:n с таблицей apis
    // одна организация -> много api
    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Api> apis = new ArrayList<>();

    // пустой конструктор обязателен для гибернета
    protected Organization() {
    }

    // конструктор с именем организации
    public Organization(String name) {
        this.name = name;
    }

    // добавить api в организацию и связать в обе стороны
    public void addApi(Api api) {
        this.apis.add(api);
        api.bindOrganization(this);
    }
}
