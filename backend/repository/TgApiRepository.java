package com.oop.lab3.repository;

import com.oop.lab3.model.TgApi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// для бд операций
@Repository
public interface TgApiRepository extends JpaRepository<TgApi, Long> {
}
