package com.oop.lab3.model;
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
// имя таблицы
@Table(name = "tg_payloads")
public class TgPayload {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // message/photo
    @Column(name = "payload_type", nullable = false, length = 255)
    private String payloadType;
    // данные
    @Column(name = "payload_data", nullable = false, length = 1500)
    private String payloadData;

    // Связь many-to-one к родительскому TgApi
    @ManyToOne(optional = false)
    // FK-колонка в tg_payloads, указывающая на tg_apis
    @JoinColumn(name = "tg_api_id", nullable = false)
    private TgApi tgApi;

    // пустой конструктор обязателен для гибернета
    protected TgPayload() {
    }

    public TgPayload(String payloadType, String payloadData, TgApi tgApi) {
        this.payloadType = payloadType;
        this.payloadData = payloadData;
        this.tgApi = tgApi;
    }
}
