package com.oop.lab3.controller;
import com.oop.lab3.model.VkApi;
import com.oop.lab3.service.VkApiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/vkapis")
public class VkApiController {

    // ссылку на сервис, тк бизнес логика там
    private final VkApiService vkApiService;

    // конструктор сохранения сервиса для использования его в методах контроллера
    public VkApiController(VkApiService vkApiService) {
        this.vkApiService = vkApiService;
    }

    // get - список всех vk api
    @GetMapping
    public List<VkApi> getAll() {
        return vkApiService.getAll();
    }

    // get - по id
    @GetMapping("/{id}")
    public ResponseEntity<VkApi> getById(@PathVariable long id) {
        return vkApiService.getById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // post для фулл конструктора 
    @PostMapping("/full")
    public ResponseEntity<Map<String, Long>> createFull(@RequestBody CreateVkFullRequest request) {
        long id = vkApiService.createFull(request.version(), request.token());
        return ResponseEntity.ok(Map.of("id", id));
    }

    // post для токена
    @PostMapping("/token")
    public ResponseEntity<Map<String, Long>> createToken(@RequestBody CreateVkTokenRequest request) {
        long id = vkApiService.createToken(request.token());
        return ResponseEntity.ok(Map.of("id", id));
    }

    // post для дефолтного конструктора
    @PostMapping("/default")
    public ResponseEntity<Map<String, Long>> createDefault() {
        long id = vkApiService.createDefault();
        return ResponseEntity.ok(Map.of("id", id));
    }

    // delete по id
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable long id) {
        boolean deleted = vkApiService.deleteById(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    // инициализация родителя у поста
    @GetMapping("/{id}/initialize")
    public ResponseEntity<String> initialize(@PathVariable long id) {
        Optional<String> result = vkApiService.initialize(id);
        return result.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // fetch у родителя у поста
    @GetMapping("/{id}/fetchdata/{command}")
    public ResponseEntity<String> fetchData(@PathVariable long id, @PathVariable String command) {
        Optional<String> result = vkApiService.fetchData(id, command);
        return result.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // вызов команды
    @GetMapping("/{id}/run/{command}")
    public ResponseEntity<Map<String, Object>> run(@PathVariable long id, @PathVariable String command) {
        Optional<Object> result = vkApiService.run(id, command);
        return result.map(value -> ResponseEntity.ok(Map.of("result", value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // шоб знать шо читать конструкторам
    public record CreateVkFullRequest(String version, String token) {
    }
    public record CreateVkTokenRequest(String token) {
    }
}
