package com.oop.lab3.controller;

import com.oop.lab3.model.Organization;
import com.oop.lab3.service.OrganizationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/organizations")
public class OrganizationController {

    // ссылку на сервис, тк бизнес логика там
    private final OrganizationService organizationService;

    // конструктор сохранения сервиса для использования его в методах контроллера
    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    // get - список всех организаций
    @GetMapping
    public List<Organization> getAll() {
        return organizationService.getAll();
    }

    // post для создания организации по имени
    @PostMapping
    public ResponseEntity<Organization> create(@RequestBody CreateOrganizationRequest request) {
        Organization created = organizationService.create(request.name());
        return ResponseEntity.ok(created);
    }

    // delete по id
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable long id) {
        boolean deleted = organizationService.deleteById(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    // привязать vk api к организации
    @PostMapping("/{id}/vk/{vkApiId}")
    public ResponseEntity<Organization> attachVkApi(@PathVariable long id, @PathVariable long vkApiId) {
        Optional<Organization> updated = organizationService.attachVkApi(id, vkApiId);
        return updated.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // привязать tg api к организации
    @PostMapping("/{id}/tg/{tgApiId}")
    public ResponseEntity<Organization> attachTgApi(@PathVariable long id, @PathVariable long tgApiId) {
        Optional<Organization> updated = organizationService.attachTgApi(id, tgApiId);
        return updated.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // шоб знать шо читать конструкторам
    public record CreateOrganizationRequest(String name) {
    }
}
