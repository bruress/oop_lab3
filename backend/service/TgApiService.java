package com.oop.lab3.service;
import com.oop.lab3.model.TgApi;
import com.oop.lab3.model.TgPayload;
import com.oop.lab3.repository.TgApiRepository;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class TgApiService {

    // репозиторий для операций с таблицей tg_apis
    private final TgApiRepository tgApiRepository;

    // фабрика entity manager для чтения идентификатора сохраненной сущности
    private final EntityManagerFactory entityManagerFactory;

    // конструктор для внедрения repository через Spring DI
    public TgApiService(TgApiRepository tgApiRepository, EntityManagerFactory entityManagerFactory) {
        this.tgApiRepository = tgApiRepository;
        this.entityManagerFactory = entityManagerFactory;
    }

    // получить всех tgAPI
    public List<TgApi> getAll() {
        return tgApiRepository.findAll();
    }

    // по id
    public Optional<TgApi> getById(long id) {
        return tgApiRepository.findById(id);
    }

    // пустой конструктор
    public long createFull(String botToken, long chatId) {
        String safeToken = botToken == null ? "" : botToken;
        TgApi saved = tgApiRepository.save(new TgApi(safeToken, chatId));
        return (Long) entityManagerFactory.getPersistenceUnitUtil().getIdentifier(saved);
    }

    // нет токена
    public long createToken(String botToken) {
        String safeToken = botToken == null ? "" : botToken;
        TgApi saved = tgApiRepository.save(new TgApi(safeToken));
        return (Long) entityManagerFactory.getPersistenceUnitUtil().getIdentifier(saved);
    }

    // нет часа айди
    public long createChat(long chatId) {
        TgApi saved = tgApiRepository.save(new TgApi("NULL", chatId));
        return (Long) entityManagerFactory.getPersistenceUnitUtil().getIdentifier(saved);
    }

    // удалить по id
    public boolean deleteById(long id) {
        if (!tgApiRepository.existsById(id)) {
            return false;
        }
        tgApiRepository.deleteById(id);
        return true;
    }

    // обновить токен и чат по id
    public Optional<TgApi> updateById(long id, String botToken, long chatId) {
        Optional<TgApi> apiOptional = tgApiRepository.findById(id);
        if (apiOptional.isEmpty()) {
            return Optional.empty();
        }
        TgApi api = apiOptional.get();
        // null токен не сохраняем, чтоб не словить not null в бд
        String safeToken = botToken == null ? "" : botToken;
        // обновляем поля через предметный метод
        api.updateTgData(safeToken, chatId);
        return Optional.of(tgApiRepository.save(api));
    }

    // получить payload конкретного tg api
    public List<TgPayload> getPayloads(long id) {
        Optional<TgApi> apiOptional = tgApiRepository.findById(id);
        if (apiOptional.isEmpty()) return List.of();
        return apiOptional.get().payloadsList();
    }

    // добавить payload конкретному tg api
    public Optional<TgPayload> addPayload(long id, String payloadType, String payloadData) {
        // ищем родительский tg api по id
        Optional<TgApi> apiOptional = tgApiRepository.findById(id);
        // если не нашли, возвращаем пустой результат
        if (apiOptional.isEmpty()) return Optional.empty();
        // если тип пустой, ставим дефолт message
        String safeType = (payloadType == null || payloadType.isBlank()) ? "message" : payloadType;
        // если данные пустые, ставим безопасное значение
        String safeData = (payloadData == null || payloadData.isBlank()) ? "пусто" : payloadData;
        // добавляем данные в список родителя и сохраняем родителя
        TgApi api = apiOptional.get();
        TgPayload saved = api.addPayloadItem(safeType, safeData);
        tgApiRepository.save(api);
        // возвращаем созданную запись
        return Optional.of(saved);
    }

    // удалить payload по его id
    public boolean deletePayloadById(long payloadId) {
        List<TgApi> all = tgApiRepository.findAll();
        for (TgApi api : all) {
            if (api.removePayloadById(payloadId)) {
                tgApiRepository.save(api);
                return true;
            }
        }
        return false;
    }

    // родительская инициализация
    public Optional<String> initialize(long id) {
        return tgApiRepository.findById(id).map(TgApi::initialize);
    }

    // родительская загрузка данных
    public Optional<String> fetchData(long id, String command) {
        return tgApiRepository.findById(id).map(api -> api.fetchData(command));
    }

    // вызвать предметный метод по команде
    // в плюсах у меня тут были if-ы
    public Optional<Object> run(long id, String command) {
        Optional<TgApi> apiOptional = tgApiRepository.findById(id);
        if (apiOptional.isEmpty()) return Optional.empty();
        TgApi api = apiOptional.get();
        // выбираем конкретный метод
        return switch (command) {
            case "sendMessage" -> Optional.of(api.sendMessage());
            case "sendPhoto" -> Optional.of(api.sendPhoto());
            default -> Optional.of("Команда не поддерживается");
        };
    }
}
