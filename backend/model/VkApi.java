package com.oop.lab3.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

// jpa сущность вк апи
@Entity // класс хранится в бд
@Table(name = "vk_apis") // имя таблицы
// читаем напрямую
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class VkApi extends Api {

    // версия апи вк
    @Column(name = "version", nullable = false, length = 50) // колонка version
    private String version;

    // вк токен
    @Column(name = "token", nullable = false, length = 255) // колонка token
    private String token;

    // связь 1:n с таблицей vk_posts
    @OneToMany(mappedBy = "vkApi", cascade = CascadeType.ALL, orphanRemoval = true) // один вк апи -> много постов
    @JsonIgnore // скрываем список постов до реализации отдельного endpoint-а posts
    private List<VkPost> posts = new ArrayList<>();

    // пустой конструктор обязателен для гибернета
    protected VkApi() {
        super();
    }

    // конструктор со всем
    public VkApi(String version, String token) {
        // родительские поля
        super("VK", "https://api.vk.com/method");
        this.version = version;
        this.token = token;
    }

    // конструктор с токеном
    public VkApi(String token) {
        // родительские поля
        super("VK", "https://api.vk.com/method");
        this.version = "5.199";
        this.token = token;
    }
    // полиморфизм инициализацию для вк
    @Override
    public String initialize() {
        return "Initializing API... VK => name=" + name
                + ", baseUrl=" + baseUrl
                + ", version=" + version
                + ", token=" + token;
    }
    // обновление полей вк без сеттеров
    public void updateVkData(String version, String token) {
        this.version = version;
        this.token = token;
    }

    @JsonIgnore
    public List<String> getText() {
        return List.of(
                "я *вот приду после учёбы и успею кучу дел сделать* я после учёбы:",
                "Эх",
                "Снежки",
                "Вкусна",
                "Это моя прелесть",
                "Мчу тебя цмокать",
                "Боба",
                "Когда-нибудь высплюсь"
        );
    }

    @JsonIgnore
    public List<Integer> getLike() {
        return List.of(23000, 23, 157, 209, 440, 487, 282, 504);
    }
}
