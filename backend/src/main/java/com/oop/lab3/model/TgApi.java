package com.oop.lab3.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

// гибернет для того, шоб не писать sql для каждого crud
// jpa для создания таблиц

// класс = таблица, поле = колонка, объект = строка в таблице

@Entity // класс хранится в бд
@Table(name = "tg_apis") // имя таблицы
// гетеры (botToken/chatId/payloads)
@Getter
// сетеры
@Setter
public class TgApi extends Api {

    // токен бота
    @Column(name = "bot_token", nullable = false, length = 255) // колонка bot_token
    private String botToken;

    // чат айди
    @Column(name = "chat_id", nullable = false) // колонка chat_id
    private long chatId;

    // связь 1:n с таблицей tg_payloads
    @OneToMany(mappedBy = "tgApi", cascade = CascadeType.ALL, orphanRemoval = true) // один тг апи -> много payload
    @JsonIgnore // скрываем список payload до реализации отдельного endpoint-а payloads
    private List<TgPayload> payloads = new ArrayList<>();

    // пустой конструктор обязателен для гибернета
    protected TgApi() {
        super();
    }

    // фулл конструктор
    public TgApi(String botToken, long chatId) {
        // родительские поля
        super("TG", "https://api.telegram.org/bot");
        this.botToken = botToken;
        this.chatId = chatId;
    }
    // конструктор с токеном
    public TgApi(String botToken) {
        // родительские поля
        super("TG", "https://api.telegram.org/bot");
        this.botToken = botToken;
        this.chatId = 0L;
    }
    public List<String> sendMessage() {
        return List.of("хорошего дня!");
    }
    public List<String> sendPhoto() {
        return List.of("https://sun9-59.userapi.com/s/v1/ig2/or2do88N_s0TFCDi8kRge1iaKOVVtBLejaC-gN9KwsdWjQk1ql6ubIdKfqxvshgQS4cuHvGgb-QSNLTFKOXciM88.jpg?quality=95&as=32x31,48x47,72x70,108x105,160x156,240x234,360x351,480x468,540x527,640x624,720x702,961x937&from=bu&cs=961x0");
    }
}
