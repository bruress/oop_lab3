package com.oop.lab3.model;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

// гибернет для того, шоб не писать sql для каждого crud
// jpa для создания таблиц

// класс = таблица, поле = колонка, объект = строка в таблице

@Entity // етат класс хранится в бд
@Table(name = "apis")  // имя таблицы
// читаем напрямую
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)

// наследование vkapis, tgapis по id через join
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Api {

    @Id // первичный ключ
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", nullable = false, length = 100)
    protected String name;
    @Column(name = "base_url", nullable = false, length = 255)
    protected String baseUrl;

    // ссылка на организацию (связь не по наследованию)
    @ManyToOne(optional = true) // много api может принадлежать одной организации
    @JoinColumn(name = "organization_id", nullable = true) // fk на organizations.id
    @JsonIgnore // избавиться от рекурсии
    protected Organization organization;

    // пустой конструктор обязателен для гибернета
    protected Api() {
    }
    protected Api(String name, String baseUrl) {
        this.name = name;
        this.baseUrl = baseUrl;
    }
    public String initialize() {
        return "Initializing API...";
    }
    public String fetchData(String command) {
        return "Загрузка данных...";
    }
}
