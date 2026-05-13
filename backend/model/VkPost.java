package com.oop.lab3.model;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

// гибернет для того, шоб не писать sql для каждого crud
// jpa для создания таблиц

// класс = таблица, поле = колонка, объект = строка в таблице

@Entity
// имя
@Table(name = "vk_posts")
// читаем напрямую
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class VkPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "text", nullable = false, length = 1000)
    private String text;

    @Column(name = "likes", nullable = false)
    private int likes;

    // many-to-one к родительскому VkApi
    @ManyToOne(optional = false)
    // fk vk_posts, указывающая на vk_apis
    @JoinColumn(name = "vk_api_id", nullable = false)
    @JsonIgnore
    private VkApi vkApi;

    // пустой конструктор обязателен для гибернета
    protected VkPost() {
    }

    // создание вк поста
    public VkPost(String text, int likes, VkApi vkApi) {
        this.text = text;
        this.likes = likes;
        this.vkApi = vkApi;
    }
}
