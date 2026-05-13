package com.oop.lab3.controller;
import com.oop.lab3.model.TgApi;
import com.oop.lab3.model.TgPayload;
import com.oop.lab3.service.TgApiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/tgapis")
public class TgApiController {

    // ссылку на сервис, тк бизнес логика там
    private final TgApiService tgApiService;

    // конструктор сохранения сервиса для использования его в методах контроллера
    public TgApiController(TgApiService tgApiService) {
        this.tgApiService = tgApiService;
    }


    // get - список всех tg api
    @GetMapping
    public List<TgApi> getAll() {
        return tgApiService.getAll();
    }

    // get - по id
    @GetMapping("/{id}")
    public ResponseEntity<TgApi> getById(@PathVariable long id) {
        return tgApiService.getById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // post для фулл конструктора
    @PostMapping("/full")
    public ResponseEntity<Map<String, Long>> createFull(@RequestBody CreateTgFullRequest request) {
        long id = tgApiService.createFull(request.botToken(), request.chatId());
        return ResponseEntity.ok(Map.of("id", id));
    }

    // post для токена
    @PostMapping("/token")
    public ResponseEntity<Map<String, Long>> createToken(@RequestBody CreateTgTokenRequest request) {
        long id = tgApiService.createToken(request.botToken());
        return ResponseEntity.ok(Map.of("id", id));
    }

    // post для chatId
    @PostMapping("/chat")
    public ResponseEntity<Map<String, Long>> createChat(@RequestBody CreateTgChatRequest request) {
        long id = tgApiService.createChat(request.chatId());
        return ResponseEntity.ok(Map.of("id", id));
    }

    // delete по id
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable long id) {
        boolean deleted = tgApiService.deleteById(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    // put для редактирования токена и chatId по id
    @PutMapping("/{id}")
    public ResponseEntity<TgApi> updateById(@PathVariable long id, @RequestBody UpdateTgRequest request) {
        Optional<TgApi> updated = tgApiService.updateById(id, request.botToken(), request.chatId());
        return updated.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // инициализация родителя у поста
    @GetMapping("/{id}/initialize")
    public ResponseEntity<String> initialize(@PathVariable long id) {
        Optional<String> result = tgApiService.initialize(id);
        return result.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // fetch у родителя у поста
    @GetMapping("/{id}/fetchdata/{command}")
    public ResponseEntity<String> fetchData(@PathVariable long id, @PathVariable String command) {
        Optional<String> result = tgApiService.fetchData(id, command);
        return result.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // вызов команды
    @GetMapping("/{id}/run/{command}")
    public ResponseEntity<Map<String, Object>> run(@PathVariable long id, @PathVariable String command) {
        Optional<Object> result = tgApiService.run(id, command);
        return result.map(value -> ResponseEntity.ok(Map.of("result", value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // список payload конкретного tg api
    @GetMapping("/{id}/payloads")
    public List<TgPayload> getPayloads(@PathVariable long id) {
        return tgApiService.getPayloads(id);
    }

    // добавить payload к конкретному tg api
    @PostMapping("/{id}/payloads")
    public ResponseEntity<TgPayload> addPayload(@PathVariable long id, @RequestBody CreateTgPayloadRequest request) {
        Optional<TgPayload> created = tgApiService.addPayload(id, request.payloadType(), request.payloadData());
        return created.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // удалить payload по id payload
    @DeleteMapping("/payloads/{payloadId}")
    public ResponseEntity<Void> deletePayload(@PathVariable long payloadId) {
        boolean deleted = tgApiService.deletePayloadById(payloadId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    // шоб знать шо читать конструкторам
    public record CreateTgFullRequest(String botToken, long chatId) {
    }
    public record CreateTgTokenRequest(String botToken) {
    }
    public record CreateTgChatRequest(long chatId) {
    }
    public record UpdateTgRequest(String botToken, long chatId) {
    }
    public record CreateTgPayloadRequest(String payloadType, String payloadData) {
    }
}
