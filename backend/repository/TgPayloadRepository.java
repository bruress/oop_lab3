package com.oop.lab3.repository;

import com.oop.lab3.model.TgPayload;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TgPayloadRepository extends JpaRepository<TgPayload, Long> {
    List<TgPayload> findByTgApiId(Long tgApiId);
}
